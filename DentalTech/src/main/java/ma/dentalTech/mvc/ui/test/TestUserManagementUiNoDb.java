package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController;
import ma.dentalTech.mvc.controllers.modules.users.impl.UserManagementControllerImpl;
import ma.dentalTech.mvc.ui.modules.users.UserManagementFrame;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import javax.swing.*;

public class TestUserManagementUiNoDb {

    public static void main(String[] args) {

        // Service fake: on veut juste ouvrir l'UI sans crash
        UserManagementService fakeService = new UserManagementService() {
            @Override public ma.dentalTech.mvc.dto.users.UserSummaryDTO createAdmin(ma.dentalTech.mvc.dto.users.CreateAdminRequestDTO request) { throw new RuntimeException("DB not connected"); }
            @Override public ma.dentalTech.mvc.dto.users.UserSummaryDTO createMedecin(ma.dentalTech.mvc.dto.users.CreateMedecinRequestDTO request) { throw new RuntimeException("DB not connected"); }
            @Override public ma.dentalTech.mvc.dto.users.UserSummaryDTO createSecretaire(ma.dentalTech.mvc.dto.users.CreateSecretaireRequestDTO request) { throw new RuntimeException("DB not connected"); }
            @Override public ma.dentalTech.mvc.dto.users.UserSummaryDTO getUserById(Long id) { return null; }
            @Override public java.util.List<ma.dentalTech.mvc.dto.users.UserSummaryDTO> getAllUsers() { return java.util.List.of(); }
            @Override public java.util.List<ma.dentalTech.mvc.dto.users.UserSummaryDTO> searchUsersByKeyword(String keyword) { return java.util.List.of(); }
            @Override public ma.dentalTech.mvc.dto.users.UserSummaryDTO updateUserProfile(Long id, ma.dentalTech.mvc.dto.users.UserSaveRequestDTO request) { return null; }
            @Override public void assignRoleToUser(Long utilisateurId, ma.dentalTech.entities.enums.LibelleRole roleType) {}
            @Override public void removeRoleFromUser(Long utilisateurId, ma.dentalTech.entities.enums.LibelleRole roleType) {}
        };

        UserManagementController ctrl = new UserManagementControllerImpl(fakeService);

        SwingUtilities.invokeLater(() -> {
            UserManagementFrame f = new UserManagementFrame(ctrl);
            f.setVisible(true);
        });
    }
}
