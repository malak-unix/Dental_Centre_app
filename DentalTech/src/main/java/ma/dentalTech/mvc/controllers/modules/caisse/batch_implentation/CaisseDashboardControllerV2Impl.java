package ma.dentalTech.mvc.controllers.modules.caisse.batch_implentation;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseDashboardControllerV2;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardServiceV2;

@RequiredArgsConstructor
public class CaisseDashboardControllerV2Impl implements CaisseDashboardControllerV2 {

    private final CaisseDashboardServiceV2 service;

    @Override
    public CaisseDashboardResponseDTO getDashboard(CaisseDashboardRequestDTO request,
                                                   LibelleRole role,
                                                   Long currentUserId) {
        return service.getDashboard(request, role, currentUserId);
    }
}
