package ma.dentalTech.service.common;

/**
 * Exception générique pour la couche Service.
 *
 * Elle permet :
 * - d’unifier les erreurs métier (validation, règle fonctionnelle)
 * - d’envelopper les exceptions techniques (DaoException, SQL, etc.)
 */
public class ServiceException extends RuntimeException {

    private final String errorCode; // optionnel, mais pratique (ex: "ORDO_NOT_FOUND")

    public ServiceException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // petites méthodes utilitaires statiques pour plus tard si tu veux
    public static ServiceException notFound(String message) {
        return new ServiceException(message, "NOT_FOUND");
    }

    public static ServiceException validation(String message) {
        return new ServiceException(message, "VALIDATION_ERROR");
    }
}
