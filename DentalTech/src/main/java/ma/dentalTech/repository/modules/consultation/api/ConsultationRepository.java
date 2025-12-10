package ma.dentalTech.repository.modules.consultation.api;

import ma.dentalTech.entities.consultation.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
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

    // Nombre total de consultations
    long count();

    // Pagination simple
    List<Consultation> findPage(int limit, int offset);
}
