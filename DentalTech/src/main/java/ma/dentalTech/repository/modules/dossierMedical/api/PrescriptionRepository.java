package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {

    /**
     * Liste des prescriptions associées à une ordonnance.
     */
    List<Prescription> findByOrdonnanceId(Long ordonnanceId);

    /**
     * Supprime toutes les prescriptions d'une ordonnance
     * (utile quand on supprime ou réédite l'ordonnance).
     */
    void deleteByOrdonnanceId(Long ordonnanceId);
    List<Prescription> findByMedicamentId(Long medicamentId);
    long countByOrdonnanceId(Long ordonnanceId);
    boolean existsById(Long id);
}
