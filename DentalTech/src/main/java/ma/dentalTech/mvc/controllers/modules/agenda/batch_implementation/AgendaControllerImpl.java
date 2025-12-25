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

    // ✅ Constructeur injecté par ApplicationContext (createOptional)
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
                // ⚠️ chez toi mois est String, donc pas .name()
                .mois(Mois.valueOf(String.valueOf(a.getMois())))
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
                // si etatJour est enum => .name() ok ; si String => direct
                .etatJour(d.getEtatJour() == null ? null : String.valueOf(StatutJournee.valueOf(d.getEtatJour().toString())))
                .commentaire(d.getCommentaire())
                .build();
    }
}
