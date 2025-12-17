package ma.dentalTech.repository.modules.agenda.api;

import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DetailJourneeRepository extends CrudRepository<DetailJournee, Long> {

    List<DetailJournee> findByAgendaId(Long agendaId);

    DetailJournee findByAgendaIdAndDateJour(Long agendaId, LocalDate dateJour);
}
