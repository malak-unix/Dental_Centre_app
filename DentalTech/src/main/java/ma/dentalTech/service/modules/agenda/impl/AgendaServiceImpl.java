package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.time.LocalDate;
import java.util.List;

public class AgendaServiceImpl implements AgendaService {

    private final AgendaMensuelRepository agendaRepo;
    private final DetailJourneeRepository detailRepo;

    public AgendaServiceImpl(AgendaMensuelRepository agendaRepo, DetailJourneeRepository detailRepo) {
        this.agendaRepo = agendaRepo;
        this.detailRepo = detailRepo;
    }

    // ---------- AgendaMensuel ----------
    @Override public List<AgendaMensuel> getAllAgendas() { return agendaRepo.findAll(); }
    @Override public AgendaMensuel getAgendaById(Long id) { return agendaRepo.findById(id); }

    @Override
    public void createAgenda(AgendaMensuel agenda) {
        if (agenda == null) throw new IllegalArgumentException("AgendaMensuel null");
        if (agenda.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire");
        if (agenda.getMois() == null) throw new IllegalArgumentException("mois obligatoire");
        if (agenda.getAnnee() <= 0) throw new IllegalArgumentException("annee invalide");
        agendaRepo.create(agenda);
    }

    @Override
    public void updateAgenda(AgendaMensuel agenda) {
        if (agenda == null || agenda.getId() == null) throw new IllegalArgumentException("AgendaMensuel id obligatoire");
        agendaRepo.update(agenda);
    }

    @Override public void deleteAgenda(AgendaMensuel agenda) { agendaRepo.delete(agenda); }
    @Override public void deleteAgendaById(Long id) { agendaRepo.deleteById(id); }

    @Override
    public AgendaMensuel getAgendaByMedecinMonth(Long medecinId, String mois, int annee) {
        return agendaRepo.findByMedecinAndMonth(medecinId, mois, annee);
    }

    @Override
    public List<AgendaMensuel> getAgendasByMedecin(Long medecinId) {
        return agendaRepo.findByMedecin(medecinId);
    }

    // ---------- DetailJournee ----------
    @Override public List<DetailJournee> getAllDetails() { return detailRepo.findAll(); }
    @Override public DetailJournee getDetailById(Long id) { return detailRepo.findById(id); }

    @Override
    public void createDetail(DetailJournee d) {
        if (d == null) throw new IllegalArgumentException("DetailJournee null");
        if (d.getAgendaId() == null) throw new IllegalArgumentException("agendaId obligatoire");
        if (d.getDateJour() == null) throw new IllegalArgumentException("dateJour obligatoire");
        detailRepo.create(d);
    }

    @Override
    public void updateDetail(DetailJournee d) {
        if (d == null || d.getId() == null) throw new IllegalArgumentException("DetailJournee id obligatoire");
        detailRepo.update(d);
    }

    @Override public void deleteDetail(DetailJournee d) { detailRepo.delete(d); }
    @Override public void deleteDetailById(Long id) { detailRepo.deleteById(id); }

    @Override
    public List<DetailJournee> getDetailsByAgenda(Long agendaId) {
        return detailRepo.findByAgendaId(agendaId);
    }

    @Override
    public DetailJournee getDetailByAgendaAndDate(Long agendaId, LocalDate dateJour) {
        return detailRepo.findByAgendaIdAndDateJour(agendaId, dateJour);
    }
}
