package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.service.modules.agenda.api.RdvAppService;
import ma.dentalTech.service.modules.agenda.mappers.RdvMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RdvAppServiceImpl implements RdvAppService {

    private final RdvRepository rdvRepo;
    private final DetailJourneeRepository detailRepo;

    public RdvAppServiceImpl(RdvRepository rdvRepo, DetailJourneeRepository detailRepo) {
        this.rdvRepo = rdvRepo;
        this.detailRepo = detailRepo;
    }

    @Override
    public List<RdvDto> getAll() {
        return rdvRepo.findAll().stream().map(RdvMapper::toDto).toList();
    }

    @Override
    public RdvDto getById(Long id) {
        requireId(id, "rdvId");
        return RdvMapper.toDto(rdvRepo.findById(id));
    }

    @Override
    public RdvDto create(RdvDto dto) {
        validateCreate(dto);

        // règles planning
        DetailJournee dj = detailRepo.findById(dto.getDetailJourneeId());
        if (dj == null) throw new IllegalArgumentException("DetailJournee introuvable");
        checkJourneeOuverte(dj);
        checkHeureDansPlage(dj, dto.getHeure());
        checkConflitHoraire(dto.getDetailJourneeId(), dto.getHeure());

        if (dto.getStatut() == null) dto.setStatut(EtatRendezVous.PREVU);

        RDV entity = RdvMapper.toEntity(dto);
        rdvRepo.create(entity);

        // si ton repo remplit l'id après create -> ok
        return RdvMapper.toDto(entity);
    }

    @Override
    public RdvDto update(RdvDto dto) {
        validateUpdate(dto);

        RDV old = rdvRepo.findById(dto.getId());
        if (old == null) throw new IllegalArgumentException("RDV introuvable (id=" + dto.getId() + ")");

        // règles planning (si on change detailJournee / heure)
        DetailJournee dj = detailRepo.findById(dto.getDetailJourneeId());
        if (dj == null) throw new IllegalArgumentException("DetailJournee introuvable");
        checkJourneeOuverte(dj);
        checkHeureDansPlage(dj, dto.getHeure());

        // conflit horaire : autoriser si c'est le même rdv (on ignore lui-même)
        List<RDV> sameDay = rdvRepo.findByDetailJourneeId(dto.getDetailJourneeId());
        for (RDV r : sameDay) {
            if (r.getId() != null && r.getId().equals(dto.getId())) continue;
            if (dto.getHeure() != null && dto.getHeure().equals(r.getHeure())) {
                throw new IllegalArgumentException("Conflit: un RDV existe déjà à " + dto.getHeure());
            }
        }

        // si statut null => garder ancien
        if (dto.getStatut() == null) dto.setStatut(parse(old.getStatut()));

        RDV updated = RdvMapper.toEntity(dto);
        rdvRepo.update(updated);

        return RdvMapper.toDto(updated);
    }

    @Override
    public void deleteById(Long id) {
        requireId(id, "rdvId");
        rdvRepo.deleteById(id);
    }

    @Override
    public RdvDto confirmer(Long rdvId) {
        requireId(rdvId, "rdvId");
        RDV r = rdvRepo.findById(rdvId);
        if (r == null) throw new IllegalArgumentException("RDV introuvable");
        EtatRendezVous etat = parse(r.getStatut());
        if (etat != EtatRendezVous.PREVU) {
            throw new IllegalArgumentException("Seuls les RDV PREVU peuvent être confirmés");
        }
        r.setStatut(EtatRendezVous.CONFIRME.name());
        rdvRepo.update(r);
        return RdvMapper.toDto(r);
    }

    @Override
    public RdvDto annuler(Long rdvId) {
        requireId(rdvId, "rdvId");
        RDV r = rdvRepo.findById(rdvId);
        if (r == null) throw new IllegalArgumentException("RDV introuvable");
        r.setStatut(EtatRendezVous.ANNULE.name());
        rdvRepo.update(r);
        return RdvMapper.toDto(r);
    }

    @Override
    public List<RdvDto> getByPatient(Long patientId) {
        requireId(patientId, "patientId");
        return rdvRepo.findByPatientId(patientId).stream().map(RdvMapper::toDto).toList();
    }

    @Override
    public List<RdvDto> getByDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        return rdvRepo.findByDate(date).stream().map(RdvMapper::toDto).toList();
    }

    @Override
    public List<RdvDto> getByStatus(EtatRendezVous status) {
        if (status == null) throw new IllegalArgumentException("status obligatoire");
        return rdvRepo.findByStatus(status).stream().map(RdvMapper::toDto).toList();
    }

    // =========================
    // Validations + règles
    // =========================

    private void validateCreate(RdvDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto null");
        if (dto.getId() != null) throw new IllegalArgumentException("Création: id doit être null");
        validateCommon(dto);
    }

    private void validateUpdate(RdvDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto null");
        requireId(dto.getId(), "id");
        validateCommon(dto);
    }

    private void validateCommon(RdvDto dto) {
        requireId(dto.getPatientId(), "patientId");
        requireId(dto.getDetailJourneeId(), "detailJourneeId");
        if (dto.getDateRdv() == null) throw new IllegalArgumentException("dateRdv obligatoire");
        if (dto.getHeure() == null) throw new IllegalArgumentException("heure obligatoire");
        if (dto.getMotif() == null || dto.getMotif().isBlank()) throw new IllegalArgumentException("motif obligatoire");
        if (dto.getTypeRdv() == null) throw new IllegalArgumentException("typeRdv obligatoire");
    }

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new IllegalArgumentException(name + " obligatoire");
    }

    private void checkJourneeOuverte(DetailJournee dj) {
        // si null => considérer comme OUVERT (ou refuse si tu préfères)
        StatutJournee etat = (dj.getEtatJour() != null) ? dj.getEtatJour() : StatutJournee.OUVERT;

        if (etat == StatutJournee.FERME || etat == StatutJournee.FERIE || etat == StatutJournee.VACANCES) {
            throw new IllegalArgumentException("Journée non ouverte: impossible de planifier");
        }
    }
    private void checkHeureDansPlage(DetailJournee dj, LocalTime heure) {
        LocalTime debut = dj.getHeureDebutTravail();
        LocalTime fin = dj.getHeureFinTravail();
        if (debut != null && heure.isBefore(debut)) {
            throw new IllegalArgumentException("Heure en dehors de la plage (avant début travail)");
        }
        if (fin != null && heure.isAfter(fin)) {
            throw new IllegalArgumentException("Heure en dehors de la plage (après fin travail)");
        }
    }

    private void checkConflitHoraire(Long detailJourneeId, LocalTime heure) {
        List<RDV> rdvs = rdvRepo.findByDetailJourneeId(detailJourneeId);
        for (RDV r : rdvs) {
            if (heure.equals(r.getHeure())) {
                throw new IllegalArgumentException("Conflit: un RDV existe déjà à " + heure);
            }
        }
    }

    private EtatRendezVous parse(String s) {
        if (s == null || s.isBlank()) return null;
        try { return EtatRendezVous.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
}
