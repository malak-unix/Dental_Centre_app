package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getAll();
    Patient getById(Long id);
    void create(Patient p);
    void update(Patient p);
    void delete(Patient p);
    void deleteById(Long id);

    List<Patient> searchByNom(String nomPart);
    Patient getByTelephone(String telephone);
}
