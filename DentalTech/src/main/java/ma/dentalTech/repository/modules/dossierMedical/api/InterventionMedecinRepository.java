package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InterventionMedecinRepository extends CrudRepository<InterventionMedecin, Long> {

    List<InterventionMedecin> findByConsultationId(Long consultationId);
    void deleteByConsultationId(Long consultationId);


    /** Toutes les interventions qui utilisent un acte précis. */
    List<InterventionMedecin> findByActeId(Long acteId);

    /** Interventions d’un dossier (via consultation -> dossier). */
    List<InterventionMedecin> findByDossierId(Long dossierId);

    /** Interventions d’un patient (via dossier -> patient). */
    List<InterventionMedecin> findByPatientId(Long patientId);

    /** Interventions dans une période (filtre sur consultation.date_consultation). */
    List<InterventionMedecin> findByDateBetween(LocalDateTime start, LocalDateTime end);

    /** Interventions d’un acte + période (utile stats). */
    List<InterventionMedecin> findByActeIdAndDateBetween(Long acteId, LocalDateTime start, LocalDateTime end);

    /** Total interventions pour un médecin dans une période (via dossier_medical.medecin_id). */
    Integer countPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);

    /** Somme des montants (prix_patient) pour un médecin dans une période. */
    Double sumMontantPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);

    /** Somme des montants d’une consultation (utile facture/recap). */
    Double sumMontantPourConsultation(Long consultationId);

    boolean existsById(Long id);
    long count();
    List<InterventionMedecin> findPage(int limit, int offset);
}
