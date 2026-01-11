package ma.dentalTech.common.exceptions;

/**
 * Exception personnalisée pour les erreurs de droits d'accès.
 */
public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}