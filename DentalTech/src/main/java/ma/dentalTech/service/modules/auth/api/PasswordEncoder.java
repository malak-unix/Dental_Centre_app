package ma.dentalTech.service.modules.auth.api;

/**
 * Interface pour le hachage et la vérification des mots de passe.
 */
public interface PasswordEncoder {

    /**
     * Encode (hache) le mot de passe en clair.
     * @param rawPassword Le mot de passe saisi par l'utilisateur.
     * @return Le mot de passe haché (ex: avec BCrypt).
     */
    String encode(CharSequence rawPassword);

    /**
     * Vérifie si un mot de passe en clair correspond au mot de passe haché en base.
     * @param rawPassword Le mot de passe en clair à vérifier.
     * @param encodedPassword Le mot de passe déjà haché provenant de la BDD.
     * @return true si les mots de passe correspondent, false sinon.
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}