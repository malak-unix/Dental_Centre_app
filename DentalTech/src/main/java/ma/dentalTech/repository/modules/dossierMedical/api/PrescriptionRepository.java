package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {

    List<Prescription> findByOrdonnanceId(Long ordonnanceId);

    void deleteByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findByMedicamentId(Long medicamentId);
    long countByOrdonnanceId(Long ordonnanceId);
    boolean existsById(Long id);
}
