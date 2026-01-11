package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    // ⚠️ patient.email n’existe pas en BD => on laisse mais on retournera Optional.empty() dans l'impl
    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByTelephone(String telephone);

    // Recherches
    List<Patient> findByNom(String nom);
    List<Patient> searchByNomPrenom(String keyword);
    List<Patient> searchByNom(String nomPart);

    boolean existsById(Long id);
    long count();
    List<Patient> findPage(int limit, int offset);

    // Many-to-many (si vous l’avez vraiment en BD, sinon UnsupportedOperationException)
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    void removeAllAntecedentsFromPatient(Long patientId);

    List<Antecedents> getAntecedentsOfPatient(Long patientId);
    List<Patient> getPatientsByAntecedent(Long antecedentId);

    // dashboard
    Integer countAll();
}
