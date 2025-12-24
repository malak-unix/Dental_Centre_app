package ma.dentalTech.service.test;

import ma.dentalTech.mvc.dto.users.*;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.impl.UserManagementServiceImpl;

public class UserServiceTest {
    public static void main(String[] args) {
        System.out.println("--- Test UserManagementService ---");

        // 1. Tester la création d'un Admin
        CreateAdminRequestDTO adminReq = CreateAdminRequestDTO.builder()
                .nom("Admin").prenom("Principal").login("admin").password("123").build();

        // 2. Tester la création d'un Médecin avec sa spécialité
        CreateMedecinRequestDTO medecinReq = CreateMedecinRequestDTO.builder()
                .nom("Alami").prenom("Sami").specialite("Dentiste").build();

        // 3. Tester la création d'une Secrétaire avec son CNSS
        CreateSecretaireRequestDTO secReq = CreateSecretaireRequestDTO.builder()
                .nom("Bennani").prenom("Amal").numCNSS("123456").build();

        System.out.println("Tests des DTO de création terminés.");
    }
}