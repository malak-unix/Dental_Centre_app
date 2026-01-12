package ma.dentalTech.common.exceptions;

import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

public class NotFoundException extends ServiceException {
    public NotFoundException(String message) { super(message); }
}
