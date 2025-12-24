package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardServiceV2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CaisseDashboardServiceV2Impl implements CaisseDashboardServiceV2 {

    private final FactureRepository factureRepository;
    private final RevenuesRepository revenuesRepository;
    private final ChargesRepository chargesRepository;

    @Override
    public CaisseDashboardResponseDTO getDashboard(CaisseDashboardRequestDTO req, LibelleRole role, Long currentUserId) {

        if (req == null) throw new IllegalArgumentException("request obligatoire");
        LocalDateTime start = toStart(req.getDateDebut());
        LocalDateTime end = toEnd(req.getDateFin());

        if (start == null || end == null) throw new IllegalArgumentException("dateDebut/dateFin obligatoires");
        if (end.isBefore(start)) throw new IllegalArgumentException("dateFin doit être après dateDebut");

        // 1) Récupérer les factures sur période
        List<Facture> factures = factureRepository.findByDateBetween(start, end);

        // 2) Filtrer par statut (UI: TOUTES | PAYEE | IMPAYEE | ANNULEE)
        factures = applyStatutFilter(factures, req.getStatut());

        // 3) Filtrer par search (sans patient dans ton schéma facture => on filtre sur id / consultationId)
        factures = applySearchFilter(factures, req.getSearch());

        // 4) Construire les lignes (DTO) + actions selon rôle
        List<CaisseFactureRowDTO> rows = factures.stream()
                .map(f -> toRowDTO(f, role))
                .collect(Collectors.toList());

        // 5) Totaux (calculés sur factures filtrées -> correspond à Figma)
        double totalFactures = sumFacturesTotal(factures);
        double totalRegle = sumFacturesPaye(factures);
        double totalNonRegle = Math.max(0.0, totalFactures - totalRegle);

        // Revenus/Charges: repos (période). Si tu veux aussi filtrer par “medecin”, il faudra ajouter critère en DB.
        double totalRevenus = nvl(revenuesRepository.calculateTotalRevenus(start, end));
        double totalCharges = nvl(chargesRepository.calculateTotalCharges(start, end));
        double soldeNet = totalRevenus - totalCharges;

        // Chart DTO (pour l’instant juste métadonnées : l’UI convertira en JFreeChart)
        CaisseChartDTO chart = CaisseChartDTO.builder()
                .title("Revenus vs Charges")
                .build();

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

    // =========================
    // Mapping Facture -> RowDTO
    // =========================
    private CaisseFactureRowDTO toRowDTO(Facture f, LibelleRole role) {
        boolean isPayee = (f.getStatut() == StatutFacture.PAYEE);

        boolean canPay = (role == LibelleRole.ADMIN || role == LibelleRole.SECRETAIRE) && !isPayee;
        boolean canCancel = (role == LibelleRole.ADMIN) && !isPayee;

        return CaisseFactureRowDTO.builder()
                .factureId(f.getId())
                .consultationId(f.getConsultationId())
                .dateFacture(f.getDateFacture())
                .totalFacture(f.getTotalFacture())
                .totalPaye(f.getTotalPaye())
                .reste(calcReste(f))
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .canView(true)
                .canPrint(true)
                .canPay(canPay)
                .canCancel(canCancel)
                .build();
    }

    private BigDecimal calcReste(Facture f) {
        return nz(f.getTotalFacture()).subtract(nz(f.getTotalPaye()));
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // =========================
    // Filters
    // =========================
    private List<Facture> applyStatutFilter(List<Facture> list, String statutUi) {
        if (statutUi == null || statutUi.isBlank()) return list;

        String s = statutUi.trim().toUpperCase(Locale.ROOT);
        if ("TOUTES".equals(s) || "TOUT".equals(s)) return list;

        // UI "IMPAYEE" => NON_PAYEE + PARTIEL
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

        // "ANNULEE" : ton enum Facture n’a pas ANNULEE -> on retourne vide (cohérent)
        if ("ANNULEE".equals(s)) {
            return List.of();
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

    // =========================
    // Totals
    // =========================
    private double sumFacturesTotal(List<Facture> list) {
        return list.stream()
                .map(Facture::getTotalFacture)
                .map(this::nz)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    private double sumFacturesPaye(List<Facture> list) {
        return list.stream()
                .map(Facture::getTotalPaye)
                .map(this::nz)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    private double nvl(Double v) { return v == null ? 0.0 : v; }

    private LocalDateTime toStart(LocalDate d) {
        return d == null ? null : d.atStartOfDay();
    }

    private LocalDateTime toEnd(LocalDate d) {
        return d == null ? null : d.atTime(LocalTime.of(23, 59, 59));
    }
}
