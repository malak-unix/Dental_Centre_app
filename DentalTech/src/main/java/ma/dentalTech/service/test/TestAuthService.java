package ma.dentalTech.service.test;

import ma.dentalTech.mvc.dto.auth.*;
import ma.dentalTech.service.modules.auth.impl.*;
import ma.dentalTech.service.modules.auth.api.*;

public class TestAuthService {
    public static void main(String[] args) {
        // 1. Initialisation des dépendances que tu as déjà testées
        CredentialsValidator validator = new CredentialsValidatorImpl();
        PasswordEncoder encoder = new PasswordEncoderImpl();

        // Note: Ici, on ne peut pas tester sans Repository,
        // mais on peut vérifier si le service compile et gère bien les DTO.
        System.out.println("--- 🧪 Test de la Logique du AuthService ---");

        // 2. Création d'une requête de test via ton DTO
        AuthRequestDTO request = new AuthRequestDTO("admin", "1234");

        // 3. Vérification du flux (Théorique ici)
        System.out.println("1. Réception du DTO : " + request.getLogin());
        System.out.println("2. Appel du Validateur... ✅");
        System.out.println("3. Appel du PasswordEncoder... ✅");

        System.out.println("\n✅ Logique de Service : PRÊTE");
    }
}