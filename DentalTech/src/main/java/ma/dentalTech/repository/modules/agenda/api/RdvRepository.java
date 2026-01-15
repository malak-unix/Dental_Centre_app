package ma.dentalTech.repository.modules.agenda.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.common.CrudRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RdvRepository extends CrudRepository<RDV, Long> {

    List<RDV> findByPatientId(Long patientId);

    List<RDV> findByDetailJourneeId(Long detailJourneeId);

    List<RDV> findByListeAttenteId(Long listeAttenteId);

    List<RDV> findByDate(LocalDate date);

    List<RDV> findByStatus(EtatRendezVous status);

    List<RDV> findUpcomingFromToday();


//methodes ajoute par AYA BERDAY utilisé dans dashboard
    List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date);

    Integer countByDate(LocalDateTime start, LocalDateTime end);
    int countRdvEnRetard(LocalDate today);
    int countRdvDuJour();
    int countByMedecinAndDate(Long medecinId, LocalDateTime start, LocalDateTime end);

}
