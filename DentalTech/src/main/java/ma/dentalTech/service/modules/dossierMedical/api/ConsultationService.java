package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationService {

    List<Consultation> getAll();
    Consultation getById(Long id);

    void create(Consultation c);
    void update(Consultation c);
    void delete(Consultation c);
    void deleteById(Long id);

    List<Consultation> getByDossierId(Long dossierId);
    List<Consultation> getByDate(LocalDate date);
    List<Consultation> getByDateBetween(LocalDate start, LocalDate end);
    List<Consultation> getByStatut(StatutConsultation statut);

    List<Consultation> searchByObservation(String keyword);

    boolean existsById(Long id);
    long count();
    List<Consultation> findPage(int limit, int offset);

    // Dashboard
    Integer countTermineesPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);
    Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end);
}
