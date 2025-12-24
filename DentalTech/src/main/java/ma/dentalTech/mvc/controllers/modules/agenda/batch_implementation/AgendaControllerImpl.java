package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;

import java.time.LocalDate;
import java.util.List;

public class AgendaControllerImpl implements AgendaController {

    private final AgendaMensuelRepository agendaRepo;
    private final DetailJourneeRepository detailRepo;

    public AgendaControllerImpl(AgendaMensuelRepository agendaRepo,
                                DetailJourneeRepository detailRepo) {
        this.agendaRepo = agendaRepo;
        this.detailRepo = detailRepo;
    }

    @Override
    public List<AgendaMensuelDto> getAllAgendas() {
        return agendaRepo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public AgendaMensuelDto getAgendaById(Long id) {
        AgendaMensuel a = agendaRepo.findById(id);
        return a == null ? null : toDto(a);
    }

    @Override
    public List<DetailJourneeDto> getDetailJourneesByAgendaId(Long agendaId) {
        return detailRepo.findByAgendaId(agendaId).stream().map(this::toDto).toList();
    }

    @Override
    public DetailJourneeDto getDetailJourneeById(Long id) {
        DetailJournee d = detailRepo.findById(id);
        return d == null ? null : toDto(d);
    }

    @Override
    public DetailJourneeDto getDetailJourneeByAgendaIdAndDate(Long agendaId, LocalDate dateJour) {
        DetailJournee d = detailRepo.findByAgendaIdAndDateJour(agendaId, dateJour);
        return d == null ? null : toDto(d);
    }

    // =========================
    // Mappers (Entity -> DTO)
    // =========================

    private AgendaMensuelDto toDto(AgendaMensuel a) {
        return AgendaMensuelDto.builder()
                .id(a.getId())
                .medecinId(a.getMedecinId())
                .mois(parseMois(a.getMois()))
                .annee(a.getAnnee())
                .build();
    }

    private DetailJourneeDto toDto(DetailJournee d) {
        return DetailJourneeDto.builder()
                .id(d.getId())
                .agendaId(d.getAgendaId())
                .dateJour(d.getDateJour())
                .heureDebutTravail(d.getHeureDebutTravail())
                .heureFinTravail(d.getHeureFinTravail())
                .etatJour(parseStatutJournee(d.getEtatJour()))
                .commentaire(d.getCommentaire())
                .build();
    }

    private Mois parseMois(Object moisValue) {
        if (moisValue == null) return null;
        if (moisValue instanceof Mois m) return m;
        String s = String.valueOf(moisValue).trim();
        if (s.isEmpty()) return null;
        try { return Mois.valueOf(s.toUpperCase()); }
        catch (Exception e) { return null; }
    }

    private StatutJournee parseStatutJournee(Object v) {
        if (v == null) return null;
        if (v instanceof StatutJournee sj) return sj;
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return null;
        try { return StatutJournee.valueOf(s.toUpperCase()); }
        catch (Exception e) { return null; }
    }
}
