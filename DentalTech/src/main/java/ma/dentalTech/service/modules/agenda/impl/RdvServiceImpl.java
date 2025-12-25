package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import java.time.LocalDate;
import java.util.List;

public class RdvServiceImpl implements RdvService {

    private final RdvRepository rdvRepository;

    public RdvServiceImpl(RdvRepository rdvRepository) {
        this.rdvRepository = rdvRepository;
    }

    @Override
    public List<RDV> getAll() {
        return rdvRepository.findAll();
    }

    @Override
    public RDV getById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id obligatoire");
        return rdvRepository.findById(id);
    }

    @Override
    public void create(RDV r) {
        if (r == null) throw new IllegalArgumentException("RDV null");
        if (r.getId() != null) throw new IllegalArgumentException("Création RDV: id doit être null");

        if (r.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        if (r.getDetailJourneeId() == null) throw new IllegalArgumentException("detailJourneeId obligatoire");
        if (r.getDateRdv() == null) throw new IllegalArgumentException("dateRdv obligatoire");
        if (r.getHeure() == null) throw new IllegalArgumentException("heure obligatoire");
        if (r.getMotif() == null || r.getMotif().isBlank()) throw new IllegalArgumentException("motif obligatoire");

        // ✅ statut = Enum
        if (r.getStatut() == null) {
            r.setStatut(String.valueOf(EtatRendezVous.EN_COURS));
        }

        rdvRepository.create(r);
    }

    @Override
    public void update(RDV r) {
        if (r == null) throw new IllegalArgumentException("RDV null");
        if (r.getId() == null || r.getId() <= 0) throw new IllegalArgumentException("RDV id obligatoire");

        RDV old = rdvRepository.findById(r.getId());
        if (old == null) throw new IllegalArgumentException("RDV introuvable (id=" + r.getId() + ")");

        // validations minimales
        if (r.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        if (r.getDetailJourneeId() == null) throw new IllegalArgumentException("detailJourneeId obligatoire");
        if (r.getDateRdv() == null) throw new IllegalArgumentException("dateRdv obligatoire");
        if (r.getHeure() == null) throw new IllegalArgumentException("heure obligatoire");
        if (r.getMotif() == null || r.getMotif().isBlank()) throw new IllegalArgumentException("motif obligatoire");

        // si statut non fourni => garder l’ancien
        if (r.getStatut() == null) r.setStatut(old.getStatut());

        rdvRepository.update(r);
    }

    @Override
    public void delete(RDV r) {
        if (r == null || r.getId() == null) throw new IllegalArgumentException("RDV id obligatoire");
        rdvRepository.delete(r);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id obligatoire");
        rdvRepository.deleteById(id);
    }

    @Override
    public List<RDV> getByPatient(Long patientId) {
        if (patientId == null || patientId <= 0) throw new IllegalArgumentException("patientId obligatoire");
        return rdvRepository.findByPatientId(patientId);
    }

    @Override
    public List<RDV> getByDetailJournee(Long detailJourneeId) {
        if (detailJourneeId == null || detailJourneeId <= 0) throw new IllegalArgumentException("detailJourneeId obligatoire");
        return rdvRepository.findByDetailJourneeId(detailJourneeId);
    }

    @Override
    public List<RDV> getByListeAttente(Long listeAttenteId) {
        if (listeAttenteId == null || listeAttenteId <= 0) throw new IllegalArgumentException("listeAttenteId obligatoire");
        return rdvRepository.findByListeAttenteId(listeAttenteId);
    }

    @Override
    public List<RDV> getByDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        return rdvRepository.findByDate(date);
    }

    @Override
    public List<RDV> getByStatus(EtatRendezVous status) {
        if (status == null) throw new IllegalArgumentException("status obligatoire");
        return rdvRepository.findByStatus(status);
    }

    @Override
    public List<RDV> getUpcomingFromToday() {
        return rdvRepository.findUpcomingFromToday();
    }
}
