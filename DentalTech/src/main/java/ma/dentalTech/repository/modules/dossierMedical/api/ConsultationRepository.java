package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends CrudRepository<Consultation, Long> {

    // Rechercher toutes les consultations d'un dossier
    List<Consultation> findByDossierId(Long dossierId);

    // Rechercher par date exacte
    List<Consultation> findByDate(LocalDate date);

    // Rechercher sur une période
    List<Consultation> findByDateBetween(LocalDate start, LocalDate end);

    // Rechercher par statut (PLANIFIE, TERMINE, ANNULE)
    List<Consultation> findByStatut(StatutConsultation statut);

    // Touch perso (très utile UI)
    List<Consultation> searchByObservation(String keyword);

    // Utilitaires
    boolean existsById(Long id);
    long count();
    List<Consultation> findPage(int limit, int offset);

    // Dashboard (Aya)
    Integer countTermineesPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);
    Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);
}
