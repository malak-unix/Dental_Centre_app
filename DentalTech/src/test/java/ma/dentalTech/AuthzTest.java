package ma.dentalTech;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.service.modules.auth.impl.AuthorizationServiceImpl;

import java.util.LinkedHashSet;
import java.util.Set;

public class AuthzTest {

    public static void main(String[] args) {

        AuthorizationService authz = new AuthorizationServiceImpl();

        Set<LibelleRole> roles = new LinkedHashSet<>();
        roles.add(LibelleRole.ADMIN);

        Set<String> privileges = new LinkedHashSet<>();
        privileges.add("PATIENT_READ");
        privileges.add("PATIENT_WRITE");

        UserPrincipalDTO admin = new UserPrincipalDTO(
                1L,
                "Admin",
                "admin@mail.com",
                "admin",
                LibelleRole.ADMIN,
                roles,
                privileges
        );

        System.out.println("hasRole ADMIN = " +
                authz.hasRole(admin, LibelleRole.ADMIN));

        System.out.println("hasRole MEDECIN = " +
                authz.hasRole(admin, LibelleRole.MEDECIN));

        System.out.println("hasAnyRole (MEDECIN, ADMIN) = " +
                authz.hasAnyRole(admin, LibelleRole.MEDECIN, LibelleRole.ADMIN));

        System.out.println("hasPrivilege PATIENT_READ = " +
                authz.hasPrivilege(admin, "PATIENT_READ"));

        System.out.println("hasPrivilege RDV_DELETE = " +
                authz.hasPrivilege(admin, "RDV_DELETE"));

        try {
            authz.checkRole(admin, LibelleRole.ADMIN);
            System.out.println("checkRole ADMIN ✅");
        } catch (Exception e) {
            System.out.println("checkRole ADMIN ❌");
        }

        try {
            authz.checkRole(admin, LibelleRole.MEDECIN);
            System.out.println("checkRole MEDECIN ❌");
        } catch (Exception e) {
            System.out.println("checkRole MEDECIN ✅ (exception levée)");
        }
    }
}
