package ma.dentalTech.common.exceptions;

/**
 * Exception métier pour la couche service.
 * - Encapsule les erreurs DaoException
 * - Porte les erreurs de validation / métier
 */
public class ServiceException extends RuntimeException {

    private final String errorCode;

    public ServiceException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // =========================
    // Méthodes utilitaires
    // =========================

    public static ServiceException validation(String message) {
        return new ServiceException(message, "VALIDATION_ERROR");
    }

    public static ServiceException notFound(String message) {
        return new ServiceException(message, "NOT_FOUND");
    }
}
