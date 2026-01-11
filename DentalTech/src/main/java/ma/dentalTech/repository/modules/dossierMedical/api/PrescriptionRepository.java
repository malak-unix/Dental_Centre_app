package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {

    List<Prescription> findByOrdonnanceId(Long ordonnanceId);
    void deleteByOrdonnanceId(Long ordonnanceId);
    boolean existsById(Long id);
    long countByOrdonnanceId(Long ordonnanceId);
}

