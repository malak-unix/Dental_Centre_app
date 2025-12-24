package ma.dentalTech.service.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.service.modules.users.api.RoleManagementService;
import ma.dentalTech.service.modules.users.impl.RoleManagementServiceImpl;

public class RoleServiceTest {
    public static void main(String[] args) {
        // Initialisation manuelle du repository et du service
        // RoleRepository roleRepo = new RoleRepositoryImpl();
        // RoleManagementService roleService = new RoleManagementServiceImpl(roleRepo);

        System.out.println("--- Test RoleManagementService ---");
        // Exemple : Création d'un rôle ADMIN
        Role role = new Role();
        role.setLibelle(String.valueOf(LibelleRole.ADMIN));

        // System.out.println("Rôle créé : " + roleService.createRole(role).getLibelle());
    }
}