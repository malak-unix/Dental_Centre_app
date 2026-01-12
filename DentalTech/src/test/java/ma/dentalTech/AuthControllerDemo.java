package ma.dentalTech;

import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthControllerImpl;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl;
import ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.impl.AuthServiceImpl;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;

public class AuthControllerDemo {

    public static void main(String[] args) {

        // Factories (comme dans tes services)
        RepoFactory<UtilisateurRepository> userRepoFactory = UtilisateurRepositoryImpl::new;
        RepoFactory<RoleRepository> roleRepoFactory = RoleRepositoryImpl::new;

        // Dépendances Auth
        LoginFormValidator validator = new CredentialsValidatorImpl();
        PasswordEncoder encoder = new PasswordEncoderImpl();

        // Service + Controller
        AuthService authService = new AuthServiceImpl(
                userRepoFactory,
                roleRepoFactory,
                validator,
                encoder
        );

        AuthController authController = new AuthControllerImpl(authService);

        // === TEST LOGIN ===
        AuthRequestDTO req = new AuthRequestDTO("admin", "admin"); // ⚠️ adapte selon seed.sql
        AuthResultDTO res = authController.login(req);

        System.out.println("Login success = " + res.isSuccess());
        System.out.println("Message = " + res.getMessage());

        if (!res.isSuccess()) {
            System.out.println("FieldErrors = " + res.getFieldErrors());
            return;
        }

        System.out.println("Current user = " + authController.currentUser());

        // === TEST LOGOUT ===
        authController.logout();
        System.out.println("After logout, authenticated = " + authController.isAuthenticated());
    }
}
