package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceService {

    // CRUD
    List<Ordonnance> findAll();
    Ordonnance findById(Long id);
    void create(Ordonnance o);
    void update(Ordonnance o);
    void delete(Ordonnance o);
    void deleteById(Long id);

    // Recherche
    List<Ordonnance> findByDossierId(Long dossierId);
    List<Ordonnance> findByConsultationId(Long consultationId);
    List<Ordonnance> findByDate(LocalDate date);
    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);

    // Utilitaires
    long count();
    List<Ordonnance> findPage(int limit, int offset);
    Ordonnance findLastByDossierId(Long dossierId);
    Ordonnance findLastByConsultationId(Long consultationId);
    boolean existsById(Long id);

    // Métier: ordonnance + prescriptions
    void createWithPrescriptions(Ordonnance ordonnance, List<Prescription> prescriptions);
    void replacePrescriptions(Long ordonnanceId, List<Prescription> prescriptions);
    List<Prescription> findPrescriptions(Long ordonnanceId);
}
