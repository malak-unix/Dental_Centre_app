package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
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

    @Override
    public CaisseFactureRowDTO create(FactureCreateDTO dto) {
        validateCreate(dto);

        Facture f = Facture.builder()
                .consultationId(dto.getConsultationId())
                .dateFacture(dto.getDateFacture())
                .totalFacture(nz(dto.getTotalFacture()))
                .totalPaye(BigDecimal.ZERO)
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
        if (dto == null || dto.getMontant() == null) throw new IllegalArgumentException("montant obligatoire");
        if (dto.getMontant().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("montant invalide");

        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");

        BigDecimal total = nz(f.getTotalFacture());
        BigDecimal payeActuel = nz(f.getTotalPaye());
        BigDecimal nouveauPaye = payeActuel.add(dto.getMontant());

        if (nouveauPaye.compareTo(total) > 0) nouveauPaye = total;

        f.setTotalPaye(nouveauPaye);

        // statut
        if (nouveauPaye.compareTo(BigDecimal.ZERO) == 0) f.setStatut(StatutFacture.NON_PAYEE);
        else if (nouveauPaye.compareTo(total) >= 0) f.setStatut(StatutFacture.PAYEE);
        else f.setStatut(StatutFacture.PARTIEL);

        factureRepository.update(f);
        return toRow(f);
    }

    @Override
    public FacturePrintDTO getForPrint(Long factureId) {
        if (factureId == null) throw new IllegalArgumentException("factureId obligatoire");
        Facture f = factureRepository.findById(factureId);
        if (f == null) throw new IllegalArgumentException("Facture introuvable");

        return FacturePrintDTO.builder()
                .numeroFacture(String.valueOf(f.getId()))
                .dateFacture(f.getDateFacture())
                .consultationId(f.getConsultationId())
                .totalFacture(f.getTotalFacture())
                .totalPaye(f.getTotalPaye())
                .reste(calcReste(f))
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                .build();
    }

    @Override
    public byte[] exportPdf(Long factureId) {
        FacturePrintDTO dto = getForPrint(factureId);
        return facturePdfService.generateFacturePdf(dto);
    }

    // =========================
    // Helpers / Validations
    // =========================
    private void validateCreate(FactureCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO obligatoire");
        if (dto.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire");
        if (dto.getDateFacture() == null) throw new IllegalArgumentException("dateFacture obligatoire");
        if (dto.getTotalFacture() == null || dto.getTotalFacture().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("totalFacture invalide");
    }

    private CaisseFactureRowDTO toRow(Facture f) {
        return CaisseFactureRowDTO.builder()
                .factureId(f.getId())
                .consultationId(f.getConsultationId())
                .dateFacture(f.getDateFacture())
                .totalFacture(f.getTotalFacture())
                .totalPaye(f.getTotalPaye())
                .reste(calcReste(f)) // ne dépend pas de la colonne calculée DB
                .statut(f.getStatut() == null ? null : f.getStatut().name())
                // Actions: la caisse dashboard décide selon rôle
                .canView(true)
                .canPrint(true)
                .canPay(false)
                .canCancel(false)
                .build();
    }

    private BigDecimal calcReste(Facture f) {
        return nz(f.getTotalFacture()).subtract(nz(f.getTotalPaye()));
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
