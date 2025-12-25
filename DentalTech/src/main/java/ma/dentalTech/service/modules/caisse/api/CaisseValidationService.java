package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.*;

public interface CaisseValidationService {

    // ===== FACTURE =====
    void validateFactureCreate(FactureCreateDTO dto);
    void validateFactureUpdate(FactureUpdateDTO dto);
    void validatePaiement(FacturePaiementDTO dto);

    // ===== CHARGES / REVENUS =====
    void validateChargeCreate(ChargeCreateDTO dto);
    void validateChargeUpdate(ChargeUpdateDTO dto);

    void validateRevenuCreate(RevenuCreateDTO dto);
    void validateRevenuUpdate(RevenuUpdateDTO dto);

    // ===== DASHBOARD / SF =====
    void validateDashboardRequest(CaisseDashboardRequestDTO dto, LibelleRole role, Long currentUserId);
}
