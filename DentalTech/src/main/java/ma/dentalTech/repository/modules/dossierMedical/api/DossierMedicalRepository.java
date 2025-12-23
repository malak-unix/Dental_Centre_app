package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DossierMedicalRepository extends CrudRepository<DossierMedical, Long> {

    Optional<DossierMedical> findByPatientId(Long patientId);

    List<DossierMedical> findByMedecinId(Long medecinId);

    List<DossierMedical> searchByNotes(String keyword);

    boolean existsById(Long id);

    long count();

    List<DossierMedical> findPage(int limit, int offset);

    // Utilisé dans dashboard - Aya
    Integer countActifs();
}
