package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Prescription;

import java.util.List;

public interface PrescriptionService {

    // CRUD
    List<Prescription> findAll();
    Prescription findById(Long id);
    void create(Prescription p);
    void update(Prescription p);
    void delete(Prescription p);
    void deleteById(Long id);

    // Spécifiques
    List<Prescription> findByOrdonnanceId(Long ordonnanceId);
    void deleteByOrdonnanceId(Long ordonnanceId);

    List<Prescription> findByMedicamentId(Long medicamentId);
    long countByOrdonnanceId(Long ordonnanceId);
    boolean existsById(Long id);
}
