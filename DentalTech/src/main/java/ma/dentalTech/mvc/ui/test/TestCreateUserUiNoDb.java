package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.mvc.ui.modules.users.CreateUserFrame;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class TestCreateUserUiNoDb {

    public static void main(String[] args) {

        // ✅ Fake controller (pas de DB)
        UserManagementController fakeController = new UserManagementController() {

            @Override public UserSummaryDTO createAdmin(CreateAdminRequestDTO request) {
                System.out.println("CREATE ADMIN -> " + request);
                return UserSummaryDTO.builder().id(1L).nom(request.getNom()).prenom(request.getPrenom())
                        .login(request.getLogin()).actif(true).build();
            }

            @Override public UserSummaryDTO createMedecin(CreateMedecinRequestDTO request) {
                System.out.println("CREATE MEDECIN -> " + request);
                return UserSummaryDTO.builder().id(2L).nom(request.getNom()).prenom(request.getPrenom())
                        .login(request.getLogin()).actif(true).build();
            }

            @Override public UserSummaryDTO createSecretaire(CreateSecretaireRequestDTO request) {
                System.out.println("CREATE SECRETAIRE -> " + request);
                return UserSummaryDTO.builder().id(3L).nom(request.getNom()).prenom(request.getPrenom())
                        .login(request.getLogin()).actif(true).build();
            }

            @Override public UserSummaryDTO getUserById(Long id) { return null; }
            @Override public List<UserSummaryDTO> getAllUsers() { return Collections.emptyList(); }
            @Override public List<UserSummaryDTO> searchUsersByKeyword(String keyword) { return Collections.emptyList(); }
            @Override public UserSummaryDTO updateUserProfile(Long id, UserSaveRequestDTO request) { return null; }
            @Override public void assignRoleToUser(Long utilisateurId, LibelleRole roleType) {}
            @Override public void removeRoleFromUser(Long utilisateurId, LibelleRole roleType) {}
        };

        SwingUtilities.invokeLater(() -> {
            CreateUserFrame frame = new CreateUserFrame(fakeController);
            frame.setVisible(true);
        });
    }
}
