package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;

import java.time.LocalDate;

public interface CaisseDashboardController {

    // ================== ADMIN ==================
    void showAdminDashboardToday() throws ServiceException;

    void showAdminDashboardForPeriod(LocalDate start, LocalDate end) throws ServiceException;

    // ================== MEDECIN ==================
    void showMedecinDashboardToday(Long medecinId) throws ServiceException;

    void showMedecinDashboardForPeriod(Long medecinId, LocalDate start, LocalDate end) throws ServiceException;

    // ================== SECRETAIRE ==================
    void showSecretaireDashboardToday(Long secretaireId) throws ServiceException;

    void showSecretaireDashboardForPeriod(Long secretaireId, LocalDate start, LocalDate end) throws ServiceException;
}
