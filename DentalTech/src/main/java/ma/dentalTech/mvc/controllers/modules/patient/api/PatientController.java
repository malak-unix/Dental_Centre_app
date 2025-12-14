package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;

import java.util.List;

public interface PatientController {
    List<Patient> findAll();
    Patient findById(Long id);
    void create(Patient p);
    void update(Patient p);
    void deleteById(Long id);

    List<Patient> searchByNom(String nomPart);
    Patient findByTelephone(String tel);
    void showRecentPatients();

}
