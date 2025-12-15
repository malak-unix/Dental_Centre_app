package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;

import java.time.LocalDate;
import java.util.List;

public interface AgendaController {

    AgendaMensuel createAgendaMensuel(AgendaMensuel agenda);

    void updateAgendaMensuel(AgendaMensuel agenda);

    AgendaMensuel getAgendaMensuelById(Long id);

    List<AgendaMensuel> getAgendasByMedecin(Long medecinId);

    DetailJournee createDetailJournee(DetailJournee d);

    void updateDetailJournee(DetailJournee d);

    List<DetailJournee> getDetailsByAgenda(Long agendaId);

    DetailJournee getDetailByDate(Long agendaId, LocalDate dateJour);

    void deleteAgendaMensuelById(Long id);

    void deleteDetailJourneeById(Long id);

    void runDemo();
}
