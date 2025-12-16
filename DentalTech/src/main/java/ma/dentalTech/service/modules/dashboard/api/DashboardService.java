package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.DashboardDTO;

public interface DashboardService {
    DashboardDTO getDashboard(Long utilisateurId) throws ServiceException;
}
