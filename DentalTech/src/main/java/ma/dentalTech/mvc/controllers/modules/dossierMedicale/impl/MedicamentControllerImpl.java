package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.MedicamentController;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class MedicamentControllerImpl implements MedicamentController {

    private final MedicamentRepository repo;

    public MedicamentControllerImpl(MedicamentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Medicament> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Medicament> searchByNom(String keyword) {
        if (keyword == null || keyword.isBlank()) return repo.findAll();
        return repo.searchByNom(keyword.trim());
    }

    @Override
    public Medicament getById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Medicament create(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null");
        if (m.getNom() == null || m.getNom().isBlank()) {
            throw new IllegalArgumentException("Nom obligatoire");
        }
        if (m.getDateCreation() == null) m.setDateCreation(LocalDateTime.now());
        repo.create(m);
        return m;
    }

    @Override
    public Medicament update(Medicament m) {
        if (m == null || m.getId() == null) throw new IllegalArgumentException("ID obligatoire");
        m.setDateDerniereModification(LocalDateTime.now());
        repo.update(m);
        return m;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        repo.deleteById(id);
    }
}
