package ma.dentalTech.test1.services;

import ma.dentalTech.entities.role.Role;
import ma.dentalTech.entities.enums.LibelleRole; // Si tu utilises un Enum
import ma.dentalTech.service.modules.users.api.RoleService;
import ma.dentalTech.service.modules.users.impl.RoleServiceImpl;

import java.util.List;

public class TestRoleService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : ROLE ---");
        RoleService roleService = new RoleServiceImpl();

        try {
            // 1. Afficher tous les rôles disponibles
            System.out.println("🔍 Recherche de tous les rôles...");
            List<Role> roles = roleService.getAllRoles();

            if (roles.isEmpty()) {
                System.out.println("⚠️ Aucun rôle trouvé. La base est vide ?");
            } else {
                for (Role r : roles) {
                    System.out.println("   - Rôle trouvé : " + r.getLibelle());
                }
            }

            // 2. Tester la recherche par nom (utile pour l'inscription)
            System.out.print("🔍 Recherche du rôle 'MEDECIN'... ");
            Role roleMedecin = roleService.getRoleParNom("MEDECIN");

            if (roleMedecin != null) {
                System.out.println("✅ Trouvé (ID: " + roleMedecin.getId() + ")");
            } else {
                System.out.println("❌ Non trouvé.");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
}