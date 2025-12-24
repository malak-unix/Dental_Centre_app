package ma.dentalTech.mvc.controllers.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ControllerException;

public interface DashboardControllerWithDTO {
    DashboardDTO getDashboardDTO(Long utilisateurId) throws ControllerException;
}
