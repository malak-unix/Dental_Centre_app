package ma.dentalTech.repository.modules.agenda.api;

import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface DetailJourneeRepository extends CrudRepository<DetailJournee, Long> {

    List<DetailJournee> findByAgenda(Long agendaId);

    DetailJournee findByAgendaAndDate(Long agendaId, LocalDate date);
}
