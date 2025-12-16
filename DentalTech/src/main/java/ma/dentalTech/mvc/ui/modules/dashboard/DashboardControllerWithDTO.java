package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.dto.DashboardDTO;

public interface DashboardControllerWithDTO {
    DashboardDTO getDashboardDTO(Long utilisateurId) throws ControllerException;
}
