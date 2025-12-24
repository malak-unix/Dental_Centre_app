package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;

public interface CredentialsValidator {

    /**
     * Valide le format du login et du mot de passe (non vide, longueur...).
     */
    void validate(AuthRequestDTO request);

    void validateNewPassword(String newPassword);
}