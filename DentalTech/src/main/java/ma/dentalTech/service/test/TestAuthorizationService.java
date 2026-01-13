package ma.dentalTech.service.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.impl.AuthorizationServiceImpl;

public class TestAuthorizationService {
    public static void main(String[] args) {
        // 1. Instanciation de ton service
        AuthorizationServiceImpl authz = new AuthorizationServiceImpl();

        System.out.println("--- 🧪 Test du AuthorizationService ---");

        // 2. Simulation d'un utilisateur connecté avec le rôle ADMIN
        // UserPrincipalDTO adminUser = UserPrincipalDTO.builder()
           //     .login("admin_test")
           //     .role(LibelleRole.ADMIN)
           //     .build();

        // 3. Test de vérification de rôle (Cas positif)
        //boolean estAdmin = authz.hasRole(adminUser, LibelleRole.ADMIN);
        //System.out.println("Vérification Admin : " + (estAdmin ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        // 4. Test de restriction (Cas négatif)
        //boolean estMedecin = authz.hasRole(adminUser, LibelleRole.MEDECIN);
        //System.out.println("Vérification Médecin (doit être faux) : " + (!estMedecin ? "✅ RÉUSSI" : "❌ ÉCHEC"));

        // 5. Test de sécurité (Principal nul)
        try {
            authz.checkRole(null, LibelleRole.ADMIN);
            System.out.println("Sécurité null : ❌ ÉCHEC (Aurait dû lancer une exception)");
        } catch (SecurityException | IllegalArgumentException e) {
            System.out.println("Sécurité null : ✅ RÉUSSI (Accès bien refusé)");
        }
    }
}