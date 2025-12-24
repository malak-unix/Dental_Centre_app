package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;

public interface CaisseDashboardControllerV2 {

    CaisseDashboardResponseDTO getDashboard(CaisseDashboardRequestDTO request,
                                            LibelleRole role,
                                            Long currentUserId);
}
