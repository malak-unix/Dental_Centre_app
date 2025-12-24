package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;

public interface DashboardService {
    DashboardDTO getDashboard(Long utilisateurId) throws ServiceException;
}
