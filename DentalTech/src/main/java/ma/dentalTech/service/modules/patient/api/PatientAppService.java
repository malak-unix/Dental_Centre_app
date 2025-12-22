package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;

import java.util.List;

public interface PatientAppService {

    List<PatientListDto> listerPatients() throws ServiceException;

    PatientFormDto consulterPatient(Long id) throws ValidationException, ServiceException;

    PatientFormDto creerPatient(PatientFormDto dto) throws ValidationException, ServiceException;

    PatientFormDto modifierPatient(Long id, PatientFormDto dto) throws ValidationException, ServiceException;

    void supprimerPatient(Long id) throws ValidationException, ServiceException;

    List<PatientListDto> rechercherParNom(String nom) throws ValidationException, ServiceException;

    PatientFormDto rechercherParTelephone(String telephone) throws ValidationException, ServiceException;
}
