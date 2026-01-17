package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseValidationService;
import ma.dentalTech.service.modules.caisse.api.FacturePdfService;
import ma.dentalTech.service.modules.caisse.api.FactureServiceV2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FactureServiceV2Impl implements FactureServiceV2 {

    private final FactureRepository factureRepository;
    private final FacturePdfService facturePdfService;

    private final CaisseValidationService validation =
            ApplicationContext.getBean(CaisseValidationService.class);

    @Override
    public CaisseFactureRowDTO create(FactureCreateDTO dto) {
        validation.validateFactureCreate(dto);

        Facture f = Facture.builder()
                .consultationId(dto.getConsultationId())
                .dateFacture(dto.getDateFacture())
                .totalFacture(toDouble(dto.getTotalFacture())) // ✅ entity Double
                .totalPaye(0.0)
                .statut(StatutFacture.NON_PAYEE)
                .build();

        factureRepository.create(f);
        return toRow(f);
    }

    @Override
    public CaisseFactureRowDTO getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        Facture f = factureRepository.findById(id);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");
        return toRow(f);
    }

    @Override
    public List<CaisseFactureRowDTO> listBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end obligatoires");
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être après start");

        return factureRepository.findByDateBetween(start, end)
                .stream()
                .map(this::toRow)
                .collect(Collectors.toList());
    }

    @Override
    public CaisseFactureRowDTO payer(Long factureId, FacturePaiementDTO dto) {
        if (factureId == null) throw new IllegalArgumentException("factureId obligatoire");
        validation.validatePaiement(dto);

        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable id=" + factureId);

        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double montant = dto.getMontant().doubleValue();

        if (montant <= 0) throw new IllegalArgumentException("Montant doit être > 0");

        double newPaye = paye + montant;
        if (newPaye > total + 0.0001) {
            throw new IllegalArgumentException("Paiement dépasse le total facture");
        }

        f.setTotalPaye(newPaye);

        if (Math.abs(newPaye - total) < 0.0001) {
            f.setStatut(StatutFacture.PAYEE);
        } else if (newPaye > 0) {
            f.setStatut(StatutFacture.PARTIEL);
        } else {
            f.setStatut(StatutFacture.NON_PAYEE);
        }

        factureRepository.update(f);
        return toRow(f);
    }

    @Override
    public FacturePrintDTO getForPrint(Long factureId) {
        if (factureId == null) throw new IllegalArgumentException("factureId obligatoire");
        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");

        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double reste = Math.max(0.0, total - paye);

        return FacturePrintDTO.builder()
                .numeroFacture(String.valueOf(f.getId()))
                .dateFacture(f.getDateFacture())
                .consultationId(f.getConsultationId())
                .totalFacture(BigDecimal.valueOf(total))
                .totalPaye(BigDecimal.valueOf(paye))
                .reste(BigDecimal.valueOf(reste))
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .build();
    }

    @Override
    public byte[] exportPdf(Long factureId) {
        FacturePrintDTO dto = getForPrint(factureId);
        return facturePdfService.generateFacturePdf(dto);
    }

    // ========================= Helpers =========================

    private CaisseFactureRowDTO toRow(Facture f) {
        double total = nvl(f.getTotalFacture());
        double paye = nvl(f.getTotalPaye());
        double reste = Math.max(0.0, total - paye);

        return CaisseFactureRowDTO.builder()
                .factureId(f.getId())
                .consultationId(f.getConsultationId())
                .dateFacture(f.getDateFacture())
                .totalFacture(BigDecimal.valueOf(total))
                .totalPaye(BigDecimal.valueOf(paye))
                .reste(BigDecimal.valueOf(reste))
                .montant(total)
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .canView(true)
                .canPrint(true)
                .canPay(f.getStatut() != StatutFacture.PAYEE)
                .canCancel(false)
                .build();
    }

    private double nvl(Double v) { return v == null ? 0.0 : v; }
    private Double toDouble(BigDecimal bd) { return bd == null ? null : bd.doubleValue(); }
}
