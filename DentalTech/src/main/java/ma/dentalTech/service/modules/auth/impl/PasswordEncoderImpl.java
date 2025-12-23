package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // Pour le moment, on retourne le texte brut (à remplacer par BCrypt plus tard)
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Compare le mot de passe du DTO avec le champ 'motDePass_hash'
        if (rawPassword == null || encodedPassword == null) return false;
        return rawPassword.toString().equals(encodedPassword);
    }
}