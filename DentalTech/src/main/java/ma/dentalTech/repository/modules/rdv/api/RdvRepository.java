package ma.dentalTech.repository.modules.rdv.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
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
    Integer countByDate(LocalDateTime start, LocalDateTime end);
    Integer countRdvEnRetard(LocalDate today);

    Integer countByMedecinAndDate(Long medecinId, LocalDateTime start, LocalDateTime end);

}
