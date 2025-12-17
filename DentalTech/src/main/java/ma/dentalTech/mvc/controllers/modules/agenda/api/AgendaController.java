package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;

import java.time.LocalDate;
import java.util.List;

public interface AgendaController {

    List<AgendaMensuelDto> getAllAgendas();
    AgendaMensuelDto getAgendaById(Long id);

    List<DetailJourneeDto> getDetailJourneesByAgendaId(Long agendaId);
    DetailJourneeDto getDetailJourneeById(Long id);
    DetailJourneeDto getDetailJourneeByAgendaIdAndDate(Long agendaId, LocalDate dateJour);
}
