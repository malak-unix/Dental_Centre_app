package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.DashboardAdminDTO;
import ma.dentalTech.mvc.dto.DashboardMedecinDTO;
import ma.dentalTech.mvc.dto.DashboardSecretaireDTO;

public interface DashboardService {

    /**
     * Dashboard vue Secrétaire :
     * - stats caisse du jour
     * - rdv / file d'attente
     * - alertes / notifications
     */
    DashboardSecretaireDTO getDashboardSecretaire(Long secretaireId) throws ServiceException;

    /**
     * Dashboard vue Médecin :
     * - rdv du jour / consultations
     * - actes réalisés
     * - situation financière de ses patients (vue synthétique)
     */
    DashboardMedecinDTO getDashboardMedecin(Long medecinId) throws ServiceException;

    /**
     * Dashboard vue Admin :
     * - stats globales du cabinet
     * - stats financières globales
     * - infos utilisateurs / sécurité
     */
    DashboardAdminDTO getDashboardAdmin(Long adminId) throws ServiceException;
}
