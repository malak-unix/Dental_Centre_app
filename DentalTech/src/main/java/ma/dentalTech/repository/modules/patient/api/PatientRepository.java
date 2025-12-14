package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    // optionnel (utile)
    List<Patient> findByNomLike(String nomPart);
}
