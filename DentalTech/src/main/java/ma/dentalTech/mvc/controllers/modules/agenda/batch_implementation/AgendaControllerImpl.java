package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.util.List;

public class AgendaControllerImpl implements AgendaController {

    private final AgendaService service;

    public AgendaControllerImpl(AgendaService service) {
        this.service = service;
    }

    @Override
    public AgendaMensuel createAgendaMensuel(AgendaMensuel agenda) {
        service.createAgendaMensuel(agenda);
        System.out.println("[AGENDA] created AgendaMensuel id=" + agenda.getId());
        return agenda;
    }

    @Override
    public void updateAgendaMensuel(AgendaMensuel agenda) {
        service.updateAgendaMensuel(agenda);
        System.out.println("[AGENDA] updated AgendaMensuel id=" + agenda.getId());
    }

    @Override
    public AgendaMensuel getAgendaMensuelById(Long id) {
        return service.findAgendaMensuelById(id);
    }

    @Override
    public List<AgendaMensuel> getAgendasByMedecin(Long medecinId) {
        return service.findAgendasByMedecin(medecinId);
    }

    @Override
    public DetailJournee createDetailJournee(DetailJournee d) {
        service.createDetailJournee(d);
        System.out.println("[AGENDA] created DetailJournee id=" + d.getId());
        return d;
    }

    @Override
    public void updateDetailJournee(DetailJournee d) {
        service.updateDetailJournee(d);
        System.out.println("[AGENDA] updated DetailJournee id=" + d.getId());
    }

    @Override
    public List<DetailJournee> getDetailsByAgenda(Long agendaId) {
        return service.findDetailsByAgendaId(agendaId);
    }

    @Override
    public DetailJournee getDetailByDate(Long agendaId, LocalDate dateJour) {
        return service.findDetailByAgendaAndDate(agendaId, dateJour);
    }

    @Override
    public void deleteAgendaMensuelById(Long id) {
        service.deleteAgendaMensuelById(id);
        System.out.println("[AGENDA] deleted AgendaMensuel id=" + id);
    }

    @Override
    public void deleteDetailJourneeById(Long id) {
        service.deleteDetailJourneeById(id);
        System.out.println("[AGENDA] deleted DetailJournee id=" + id);
    }

    @Override
    public void runDemo() {
        System.out.println("=== AGENDA Controller DEMO ===");
        System.out.println("OK");
    }
}
