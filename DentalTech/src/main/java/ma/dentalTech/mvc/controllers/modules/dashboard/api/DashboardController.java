package ma.dentalTech.mvc.controllers.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ControllerException;

public interface DashboardController {

    /**
     * Affiche le dashboard pour un utilisateur (selon son rôle).
     */
    void showDashboard(Long utilisateurId) throws ControllerException;

    DashboardDTO getDashboardDTO(Long utilisateurId) throws ControllerException;
}
