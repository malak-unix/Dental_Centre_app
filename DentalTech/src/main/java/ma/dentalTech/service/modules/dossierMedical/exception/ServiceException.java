package ma.dentalTech.service.modules.dossierMedical.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) { super(message); }
    public ServiceException(String message, Throwable cause) { super(message, cause); }
}
