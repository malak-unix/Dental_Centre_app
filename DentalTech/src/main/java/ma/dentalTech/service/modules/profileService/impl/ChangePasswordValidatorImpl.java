package ma.dentalTech.service.modules.profileService.impl;

import ma.dentalTech.mvc.dto.auth.ChangePasswordRequest;
import ma.dentalTech.service.modules.profileService.api.ChangePasswordValidator;
import ma.dentalTech.configuration.ApplicationContext;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChangePasswordValidatorImpl implements ChangePasswordValidator {

    @Override
    public Map<String, String> validate(ChangePasswordRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (req == null) {
            errors.put("_global", "Requête invalide.");
            return errors;
        }

        // Récupération des config ou valeurs par défaut
       // int min = Integer.parseInt(ApplicationContext.getInstance().getProperty("profile.password.min", "8"));
       // int max = Integer.parseInt(ApplicationContext.getInstance().getProperty("profile.password.max", "128"));

        String cur = req.currentPassword();
        String nw  = req.newPassword();
        String cf  = req.confirmPassword();

        if (req.userId() == null) errors.put("_global", "Utilisateur invalide.");

        if (cur == null || cur.isBlank()) errors.put("currentPassword", "Mot de passe actuel obligatoire.");

        if (nw == null || nw.isBlank()) {
            errors.put("newPassword", "Nouveau mot de passe obligatoire.");
        } else {
           // if (nw.length() < min) errors.put("newPassword", "Minimum " + min + " caractères.");
           // else if (nw.length() > max) errors.put("newPassword", "Maximum " + max + " caractères.");

            if (!hasUpper(nw)) errors.put("newPassword", "Doit contenir une majuscule.");
            else if (!hasLower(nw)) errors.put("newPassword", "Doit contenir une minuscule.");
            else if (!hasDigit(nw)) errors.put("newPassword", "Doit contenir un chiffre.");
            else if (!hasSpecial(nw)) errors.put("newPassword", "Doit contenir un caractère spécial.");
        }

        if (cf == null || cf.isBlank()) {
            errors.put("confirmPassword", "Confirmation obligatoire.");
        } else if (nw != null && !nw.equals(cf)) {
            errors.put("confirmPassword", "Confirmation incorrecte.");
        }

        if (cur != null && nw != null && cur.equals(nw)) {
            errors.put("_global", "Le nouveau mot de passe doit être différent de l'ancien.");
        }

        return errors;
    }

    private boolean hasUpper(String s){ for(char c: s.toCharArray()) if(Character.isUpperCase(c)) return true; return false; }
    private boolean hasLower(String s){ for(char c: s.toCharArray()) if(Character.isLowerCase(c)) return true; return false; }
    private boolean hasDigit(String s){ for(char c: s.toCharArray()) if(Character.isDigit(c)) return true; return false; }
    private boolean hasSpecial(String s){ for(char c: s.toCharArray()) if(!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) return true; return false; }
}