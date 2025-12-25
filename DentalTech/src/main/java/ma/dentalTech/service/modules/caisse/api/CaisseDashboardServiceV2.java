package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;

public interface CaisseDashboardServiceV2 {
    CaisseDashboardResponseDTO getDashboard(CaisseDashboardRequestDTO req, LibelleRole role, Long currentUserId);
}
