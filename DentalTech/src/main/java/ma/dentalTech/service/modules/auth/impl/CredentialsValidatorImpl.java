package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO; // Utilisation obligatoire de ton DTO

public class CredentialsValidatorImpl implements CredentialsValidator {

    @Override
    public void validate(AuthRequestDTO request) {
        // On vérifie les données du DTO avant d'appeler la base de données
        if (request == null) {
            throw new IllegalArgumentException("La requête est nulle");
        }
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Le login ne peut pas être vide");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe est trop court");
        }
    }
}