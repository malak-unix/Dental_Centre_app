package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.users.*;
import java.util.List;

public interface UserManagementService {
    UserSummaryDTO createAdmin(CreateAdminRequestDTO request);

    UserSummaryDTO createMedecin(CreateMedecinRequestDTO request);

    UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request);

    UserSummaryDTO getUserById(Long id);

    List<UserSummaryDTO> getAllUsers();

    List<UserSummaryDTO> searchUsersByKeyword(String keyword);

    UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request);

    void assignRoleToUser(Long utilisateurId, LibelleRole roleType);

    void removeRoleFromUser(Long utilisateurId, LibelleRole roleType);

    void activateUser(Long utilisateurId);

    void deactivateUser(Long utilisateurId);
}