package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implémentation du service d'encodage des mots de passe utilisant BCrypt.
 * Nom du fichier conservé : PasswordEncoderImpl.java
 */
public class PasswordEncoderImpl implements PasswordEncoder {

    private final int strength;

    public PasswordEncoderImpl() {
        this(10); // Coût par défaut pour BCrypt
    }

    public PasswordEncoderImpl(int strength) {
        this.strength = strength;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null");
        }
        // Génération du sel et hachage
        String salt = BCrypt.gensalt(strength);
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        // Vérification de la correspondance entre le clair et le haché
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}