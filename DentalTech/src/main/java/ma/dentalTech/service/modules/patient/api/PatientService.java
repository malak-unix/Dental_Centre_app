package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.PatientDTO;

import java.util.List;

/**
 * Service métier pour le module Patient.
 */
public interface PatientService {


    List<PatientDTO> getTodayPatientsAsDTO() throws ServiceException;
}
