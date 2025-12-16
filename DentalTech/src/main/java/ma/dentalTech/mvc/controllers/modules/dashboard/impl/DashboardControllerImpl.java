package ma.dentalTech.mvc.controllers.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.ui.modules.dashboard.DashboardConsoleUI;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

public class DashboardControllerImpl implements DashboardController {

    private final DashboardService dashboardService;

    public DashboardControllerImpl(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public void showDashboard(Long utilisateurId) throws ControllerException {
        try {
            DashboardDTO dto = dashboardService.getDashboard(utilisateurId);
            DashboardConsoleUI.render(dto);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de l'affichage du dashboard.", e);
        }
    }
    @Override
    public DashboardDTO getDashboardDTO(Long utilisateurId) throws ControllerException {
        try {
            return dashboardService.getDashboard(utilisateurId);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur getDashboardDTO.", e);
        }
    }

}
