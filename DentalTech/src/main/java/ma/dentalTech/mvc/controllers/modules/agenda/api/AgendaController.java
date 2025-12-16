package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;

import java.time.LocalDate;
import java.util.List;

public interface AgendaController {

    // AgendaMensuel
    List<AgendaMensuel> getAllAgendas();
    AgendaMensuel getAgendaById(Long id);
    void createAgenda(AgendaMensuel agenda);
    void updateAgenda(AgendaMensuel agenda);
    void deleteAgenda(AgendaMensuel agenda);
    void deleteAgendaById(Long id);

    AgendaMensuel getAgendaByMedecinMonth(Long medecinId, String mois, int annee);
    List<AgendaMensuel> getAgendasByMedecin(Long medecinId);

    // DetailJournee
    List<DetailJournee> getAllDetails();
    DetailJournee getDetailById(Long id);
    void createDetail(DetailJournee d);
    void updateDetail(DetailJournee d);
    void deleteDetail(DetailJournee d);
    void deleteDetailById(Long id);

    List<DetailJournee> getDetailsByAgenda(Long agendaId);
    DetailJournee getDetailByAgendaAndDate(Long agendaId, LocalDate dateJour);

    void runDemo();
}
