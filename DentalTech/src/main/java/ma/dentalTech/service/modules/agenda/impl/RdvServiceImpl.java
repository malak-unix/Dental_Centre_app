package ma.dentalTech.service.modules.agenda.impl;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.agenda.RDV;
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
        return rdvRepository.findById(id);
    }

    @Override
    public void create(RDV r) {
        if (r == null) throw new IllegalArgumentException("RDV null");
        if (r.getDateRdv() == null) throw new IllegalArgumentException("dateRdv obligatoire");

        // statut est String dans l'entity => on met la valeur enum en .name()
        if (r.getStatut() == null || r.getStatut().isBlank()) {
            r.setStatut(EtatRendezVous.PREVU.name());
        }

        rdvRepository.create(r);
    }

    @Override
    public void update(RDV r) {
        if (r == null || r.getId() == null) throw new IllegalArgumentException("RDV id obligatoire");
        rdvRepository.update(r);
    }

    @Override
    public void delete(RDV r) {
        rdvRepository.delete(r);
    }

    @Override
    public void deleteById(Long id) {
        rdvRepository.deleteById(id);
    }

    @Override
    public List<RDV> getByPatient(Long patientId) {
        return rdvRepository.findByPatientId(patientId);
    }

    @Override
    public List<RDV> getByDetailJournee(Long detailJourneeId) {
        return rdvRepository.findByDetailJourneeId(detailJourneeId);
    }

    @Override
    public List<RDV> getByListeAttente(Long listeAttenteId) {
        return rdvRepository.findByListeAttenteId(listeAttenteId);
    }

    @Override
    public List<RDV> getByDate(LocalDate date) {
        return rdvRepository.findByDate(date);
    }

    @Override
    public List<RDV> getByStatus(EtatRendezVous status) {
        return rdvRepository.findByStatus(status);
    }

    @Override
    public List<RDV> getUpcomingFromToday() {
        return rdvRepository.findUpcomingFromToday();
    }
}
