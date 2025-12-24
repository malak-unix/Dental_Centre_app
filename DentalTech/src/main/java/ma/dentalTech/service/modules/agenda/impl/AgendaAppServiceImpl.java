package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaAppService;
import ma.dentalTech.service.modules.agenda.mappers.RdvMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AgendaAppServiceImpl implements AgendaAppService {

    private final AgendaMensuelRepository agendaRepo;
    private final DetailJourneeRepository detailRepo;
    private final RdvRepository rdvRepo;

    public AgendaAppServiceImpl(AgendaMensuelRepository agendaRepo,
                                DetailJourneeRepository detailRepo,
                                RdvRepository rdvRepo) {
        this.agendaRepo = agendaRepo;
        this.detailRepo = detailRepo;
        this.rdvRepo = rdvRepo;
    }

    // ============================
    // 1) Agenda semaine (maquette)
    // ============================
    @Override
    public AgendaMensuelDto consulterAgendaSemaine(Long medecinId, LocalDate dateDansSemaine)
            throws ValidationException, ServiceException {

        if (medecinId == null) throw new ValidationException("medecinId obligatoire");
        if (dateDansSemaine == null) throw new ValidationException("date obligatoire");

        LocalDate start = startOfWeek(dateDansSemaine);
        LocalDate end = start.plusDays(6);

        Mois mois = toMois(dateDansSemaine);
        int annee = dateDansSemaine.getYear();

        try {
            // ✅ ton repo attend String mois
            AgendaMensuel agenda = agendaRepo.findByMedecinAndMonth(medecinId, mois.name(), annee);

            // si agenda non créé → DTO vide mais avec semaine calculée
            if (agenda == null) {
                return AgendaMensuelDto.builder()
                        .id(null)
                        .medecinId(medecinId)
                        .mois(mois)
                        .annee(annee)
                        .joursSemaine(new ArrayList<>())
                        .rdvsSemaine(new ArrayList<>())
                        .build();
            }

            List<DetailJournee> tousJours = detailRepo.findByAgendaId(agenda.getId());

            // Filtrer semaine
            List<DetailJournee> joursSemaine = new ArrayList<>();
            for (DetailJournee dj : tousJours) {
                if (dj.getDateJour() == null) continue;
                if (!dj.getDateJour().isBefore(start) && !dj.getDateJour().isAfter(end)) {
                    joursSemaine.add(dj);
                }
            }
            joursSemaine.sort(Comparator.comparing(DetailJournee::getDateJour));

            // Récupérer rdv semaine
            List<RdvDto> rdvsSemaine = new ArrayList<>();
            for (DetailJournee dj : joursSemaine) {
                List<RDV> rdvs = rdvRepo.findByDetailJourneeId(dj.getId());
                for (RDV r : rdvs) rdvsSemaine.add(RdvMapper.toDto(r));
            }

            List<DetailJourneeDto> joursDto = joursSemaine.stream().map(this::toDetailDto).toList();

            return AgendaMensuelDto.builder()
                    .id(agenda.getId())
                    .medecinId(agenda.getMedecinId())
                    .mois(agenda.getMois() != null ? agenda.getMois() : mois)
                    .annee(agenda.getAnnee())
                    .joursSemaine(joursDto)
                    .rdvsSemaine(rdvsSemaine)
                    .build();

        } catch (Exception e) {
            throw new ServiceException("Erreur consultation agenda semaine", e);
        }
    }

    // ============================
    // 2) Créer RDV
    // ============================
    @Override
    public RdvDto creerRdv(RdvDto dto) throws ValidationException, ServiceException {
        validateRdv(dto);

        try {
            DetailJournee dj = detailRepo.findById(dto.getDetailJourneeId());
            if (dj == null) throw new ValidationException("DetailJournee introuvable");

            // ✅ StatutJournee enum (plus de equalsIgnoreCase)
            if (dj.getEtatJour() == StatutJournee.FERME) {
                throw new ValidationException("Journée fermée : impossible de planifier un RDV");
            }

            LocalTime h = dto.getHeure();
            if (dj.getHeureDebutTravail() != null && h.isBefore(dj.getHeureDebutTravail())) {
                throw new ValidationException("Heure avant début de travail");
            }
            if (dj.getHeureFinTravail() != null && h.isAfter(dj.getHeureFinTravail())) {
                throw new ValidationException("Heure après fin de travail");
            }

            // conflit horaire : même detailJournee + même heure (ignorer ANNULE)
            List<RDV> existing = rdvRepo.findByDetailJourneeId(dto.getDetailJourneeId());
            for (RDV r : existing) {
                if (r.getHeure() != null && r.getHeure().equals(dto.getHeure())) {
                    EtatRendezVous st = parseEtat(r.getStatut());
                    if (st == null || st != EtatRendezVous.ANNULE) {
                        throw new ValidationException("Conflit: un RDV existe déjà à cette heure");
                    }
                }
            }

            RDV entity = RdvMapper.toEntity(dto);

            // default statut
            EtatRendezVous statut = (dto.getStatut() != null) ? dto.getStatut() : EtatRendezVous.PREVU;
            entity.setStatut(statut.name());

            rdvRepo.create(entity);
            return RdvMapper.toDto(entity);

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur création RDV", e);
        }
    }

    // ============================
    // 3) Modifier RDV
    // ============================
    @Override
    public RdvDto modifierRdv(Long rdvId, RdvDto dto) throws ValidationException, ServiceException {
        if (rdvId == null) throw new ValidationException("rdvId obligatoire");
        validateRdv(dto);

        try {
            RDV old = rdvRepo.findById(rdvId);
            if (old == null) throw new ValidationException("RDV introuvable");

            EtatRendezVous oldSt = parseEtat(old.getStatut());
            if (oldSt == EtatRendezVous.ANNULE) {
                throw new ValidationException("Impossible de modifier un RDV annulé");
            }

            RDV updated = RdvMapper.toEntity(dto);
            updated.setId(rdvId);

            // garder statut ancien si dto.statut null
            if (dto.getStatut() != null) updated.setStatut(dto.getStatut().name());
            else updated.setStatut(old.getStatut());

            rdvRepo.update(updated);
            return RdvMapper.toDto(updated);

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur modification RDV", e);
        }
    }

    // ============================
    // 4) Annuler / confirmer / consulter
    // ============================
    @Override
    public void annulerRdv(Long rdvId) throws ValidationException, ServiceException {
        if (rdvId == null) throw new ValidationException("rdvId obligatoire");
        try {
            RDV r = rdvRepo.findById(rdvId);
            if (r == null) throw new ValidationException("RDV introuvable");

            r.setStatut(EtatRendezVous.ANNULE.name());
            rdvRepo.update(r);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur annulation RDV", e);
        }
    }

    @Override
    public void confirmerRdv(Long rdvId) throws ValidationException, ServiceException {
        if (rdvId == null) throw new ValidationException("rdvId obligatoire");
        try {
            RDV r = rdvRepo.findById(rdvId);
            if (r == null) throw new ValidationException("RDV introuvable");

            EtatRendezVous st = parseEtat(r.getStatut());
            if (st == EtatRendezVous.ANNULE) throw new ValidationException("Impossible de confirmer un RDV annulé");

            r.setStatut(EtatRendezVous.CONFIRME.name());
            rdvRepo.update(r);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur confirmation RDV", e);
        }
    }

    @Override
    public RdvDto consulterRdv(Long rdvId) throws ValidationException, ServiceException {
        if (rdvId == null) throw new ValidationException("rdvId obligatoire");
        try {
            RDV r = rdvRepo.findById(rdvId);
            if (r == null) throw new ValidationException("RDV introuvable");
            return RdvMapper.toDto(r);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur consultation RDV", e);
        }
    }

    // ============================
    // Helpers
    // ============================
    private void validateRdv(RdvDto dto) throws ValidationException {
        if (dto == null) throw new ValidationException("DTO RDV null");
        if (dto.getPatientId() == null) throw new ValidationException("patientId obligatoire");
        if (dto.getDetailJourneeId() == null) throw new ValidationException("detailJourneeId obligatoire");
        if (dto.getDateRdv() == null) throw new ValidationException("dateRdv obligatoire");
        if (dto.getHeure() == null) throw new ValidationException("heure obligatoire");
        if (dto.getMotif() == null || dto.getMotif().isBlank()) throw new ValidationException("motif obligatoire");
        if (dto.getTypeRdv() == null) throw new ValidationException("typeRdv obligatoire");
    }

    private DetailJourneeDto toDetailDto(DetailJournee dj) {
        return DetailJourneeDto.builder()
                .id(dj.getId())
                .agendaId(dj.getAgendaId())
                .dateJour(dj.getDateJour())
                .heureDebutTravail(dj.getHeureDebutTravail())
                .heureFinTravail(dj.getHeureFinTravail())
                .etatJour(dj.getEtatJour()) // ✅ enum direct
                .commentaire(dj.getCommentaire())
                .build();
    }

    private EtatRendezVous parseEtat(String s) {
        if (s == null || s.isBlank()) return null;
        try { return EtatRendezVous.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    private LocalDate startOfWeek(LocalDate d) {
        int shift = d.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        return d.minusDays(shift);
    }

    private Mois toMois(LocalDate d) {
        return Mois.values()[d.getMonthValue() - 1];
    }
}
