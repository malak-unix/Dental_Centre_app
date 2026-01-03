package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Antecedents;

import java.util.List;

public interface AntecedentAppService {

    List<Antecedents> lister() throws ServiceException;

    Antecedents consulter(Long id) throws ValidationException, ServiceException;

    Antecedents creer(Antecedents a) throws ValidationException, ServiceException;

    Antecedents modifier(Long id, Antecedents a) throws ValidationException, ServiceException;

    void supprimer(Long id) throws ValidationException, ServiceException;

    List<Antecedents> listerParPatient(Long patientId) throws ValidationException, ServiceException;
}
