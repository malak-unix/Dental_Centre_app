package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.users.*; // Import de tous vos nouveaux DTO
import java.util.List;

public interface UserManagementService {
    // On change les paramètres ici pour correspondre à vos fichiers DTO
    UserSummaryDTO createAdmin(CreateAdminRequestDTO request);
    UserSummaryDTO createMedecin(CreateMedecinRequestDTO request);
    UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request);

    UserSummaryDTO getUserById(Long id);
    List<UserSummaryDTO> getAllUsers();
    List<UserSummaryDTO> searchUsersByKeyword(String keyword);
    UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request);

    void assignRoleToUser(Long utilisateurId, LibelleRole roleType);
    void removeRoleFromUser(Long utilisateurId, LibelleRole roleType);

    /**
     * Suppression logique d'un utilisateur depuis l'interface d'administration.
     * Actuellement on effectue une suppression simple en base (deleteById).
     */
    void deleteUser(Long id);
}