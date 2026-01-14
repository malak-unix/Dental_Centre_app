package ma.dentalTech;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthControllerImpl;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.impl.AuthServiceImpl;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;
import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

public class AuthControllerDemo {

    public static void main(String[] args) {

        // wiring MANUEL (comme avant DB)
        RepoFactory<UtilisateurRepository> userFactory =
                ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl::new;

        RepoFactory<RoleRepository> roleFactory =
                ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl::new;

        AuthService authService = new AuthServiceImpl(
                userFactory,
                roleFactory,
                new CredentialsValidatorImpl(),
                new PasswordEncoderImpl()
        );

        AuthController authController = new AuthControllerImpl(authService);

        // ✅ CONSTRUCTEUR (pas de setters chez toi)
        AuthRequestDTO req = new AuthRequestDTO("admin", "admin");

        var res = authController.login(req);
        System.out.println(res);

        System.out.println("Success = " + res);
    }
}
