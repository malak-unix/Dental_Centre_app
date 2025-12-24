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
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaAppService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    // 1) Agenda semaine
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
            AgendaMensuel agenda = agendaRepo.findByMedecinAndMonth(medecinId, mois.name(), annee);

            if (agenda == null) {
                return AgendaMensuelDto.builder()
                        .id(null)
                        .medecinId(medecinId)
                        .mois(mois)
                        .annee(annee)
                        .build();
            }

            // Ici tu récupères les RDV mais ton DTO ne les expose pas encore.
            // On garde la logique côté service si tu ajoutes plus tard un champ "rendezVous".
            List<RDV> rdvsSemaine = new ArrayList<>();
            List<DetailJournee> jours = detailRepo.findByAgendaId(agenda.getId());

            for (DetailJournee dj : jours) {
                if (dj.getDateJour() != null && !dj.getDateJour().isBefore(start) && !dj.getDateJour().isAfter(end)) {
                    rdvsSemaine.addAll(rdvRepo.findByDetailJourneeId(dj.getId()));
                }
            }

            return AgendaMensuelDto.builder()
                    .id(agenda.getId())
                    .medecinId(agenda.getMedecinId())
                    .mois(agenda.getMois() != null ? agenda.getMois() : mois)
                    .annee(agenda.getAnnee() != null ? agenda.getAnnee() : annee)
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

        DetailJournee dj = detailRepo.findById(dto.getDetailJourneeId());
        if (dj == null) throw new ValidationException("DetailJournee introuvable");

        // ✅ FIX: comparaison enum (plus de equalsIgnoreCase)
        StatutJournee etat = (dj.getEtatJour() != null) ? dj.getEtatJour() : StatutJournee.OUVERT;
        if (etat == StatutJournee.FERME || etat == StatutJournee.FERIE || etat == StatutJournee.VACANCES) {
            throw new ValidationException("Journée non ouverte : impossible de planifier un RDV");
        }

        LocalTime h = dto.getHeure();
        if (dj.getHeureDebutTravail() != null && h.isBefore(dj.getHeureDebutTravail())) {
            throw new ValidationException("Heure avant début de travail");
        }
        if (dj.getHeureFinTravail() != null && h.isAfter(dj.getHeureFinTravail())) {
            throw new ValidationException("Heure après fin de travail");
        }

        // conflit: même detailJourneeId + même heure (simple)
        List<RDV> existing = rdvRepo.findByDetailJourneeId(dto.getDetailJourneeId());
        for (RDV r : existing) {
            if (r.getHeure() != null && r.getHeure().equals(dto.getHeure())) {
                String st = r.getStatut();
                if (st == null || !st.equalsIgnoreCase(EtatRendezVous.ANNULE.name())) {
                    throw new ValidationException("Conflit: un RDV existe déjà à cette heure");
                }
            }
        }

        RDV entity = toEntity(dto);

        EtatRendezVous statut = (dto.getStatut() != null) ? dto.getStatut() : EtatRendezVous.PREVU;
        entity.setStatut(statut.name());

        try {
            rdvRepo.create(entity);
            return toDto(entity);
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

            if (old.getStatut() != null && old.getStatut().equalsIgnoreCase(EtatRendezVous.ANNULE.name())) {
                throw new ValidationException("Impossible de modifier un RDV annulé");
            }

            RDV updated = toEntity(dto);
            updated.setId(rdvId);

            if (dto.getStatut() != null) updated.setStatut(dto.getStatut().name());
            else updated.setStatut(old.getStatut());

            rdvRepo.update(updated);
            return toDto(updated);

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

            if (r.getStatut() != null && r.getStatut().equalsIgnoreCase(EtatRendezVous.ANNULE.name())) {
                throw new ValidationException("Impossible de confirmer un RDV annulé");
            }

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
            return toDto(r);
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur consultation RDV", e);
        }
    }

    // ============================
    // Validation + mapping
    // ============================
    private void validateRdv(RdvDto dto) throws ValidationException {
        if (dto == null) throw new ValidationException("DTO RDV null");
        if (dto.getPatientId() == null) throw new ValidationException("patientId obligatoire");
        if (dto.getDetailJourneeId() == null) throw new ValidationException("detailJourneeId obligatoire");
        if (dto.getDateRdv() == null) throw new ValidationException("dateRdv obligatoire");
        if (dto.getHeure() == null) throw new ValidationException("heure obligatoire");
        if (dto.getMotif() == null || dto.getMotif().isBlank()) throw new ValidationException("motif obligatoire");
    }

    private RDV toEntity(RdvDto dto) {
        return RDV.builder()
                .id(dto.getId())
                .patientId(dto.getPatientId())
                .detailJourneeId(dto.getDetailJourneeId())
                .listeAttenteId(dto.getListeAttenteId())
                .dateRdv(dto.getDateRdv())
                .heure(dto.getHeure())
                .motif(dto.getMotif())
                .noteMedecin(null)
                .statut(dto.getStatut() != null ? dto.getStatut().name() : null)
                .build();
    }

    private RdvDto toDto(RDV r) {
        EtatRendezVous st = null;
        if (r.getStatut() != null) {
            try { st = EtatRendezVous.valueOf(r.getStatut()); }
            catch (Exception ignored) {}
        }

        return RdvDto.builder()
                .id(r.getId())
                .patientId(r.getPatientId())
                .detailJourneeId(r.getDetailJourneeId())
                .listeAttenteId(r.getListeAttenteId())
                .dateRdv(r.getDateRdv())
                .heure(r.getHeure())
                .motif(r.getMotif())
                .statut(st)
                .build();
    }

    private LocalDate startOfWeek(LocalDate d) {
        int shift = d.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        return d.minusDays(shift);
    }

    private Mois toMois(LocalDate d) {
        return Mois.values()[d.getMonthValue() - 1];
    }
}
