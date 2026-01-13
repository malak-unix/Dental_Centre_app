package ma.dentalTech.service.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;

public class RoleServiceTest {

    public static void main(String[] args) {

        System.out.println("--- Test Role (entité) ---");

        Role role = new Role();
        role.setLibelle(LibelleRole.ADMIN);
        role.setPrivileges("PATIENT_READ,PATIENT_WRITE");

        System.out.println("Role = " + role);
        System.out.println("Libelle = " + role.getLibelle());
        System.out.println("Privileges (CSV) = " + role.getPrivileges());
    }
}
