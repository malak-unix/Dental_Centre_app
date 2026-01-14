package ma.dentalTech.service.test;

import ma.dentalTech.common.exceptions.AuthorizationException;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.service.modules.auth.impl.AuthorizationServiceImpl;

public class TestAuthorizationService {
    public static void main(String[] args) {

        AuthorizationServiceImpl authz = new AuthorizationServiceImpl();
        System.out.println("--- 🧪 Test du AuthorizationService ---");

        // 5. Test de sécurité (Principal nul)
        try {
            authz.checkRole(null, LibelleRole.ADMIN);
            System.out.println("Sécurité null : ❌ ÉCHEC (Aurait dû lancer une exception)");
        } catch (AuthorizationException | SecurityException | IllegalArgumentException e) {
            System.out.println("Sécurité null : ✅ RÉUSSI (Accès bien refusé) -> " + e.getMessage());
        }
    }
}
