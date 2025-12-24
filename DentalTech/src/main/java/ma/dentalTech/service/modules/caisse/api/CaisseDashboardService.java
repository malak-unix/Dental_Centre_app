package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;
import ma.dentalTech.entities.enums.LibelleRole;

public interface CaisseDashboardService {

    CaisseDashboardResponseDTO getDashboard(
            CaisseDashboardRequestDTO request,
            LibelleRole role,
            Long currentUserId
    );
}
