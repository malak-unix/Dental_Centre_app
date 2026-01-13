package ma.dentalTech.service.test;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl; // ou CredentialsValidatorImpl selon ton renommage

public class TestCredentialsValidator {
    public static void main(String[] args) {
        CredentialsValidatorImpl validator = new CredentialsValidatorImpl();

        System.out.println("--- Test CredentialsValidator ---");

        // Cas 1 : Login vide
        try {
            validator.validate(new AuthRequestDTO("", "1234"));
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Succès : Login vide détecté : " + e.getMessage());
        }

        // Cas 2 : Password nul
        try {
            validator.validate(new AuthRequestDTO("admin", null));
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Succès : Password nul détecté : " + e.getMessage());
        }
    }
}