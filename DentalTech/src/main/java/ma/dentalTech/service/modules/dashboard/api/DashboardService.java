package ma.dentalTech.service.modules.dashboard.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.DashboardAdminDTO;
import ma.dentalTech.mvc.dto.DashboardMedecinDTO;
import ma.dentalTech.mvc.dto.DashboardSecretaireDTO;

public interface DashboardService {

    DashboardSecretaireDTO getDashboardSecretaire(Long secretaireId) throws ServiceException;

    DashboardMedecinDTO getDashboardMedecin(Long medecinId) throws ServiceException;

    DashboardAdminDTO getDashboardAdmin(Long adminId) throws ServiceException;
}
