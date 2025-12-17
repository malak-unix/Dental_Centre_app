package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.service.modules.dossierMedical.api.ActeService;

import java.time.LocalDateTime;
import java.util.List;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository acteRepository;

    public ActeServiceImpl(ActeRepository acteRepository) {
        this.acteRepository = acteRepository;
    }

    @Override
    public List<Acte> getAll() {
        return acteRepository.findAll();
    }

    @Override
    public Acte getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return acteRepository.findById(id);
    }

    @Override
    public void create(Acte a) {
        if (a == null) throw new IllegalArgumentException("Acte null");
        if (a.getLibelle() == null || a.getLibelle().isBlank()) throw new IllegalArgumentException("libelle obligatoire");
        if (a.getPrixBase() != null && a.getPrixBase() < 0) a.setPrixBase(0.0);

        acteRepository.create(a);
    }

    @Override
    public void update(Acte a) {
        if (a == null) throw new IllegalArgumentException("Acte null");
        if (a.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (a.getLibelle() == null || a.getLibelle().isBlank()) throw new IllegalArgumentException("libelle obligatoire");
        if (a.getPrixBase() != null && a.getPrixBase() < 0) a.setPrixBase(0.0);

        acteRepository.update(a);
    }

    @Override
    public void delete(Acte a) {
        acteRepository.delete(a);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        acteRepository.deleteById(id);
    }

    @Override
    public List<Acte> getByCategorie(String categorie) {
        if (categorie == null) categorie = "";
        return acteRepository.findByCategorie(categorie);
    }

    @Override
    public List<Acte> searchByLibelle(String keyword) {
        return acteRepository.searchByLibelle(keyword == null ? "" : keyword);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return acteRepository.existsById(id);
    }

    @Override
    public long count() {
        return acteRepository.count();
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit > 0");
        if (offset < 0) throw new IllegalArgumentException("offset >= 0");
        return acteRepository.findPage(limit, offset);
    }

    @Override
    public Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0;
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return acteRepository.countActesPourMedecinEtDate(medecinId, start, end);
    }

    @Override
    public Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0.0;
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return acteRepository.sumMontantActesPourMedecinEtDate(medecinId, start, end);
    }
}
