package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.auth.common.SessionContext;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthControllerImpl;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.service.modules.auth.impl.AuthServiceImpl;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;
import ma.dentalTech.service.modules.users.api.UserAuthQueryService;

import java.time.LocalDate;
import java.util.List;

public class TestAuthNoDb {

    public static void main(String[] args) {

        // Fake service USERS (simule un user en mémoire)
        UserAuthQueryService fakeUsers = login -> {
            if (!"admin".equals(login)) return null;

            Utilisateur u = new Utilisateur();
            u.setId(1L);
            u.setNom("Admin");
            u.setEmail("admin@mail.com");
            u.setLogin("admin");

            // mot de passe en clair "1234" hashé via encoder
            PasswordEncoderImpl enc = new PasswordEncoderImpl();
            u.setMotDePasse(enc.encode("1234"));
            u.setLastLoginDate(LocalDate.now());
            u.setDateNaissance(LocalDate.of(2000, 1, 1));

            Role r = new Role();
            r.setLibelle(LibelleRole.ADMIN);
            r.setPrivileges("DASHBOARD_READ,PATIENT_WRITE");

            return new UserAuthQueryService.UserAuthData(u, List.of(r));
        };

        var validator = new CredentialsValidatorImpl();
        var encoder = new PasswordEncoderImpl();

        // On utilise le constructeur "new arch"
        var authService = new AuthServiceImpl(fakeUsers, validator, encoder);

        var controller = new AuthControllerImpl(authService);

        // 1) Test login OK
        AuthResultDTO ok = controller.login(new AuthRequestDTO("admin", "1234"));
        System.out.println("LOGIN OK? " + ok.isSuccess());
        System.out.println("SESSION user? " + (SessionContext.getInstance().getCurrentUser() != null));

        // 2) Test login KO
        SessionContext.getInstance().clear();
        AuthResultDTO ko = controller.login(new AuthRequestDTO("admin", "wrong"));
        System.out.println("LOGIN KO? " + (!ko.isSuccess()));
        System.out.println("SESSION empty? " + (SessionContext.getInstance().getCurrentUser() == null));
    }
}
