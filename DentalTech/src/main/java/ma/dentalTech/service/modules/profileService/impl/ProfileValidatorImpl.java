package ma.dentalTech.service.modules.profileService.impl; // Ton arborescence

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Tes DTOs
import ma.dentalTech.mvc.dto.auth.ProfileUpdateRequest;
// Ton interface API
import ma.dentalTech.service.modules.profileService.api.ProfileValidator;

public class ProfileValidatorImpl implements ProfileValidator {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public Map<String, String> validate(ProfileUpdateRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (req == null || req.id() == null) {
            errors.put("_global", "Requête invalide.");
            return errors;
        }

        // --- Validation Utilisateur (Base) ---
        if (isBlank(req.prenom())) errors.put("prenom", "Prénom obligatoire.");
        if (isBlank(req.nom())) errors.put("nom", "Nom obligatoire.");

        String email = trim(req.email());
        if (isBlank(email)) {
            errors.put("email", "Email obligatoire.");
        } else if (!EMAIL.matcher(email).matches()) {
            errors.put("email", "Format d'email invalide.");
        }

        if (!isBlank(req.tel()) && req.tel().length() > 30) {
            errors.put("tel", "Numéro de téléphone trop long.");
        }

        if (!isBlank(req.cin()) && req.cin().length() > 30) {
            errors.put("cin", "CIN trop long.");
        }

        if (req.sexe() == null) {
            errors.put("sexe", "Le sexe est obligatoire.");
        }

        // --- Validation Staff (Champs partagés) ---
        if (req.salaire() != null && req.salaire() < 0) {
            errors.put("salaire", "Le salaire ne peut pas être négatif.");
        }
        if (req.prime() != null && req.prime() < 0) {
            errors.put("prime", "La prime ne peut pas être négative.");
        }
        // Utilisation de soldeConge (ton nom de champ)
        if (req.soldeConge() != null && req.soldeConge() < 0) {
            errors.put("soldeConge", "Le solde de congé est invalide.");
        }

        // --- Validation Médecin (Spécifique) ---
        if (!isBlank(req.specialite()) && req.specialite().length() > 150) {
            errors.put("specialite", "La spécialité est trop longue.");
        }

        // --- Validation Secrétaire (Spécifique) ---
        // Synchronisé avec req.numCNSS() de ton DTO
        if (!isBlank(req.numCNSS()) && req.numCNSS().length() > 50) {
            errors.put("numCNSS", "Le numéro CNSS est trop long.");
        }

        if (req.commission() != null && req.commission() < 0) {
            errors.put("commission", "La commission est invalide.");
        }

        return errors;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}