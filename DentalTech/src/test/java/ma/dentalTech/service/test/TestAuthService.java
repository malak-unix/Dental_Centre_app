package ma.dentalTech.service.test;

import java.util.Map;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;

public class TestAuthService {

    public static void main(String[] args) {

        LoginFormValidator validator = new CredentialsValidatorImpl();
        PasswordEncoder encoder = new PasswordEncoderImpl();

        System.out.println("--- 🧪 Tests Validator ---");

        testValidator(validator, new AuthRequestDTO("admin", "1234"));
        testValidator(validator, new AuthRequestDTO("", "1234"));
        testValidator(validator, new AuthRequestDTO("admin", ""));

        System.out.println("\n--- 🧪 Tests PasswordEncoder ---");

        String hashed = encoder.encode("1234");
        System.out.println("Hash généré ✅ : " + hashed);

        boolean ok = encoder.matches("1234", hashed);
        boolean ko = encoder.matches("0000", hashed);

        System.out.println("matches bon mdp ✅ : " + ok);
        System.out.println("matches mauvais mdp ✅ : " + ko);
    }

    private static void testValidator(LoginFormValidator validator, AuthRequestDTO req) {
        Map<String, String> errors = validator.validate(req);
        System.out.println("Request = login='" + req.login() + "', password='" + req.password() + "'");
        if (errors.isEmpty()) {
            System.out.println("✅ OK (aucune erreur)\n");
        } else {
            System.out.println("❌ Erreurs = " + errors + "\n");
        }
    }
}
