package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoderImpl implements PasswordEncoder {

    private final int strength;

    public PasswordEncoderImpl() {
        this(10);
    }

    public PasswordEncoderImpl(int strength) {
        this.strength = strength;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null");
        }
        String salt = BCrypt.gensalt(strength);
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;

        String raw = rawPassword.toString();

        // ✅ DEV: si ce n'est pas un hash BCrypt → on compare en clair
        if (!isBcryptHash(encodedPassword)) {
            return raw.equals(encodedPassword);
        }

        // ✅ PROD: BCrypt normal
        try {
            return BCrypt.checkpw(raw, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBcryptHash(String s) {
        if (s == null) return false;
        return s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$");
    }
}
