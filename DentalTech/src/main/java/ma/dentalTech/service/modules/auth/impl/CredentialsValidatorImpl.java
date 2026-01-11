package ma.dentalTech.service.modules.auth.impl;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;

/**
 * Implémentation de la validation des identifiants.
 */
public class CredentialsValidatorImpl implements LoginFormValidator {

    private static final int LOGIN_MIN = 3;
    private static final int PASSWORD_MIN = 4;

    @Override
    public Map<String, String> validate(AuthRequestDTO request) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (request == null) {
            errors.put("_global", "Formulaire invalide (requête null).");
            return errors;
        }

        // CORRECTION : Utilisation de login() et password() car c'est un Record
        // On enlève les "get" qui causaient l'erreur rouge
        String login = (request.login() == null) ? null : request.login().trim();
        String password = request.password();

        // 1) Validation du LOGIN
        if (login == null || login.isEmpty()) {
            errors.put("login", "Le login est obligatoire.");
        } else if (login.length() < LOGIN_MIN) {
            errors.put("login", "Le login doit contenir au moins " + LOGIN_MIN + " caractères.");
        }

        // 2) Validation du PASSWORD
        if (password == null || password.isBlank()) {
            errors.put("password", "Le mot de passe est obligatoire.");
        } else if (password.length() < PASSWORD_MIN) {
            errors.put("password", "Le mot de passe doit contenir au moins " + PASSWORD_MIN + " caractères.");
        }

        return errors;
    }
}