package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.dto.caisse.CaisseChartDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardServiceV2;
import ma.dentalTech.service.modules.caisse.api.CaisseValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CaisseDashboardServiceV2Impl implements CaisseDashboardServiceV2 {

    private final FactureRepository factureRepository;
    private final RevenuesRepository revenuesRepository;
    private final ChargesRepository chargesRepository;

    private final CaisseValidationService validation =
            ApplicationContext.getBean(CaisseValidationService.class);

    @Override
    public CaisseDashboardResponseDTO getDashboard(CaisseDashboardRequestDTO req, LibelleRole role, Long currentUserId) {

        validation.validateDashboardRequest(req, role, currentUserId);

        // ✅ Toujours en LocalDateTime pour les repos
        LocalDateTime start = toStart(req == null ? null : req.getDateDebut());
        LocalDateTime end = toEnd(req == null ? null : req.getDateFin());

        List<Facture> factures = factureRepository.findByDateBetween(start, end);
        factures = applyStatutFilter(factures, req == null ? null : req.getStatut());
        factures = applySearchFilter(factures, req == null ? null : req.getSearch());

        List<CaisseFactureRowDTO> rows = factures.stream()
                .map(f -> toRowDTO(f, role))
                .collect(Collectors.toList());

        double totalFactures = sumDouble(factures, Facture::getTotalFacture);
        double totalRegle = sumDouble(factures, Facture::getTotalPaye);
        double totalNonRegle = Math.max(0.0, totalFactures - totalRegle);

        double totalRevenus = nvl(revenuesRepository.calculateTotalRevenus(start, end));
        double totalCharges = nvl(chargesRepository.calculateTotalCharges(start, end));
        double soldeNet = totalRevenus - totalCharges;

        // ✅ Chart réel (6 derniers mois), sans toucher à ApplicationContext / beans
        CaisseChartDTO chart = buildRevenusVsCharges6MonthsChart(req == null ? null : req.getDateFin());

        return CaisseDashboardResponseDTO.builder()
                .filters(req)
                .totalFactures(totalFactures)
                .totalRegle(totalRegle)
                .totalNonRegle(totalNonRegle)
                .totalRevenus(totalRevenus)
                .totalCharges(totalCharges)
                .soldeNet(soldeNet)
                .chart(chart)
                .factures(rows)
                .build();
    }

    private CaisseChartDTO buildRevenusVsCharges6MonthsChart(LocalDate dateFinUi) {
        LocalDate base = (dateFinUi == null) ? LocalDate.now() : dateFinUi;

        YearMonth endMonth = YearMonth.from(base);
        YearMonth startMonth = endMonth.minusMonths(5);

        List<String> labels = new ArrayList<>();
        List<Double> revenus = new ArrayList<>();
        List<Double> charges = new ArrayList<>();

        YearMonth cur = startMonth;
        while (!cur.isAfter(endMonth)) {

            String label = cur.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + cur.getYear();
            labels.add(label);

            LocalDateTime from = cur.atDay(1).atStartOfDay();
            LocalDateTime to = cur.atEndOfMonth().atTime(LocalTime.MAX);

            Double r = revenuesRepository.calculateTotalRevenus(from, to);
            Double c = chargesRepository.calculateTotalCharges(from, to);

            revenus.add(nvl(r));
            charges.add(nvl(c));

            cur = cur.plusMonths(1);
        }

        return CaisseChartDTO.builder()
                .title("Revenus vs Charges")
                .labels(labels)
                .revenus(revenus)
                .charges(charges)
                .build();
    }

    private CaisseFactureRowDTO toRowDTO(Facture f, LibelleRole role) {
        boolean isPayee = (f.getStatut() == StatutFacture.PAYEE);

        boolean canPay = (role == LibelleRole.ADMIN || role == LibelleRole.SECRETAIRE) && !isPayee;
        boolean canCancel = (role == LibelleRole.ADMIN) && !isPayee;

        Double total = nvlObj(f.getTotalFacture());
        Double paye = nvlObj(f.getTotalPaye());
        Double reste = Math.max(0.0, total - paye);

        return CaisseFactureRowDTO.builder()
                .factureId(f.getId())
                .consultationId(f.getConsultationId())
                .dateFacture(f.getDateFacture())
                .totalFacture(BigDecimal.valueOf(total))
                .totalPaye(BigDecimal.valueOf(paye))
                .reste(BigDecimal.valueOf(reste))
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .canView(true)
                .canPrint(true)
                .canPay(canPay)
                .canCancel(canCancel)
                .build();
    }

    private List<Facture> applyStatutFilter(List<Facture> list, String statutUi) {
        if (statutUi == null || statutUi.isBlank()) return list;

        String s = statutUi.trim().toUpperCase(Locale.ROOT);
        if ("TOUTES".equals(s) || "TOUT".equals(s)) return list;

        // ✅ Impayés = NON_PAYEE + PARTIEL
        if ("IMPAYEE".equals(s)) {
            return list.stream()
                    .filter(f -> f.getStatut() == StatutFacture.NON_PAYEE || f.getStatut() == StatutFacture.PARTIEL)
                    .collect(Collectors.toList());
        }

        if ("PAYEE".equals(s)) {
            return list.stream()
                    .filter(f -> f.getStatut() == StatutFacture.PAYEE)
                    .collect(Collectors.toList());
        }

        if ("ANNULEE".equals(s)) {
            return List.of(); // ton enum n’a pas ANNULEE
        }

        return list;
    }

    private List<Facture> applySearchFilter(List<Facture> list, String search) {
        if (search == null || search.isBlank()) return list;
        String q = search.trim().toLowerCase(Locale.ROOT);

        return list.stream().filter(f -> {
            String id = f.getId() == null ? "" : String.valueOf(f.getId());
            String consult = f.getConsultationId() == null ? "" : String.valueOf(f.getConsultationId());
            return id.contains(q) || consult.contains(q);
        }).collect(Collectors.toList());
    }

    private double sumDouble(List<Facture> list, java.util.function.Function<Facture, Double> getter) {
        return list.stream()
                .map(getter)
                .map(this::nvlObj)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double nvl(Double v) { return v == null ? 0.0 : v; }
    private Double nvlObj(Double v) { return v == null ? 0.0 : v; }

    // ✅ Defaults safe (si UI envoie null)
    private LocalDateTime toStart(LocalDate d) {
        return d == null ? LocalDate.now().minusMonths(1).atStartOfDay() : d.atStartOfDay();
    }

    private LocalDateTime toEnd(LocalDate d) {
        return d == null ? LocalDate.now().atTime(LocalTime.MAX) : d.atTime(LocalTime.MAX);
    }
}
