package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.CrudRepository;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;


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

    List<Consultation> searchByObservation(String keyword);

    boolean existsById(Long id);

    // Nombre total de consultations
    long count();

    // Pagination simple
    List<Consultation> findPage(int limit, int offset);

    List<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO req);

    long countForList(ConsultationListRequestDTO req);

    //Methodes ajoute par aya berday kan st3mlhom f dashboard
    Integer countTermineesPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);
    Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);


}
