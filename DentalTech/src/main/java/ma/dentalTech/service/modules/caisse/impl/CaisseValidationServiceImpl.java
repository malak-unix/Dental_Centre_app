package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.service.modules.caisse.api.CaisseValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CaisseValidationServiceImpl implements CaisseValidationService {

    private void fail(String msg) {
        throw new RuntimeException(new ValidationException(msg));
    }

    private void require(boolean cond, String msg) {
        if (!cond) fail(msg);
    }

    private void requireNotNull(Object o, String msg) {
        if (o == null) fail(msg);
    }

    private void requireNotBlank(String s, String msg) {
        if (s == null || s.isBlank()) fail(msg);
    }

    private void requirePositive(BigDecimal v, String msg) {
        requireNotNull(v, msg);
        require(v.compareTo(BigDecimal.ZERO) > 0, msg);
    }

    private void requirePositive(Double v, String msg) {
        requireNotNull(v, msg);
        require(v > 0.0, msg);
    }

    // ========================= FACTURE =========================

    @Override
    public void validateFactureCreate(FactureCreateDTO dto) {
        requireNotNull(dto, "FactureCreateDTO est null");
        requireNotNull(dto.getConsultationId(), "consultationId est obligatoire");
        requireNotNull(dto.getDateFacture(), "dateFacture est obligatoire");
        requireNotNull(dto.getTotalFacture(), "totalFacture est obligatoire");
        require(dto.getTotalFacture().compareTo(BigDecimal.ZERO) > 0, "totalFacture doit etre > 0");
    }

    @Override
    public void validateFactureUpdate(FactureUpdateDTO dto) {
        requireNotNull(dto, "FactureUpdateDTO est null");

        List<FactureLineCreateDTO> lignes = dto.getLignes();
        requireNotNull(lignes, "lignes est obligatoire");
        require(lignes.size() > 0, "lignes doit contenir au moins 1 ligne");

        for (int i = 0; i < lignes.size(); i++) {
            FactureLineCreateDTO l = lignes.get(i);
            requireNotNull(l, "ligne[" + i + "] est null");
            requireNotBlank(l.getDesignation(), "designation obligatoire (ligne[" + i + "])" );
            requireNotNull(l.getQuantite(), "quantite obligatoire (ligne[" + i + "])" );
            require(l.getQuantite() > 0, "quantite doit etre > 0 (ligne[" + i + "])" );

            requirePositive(l.getPrixUnitaire(), "prixUnitaire doit etre > 0 (ligne[" + i + "])" );
        }
    }

    @Override
    public void validatePaiement(FacturePaiementDTO dto) {
        requireNotNull(dto, "FacturePaiementDTO est null");
        requirePositive(dto.getMontant(), "montant paiement doit etre > 0");
    }

    // ========================= CHARGES =========================

    @Override
    public void validateChargeCreate(ChargeCreateDTO dto) {
        requireNotNull(dto, "ChargeCreateDTO est null");
        requireNotNull(dto.getCabinetId(), "cabinetId est obligatoire");
        requireNotBlank(dto.getTitre(), "titre est obligatoire");
        requirePositive(dto.getMontant(), "montant charge doit etre > 0");
        requireNotNull(dto.getDateCharge(), "dateCharge est obligatoire");
    }

    @Override
    public void validateChargeUpdate(ChargeUpdateDTO dto) {
        requireNotNull(dto, "ChargeUpdateDTO est null");
        requireNotBlank(dto.getTitre(), "titre est obligatoire");
        requirePositive(dto.getMontant(), "montant charge doit etre > 0");
        requireNotNull(dto.getDateCharge(), "dateCharge est obligatoire");
    }

    // ========================= REVENUS =========================

    @Override
    public void validateRevenuCreate(RevenuCreateDTO dto) {
        requireNotNull(dto, "RevenuCreateDTO est null");
        requireNotNull(dto.getCabinetId(), "cabinetId est obligatoire");
        requireNotBlank(dto.getTitre(), "titre est obligatoire");
        requirePositive(dto.getMontant(), "montant revenu doit etre > 0");
        requireNotNull(dto.getDateRevenu(), "dateRevenu est obligatoire");
    }

    @Override
    public void validateRevenuUpdate(RevenuUpdateDTO dto) {
        requireNotNull(dto, "RevenuUpdateDTO est null");
        requireNotBlank(dto.getTitre(), "titre est obligatoire");
        requirePositive(dto.getMontant(), "montant revenu doit etre > 0");
        requireNotNull(dto.getDateRevenu(), "dateRevenu est obligatoire");
    }

    // ========================= DASHBOARD CAISSE =========================

    @Override
    public void validateDashboardRequest(CaisseDashboardRequestDTO dto, LibelleRole role, Long currentUserId) {
        requireNotNull(dto, "CaisseDashboardRequestDTO est null");
        requireNotNull(role, "role est obligatoire");
        requireNotNull(currentUserId, "currentUserId est obligatoire");

        LocalDate deb = dto.getDateDebut();
        LocalDate fin = dto.getDateFin();

        requireNotNull(deb, "dateDebut est obligatoire");
        requireNotNull(fin, "dateFin est obligatoire");
        require(!fin.isBefore(deb), "dateFin doit etre >= dateDebut");
    }
}
