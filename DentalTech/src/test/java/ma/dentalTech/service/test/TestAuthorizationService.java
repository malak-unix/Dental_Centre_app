package ma.dentalTech.service.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.service.modules.auth.impl.AuthorizationServiceImpl;

import java.util.Set;

public class TestAuthorizationService {

    public static void main(String[] args) {

        AuthorizationService authz = new AuthorizationServiceImpl();

        System.out.println("--- Test AuthorizationService ---");

        UserPrincipalDTO adminUser = new UserPrincipalDTO(
                1L,                         // id
                "Admin",                    // nom
                "admin@mail.com",           // email
                "admin",                    // login
                LibelleRole.ADMIN,          // rolePrincipal
                Set.of(LibelleRole.ADMIN),  // roles
                Set.of("PATIENT_READ", "PATIENT_WRITE") // privileges
        );

        boolean estAdmin = authz.hasRole(adminUser, LibelleRole.ADMIN);
        System.out.println("Autorisation Admin : " + (estAdmin ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        boolean estMedecin = authz.hasRole(adminUser, LibelleRole.MEDECIN);
        System.out.println("Autorisation Médecin (doit être faux) : " + (!estMedecin ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        boolean canRead = authz.hasPrivilege(adminUser, "patient_read"); // minuscule exprès
        System.out.println("Privilege patient_read : " + (canRead ? "✅ RÉUSSI" : "❌ ÉCHEC"));
    }
}
