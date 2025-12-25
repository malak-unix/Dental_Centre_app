package ma.dentalTech.mvc.controllers.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;

public interface DashboardController {

    void showDashboard(Long utilisateurId) throws ControllerException;

    DashboardDTO getDashboardDTO(Long utilisateurId) throws ControllerException;
}
