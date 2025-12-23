package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;

import java.time.LocalDateTime;
import java.util.List;

public interface InterventionMedecinService {

    List<InterventionMedecin> getAll();
    InterventionMedecin getById(Long id);

    void create(InterventionMedecin i);
    void update(InterventionMedecin i);
    void delete(InterventionMedecin i);
    void deleteById(Long id);

    List<InterventionMedecin> getByConsultationId(Long consultationId);
    void deleteByConsultationId(Long consultationId);

    List<InterventionMedecin> getByActeId(Long acteId);
    List<InterventionMedecin> getByDossierId(Long dossierId);
    List<InterventionMedecin> getByPatientId(Long patientId);

    List<InterventionMedecin> getByDateBetween(LocalDateTime start, LocalDateTime end);
    List<InterventionMedecin> getByActeIdAndDateBetween(Long acteId, LocalDateTime start, LocalDateTime end);

    Integer countPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
    Double sumMontantPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);

    Double sumMontantPourConsultation(Long consultationId);

    boolean existsById(Long id);
    long count();
    List<InterventionMedecin> findPage(int limit, int offset);
}
