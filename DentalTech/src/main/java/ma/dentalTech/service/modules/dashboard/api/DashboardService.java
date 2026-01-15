package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;

public interface DashboardService {
    DashboardDTO getDashboard(Long utilisateurId) throws ServiceException;

    // Direct access for lazy loading or specific panel refresh
    ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO buildAdmin();

    ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO buildMedecin(Long medecinId);

    ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO buildSecretaire(Long utilisateurId);
}
