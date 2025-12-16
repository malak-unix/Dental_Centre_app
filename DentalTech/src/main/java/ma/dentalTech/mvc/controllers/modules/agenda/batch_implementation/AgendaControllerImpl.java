package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.util.List;

public class AgendaControllerImpl implements AgendaController {

    private final AgendaService service;

    public AgendaControllerImpl(AgendaService service) {
        this.service = service;
    }

    // ==========================
    // AgendaMensuel
    // ==========================

    @Override
    public List<AgendaMensuel> getAllAgendas() {
        return service.getAllAgendas();
    }

    @Override
    public AgendaMensuel getAgendaById(Long id) {
        return service.getAgendaById(id);
    }

    @Override
    public void createAgenda(AgendaMensuel agenda) {
        service.createAgenda(agenda);
        System.out.println("[AGENDA] created AgendaMensuel id=" + agenda.getId());
    }

    @Override
    public void updateAgenda(AgendaMensuel agenda) {
        service.updateAgenda(agenda);
        System.out.println("[AGENDA] updated AgendaMensuel id=" + (agenda != null ? agenda.getId() : null));
    }

    @Override
    public void deleteAgenda(AgendaMensuel agenda) {
        service.deleteAgenda(agenda);
        System.out.println("[AGENDA] deleted AgendaMensuel entity");
    }

    @Override
    public void deleteAgendaById(Long id) {
        service.deleteAgendaById(id);
        System.out.println("[AGENDA] deleted AgendaMensuel id=" + id);
    }

    @Override
    public AgendaMensuel getAgendaByMedecinMonth(Long medecinId, String mois, int annee) {
        return service.getAgendaByMedecinMonth(medecinId, mois, annee);
    }

    @Override
    public List<AgendaMensuel> getAgendasByMedecin(Long medecinId) {
        return service.getAgendasByMedecin(medecinId);
    }

    // ==========================
    // DetailJournee
    // ==========================

    @Override
    public List<DetailJournee> getAllDetails() {
        return service.getAllDetails();
    }

    @Override
    public DetailJournee getDetailById(Long id) {
        return service.getDetailById(id);
    }

    @Override
    public void createDetail(DetailJournee d) {
        service.createDetail(d);
        System.out.println("[AGENDA] created DetailJournee id=" + d.getId());
    }

    @Override
    public void updateDetail(DetailJournee d) {
        service.updateDetail(d);
        System.out.println("[AGENDA] updated DetailJournee id=" + (d != null ? d.getId() : null));
    }

    @Override
    public void deleteDetail(DetailJournee d) {
        service.deleteDetail(d);
        System.out.println("[AGENDA] deleted DetailJournee entity");
    }

    @Override
    public void deleteDetailById(Long id) {
        service.deleteDetailById(id);
        System.out.println("[AGENDA] deleted DetailJournee id=" + id);
    }

    @Override
    public List<DetailJournee> getDetailsByAgenda(Long agendaId) {
        return service.getDetailsByAgenda(agendaId);
    }

    @Override
    public DetailJournee getDetailByAgendaAndDate(Long agendaId, LocalDate dateJour) {
        return service.getDetailByAgendaAndDate(agendaId, dateJour);
    }

    // ==========================
    // Demo batch
    // ==========================
    @Override
    public void runDemo() {
        System.out.println("=== AGENDA Controller DEMO ===");
        System.out.println("Agendas total: " + getAllAgendas().size());
        System.out.println("Details total: " + getAllDetails().size());
        System.out.println("==============================");
    }
}
