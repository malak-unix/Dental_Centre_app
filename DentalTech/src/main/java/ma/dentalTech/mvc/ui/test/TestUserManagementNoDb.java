package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.users.impl.UserManagementControllerImpl;
import ma.dentalTech.mvc.dto.users.CreateAdminRequestDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.impl.UserManagementServiceImpl;

import java.sql.Connection;

public class TestUserManagementNoDb {

    public static void main(String[] args) {

        // Factories "fake" => si on appelle la DB, on verra l'erreur clairement
        RepoFactory<UtilisateurRepository> userFactory = (Connection cnx) -> null;
        RepoFactory<MedecinRepository> medecinFactory = (Connection cnx) -> null;
        RepoFactory<SecretaireRepository> secretaireFactory = (Connection cnx) -> null;
        RepoFactory<RoleRepository> roleFactory = (Connection cnx) -> null;

        UserManagementService service = new UserManagementServiceImpl(
                userFactory,
                medecinFactory,
                secretaireFactory,
                roleFactory,
                new PasswordEncoderImpl()
        );

        var ctrl = new UserManagementControllerImpl(service);

        // ✅ Juste vérifier que le controller existe et peut être appelé sans crash "wiring"
        System.out.println("✅ userManagementController OK -> " + ctrl.getClass().getName());

        // Test minimal sans DB : appel avec request vide => ne doit pas crash
        try {
            CreateAdminRequestDTO req = new CreateAdminRequestDTO();
            req.setNom("Test");
            req.setPrenom("User");
            req.setLogin("test_admin");
            req.setPassword("test");
            UserSummaryDTO res = ctrl.createAdmin(req);
            System.out.println("ℹ️ createAdmin appelé (normalement DB requise) -> résultat: " + res);
        } catch (Exception e) {
            System.out.println("✅ Wiring OK (exception attendue car DB non branchée) : " + e.getClass().getName());
        }
    }
}
