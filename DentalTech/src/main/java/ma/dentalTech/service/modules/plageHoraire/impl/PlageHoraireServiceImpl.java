package ma.dentalTech.service.modules.plageHoraire.impl;

import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;
import ma.dentalTech.service.modules.plageHoraire.api.PlageHoraireService;

import java.util.List;

public class PlageHoraireServiceImpl implements PlageHoraireService {

    private final PlageHoraireRepository plageRepo;

    public PlageHoraireServiceImpl(PlageHoraireRepository plageRepo) {
        this.plageRepo = plageRepo;
    }

    @Override public List<PlageHoraire> getAll() { return plageRepo.findAll(); }
    @Override public PlageHoraire getById(Long id) { return plageRepo.findById(id); }

    @Override
    public void create(PlageHoraire p) {
        if (p == null) throw new IllegalArgumentException("PlageHoraire null");
        if (p.getDetailJourneeId() == null) throw new IllegalArgumentException("detailJourneeId obligatoire");
        if (p.getHeureDebut() == null || p.getHeureFin() == null) throw new IllegalArgumentException("heureDebut/heureFin obligatoires");
        plageRepo.create(p);
    }

    @Override
    public void update(PlageHoraire p) {
        if (p == null || p.getId() == null) throw new IllegalArgumentException("PlageHoraire id obligatoire");
        plageRepo.update(p);
    }

    @Override public void delete(PlageHoraire p) { plageRepo.delete(p); }
    @Override public void deleteById(Long id) { plageRepo.deleteById(id); }

    @Override public List<PlageHoraire> getByDetailJournee(Long detailJourneeId) {
        return plageRepo.findByDetailJourneeId(detailJourneeId);
    }

    @Override public List<PlageHoraire> getDisponiblesByDetailJournee(Long detailJourneeId) {
        return plageRepo.findDisponiblesByDetailJournee(detailJourneeId);
    }
}
