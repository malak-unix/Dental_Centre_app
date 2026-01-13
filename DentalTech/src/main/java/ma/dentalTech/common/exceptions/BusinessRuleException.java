package ma.dentalTech.common.exceptions;

import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

public class BusinessRuleException extends ServiceException {
    public BusinessRuleException(String message) { super(message); }
}
