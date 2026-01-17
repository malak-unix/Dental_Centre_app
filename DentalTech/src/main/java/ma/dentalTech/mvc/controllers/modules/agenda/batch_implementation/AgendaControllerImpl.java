package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.api.PlageHoraireService;
import ma.dentalTech.configuration.ApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class AgendaControllerImpl implements AgendaController {

    private final AgendaService agendaService;
    private PlageHoraireService plageService;

    public AgendaControllerImpl(AgendaService agendaService,
                                PlageHoraireService plageService) {
        this.agendaService = agendaService;
        this.plageService = plageService;
    }

    public AgendaControllerImpl(AgendaService agendaService) {
        this.agendaService = agendaService;
        this.plageService = null;
    }

    @Override
    public List<AgendaMensuelDto> getAllAgendas() {
        return agendaService.getAllAgendas().stream().map(this::toDto).toList();
    }

    @Override
    public AgendaMensuelDto getAgendaById(Long id) {
        AgendaMensuel a = agendaService.getAgendaById(id);
        return a == null ? null : toDto(a);
    }

    @Override
    public List<DetailJourneeDto> getDetailJourneesByAgendaId(Long agendaId) {
        return agendaService.getDetailsByAgenda(agendaId).stream().map(this::toDto).toList();
    }

    @Override
    public DetailJourneeDto getDetailJourneeById(Long id) {
        DetailJournee d = agendaService.getDetailById(id);
        return d == null ? null : toDto(d);
    }

    @Override
    public DetailJourneeDto getDetailJourneeByAgendaIdAndDate(Long agendaId, LocalDate dateJour) {
        DetailJournee d = agendaService.getDetailByAgendaAndDate(agendaId, dateJour);
        return d == null ? null : toDto(d);
    }

    @Override
    public DetailJourneeDto getDetailJourneeByMedecinAndDate(Long medecinId, LocalDate dateJour) {
        if (medecinId == null || dateJour == null) return null;
        String mois = Mois.values()[dateJour.getMonthValue() - 1].name();
        AgendaMensuel agenda = agendaService.getAgendaByMedecinMonth(medecinId, mois, dateJour.getYear());
        if (agenda == null) return null;
        DetailJournee d = agendaService.getDetailByAgendaAndDate(agenda.getId(), dateJour);
        return d == null ? null : toDto(d);
    }

    @Override
    public AgendaMensuelDto createAgenda(AgendaMensuelDto dto) {
        AgendaMensuel a = new AgendaMensuel();
        a.setMedecinId(dto.getMedecinId());
        a.setMois(dto.getMois());
        a.setAnnee(dto.getAnnee());
        agendaService.createAgenda(a);
        return toDto(a);
    }

    @Override
    public DetailJourneeDto createDetailJournee(DetailJourneeDto dto) {
        DetailJournee d = new DetailJournee();
        d.setAgendaId(dto.getAgendaId());
        d.setDateJour(dto.getDateJour());
        d.setHeureDebutTravail(dto.getHeureDebutTravail());
        d.setHeureFinTravail(dto.getHeureFinTravail());
        d.setEtatJour(parseEtat(dto.getEtatJour()));
        d.setCommentaire(dto.getCommentaire());
        agendaService.createDetail(d);
        return toDto(d);
    }

    @Override
    public DetailJourneeDto updateDetailJournee(DetailJourneeDto dto) {
        DetailJournee d = agendaService.getDetailById(dto.getId());
        if (d == null) throw new IllegalArgumentException("DetailJournee introuvable");
        d.setAgendaId(dto.getAgendaId());
        d.setDateJour(dto.getDateJour());
        d.setHeureDebutTravail(dto.getHeureDebutTravail());
        d.setHeureFinTravail(dto.getHeureFinTravail());
        d.setEtatJour(parseEtat(dto.getEtatJour()));
        d.setCommentaire(dto.getCommentaire());
        agendaService.updateDetail(d);
        return toDto(d);
    }

    @Override
    public List<PlageHoraire> getPlagesByDetailJournee(Long detailJourneeId) {
        PlageHoraireService svc = ensurePlageService();
        return svc.getByDetailJournee(detailJourneeId);
    }

    @Override
    public PlageHoraire createPlage(PlageHoraire plage) {
        PlageHoraireService svc = ensurePlageService();
        svc.create(plage);
        return plage;
    }

    @Override
    public void deletePlage(Long plageId) {
        PlageHoraireService svc = ensurePlageService();
        svc.deleteById(plageId);
    }

    private AgendaMensuelDto toDto(AgendaMensuel a) {
        return AgendaMensuelDto.builder()
                .id(a.getId())
                .medecinId(a.getMedecinId())
                .mois(a.getMois() != null ? a.getMois() : Mois.JANVIER)
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
                .etatJour(d.getEtatJour() == null ? null : d.getEtatJour().name())
                .commentaire(d.getCommentaire())
                .build();
    }

    private StatutJournee parseEtat(String v) {
        if (v == null || v.isBlank()) return StatutJournee.OUVERT;
        try { return StatutJournee.valueOf(v.trim().toUpperCase()); }
        catch (Exception e) { return StatutJournee.OUVERT; }
    }

    private PlageHoraireService ensurePlageService() {
        if (plageService == null) {
            plageService = ApplicationContext.getBean(PlageHoraireService.class);
        }
        if (plageService == null) {
            throw new IllegalStateException("PlageHoraireService introuvable");
        }
        return plageService;
    }
}
