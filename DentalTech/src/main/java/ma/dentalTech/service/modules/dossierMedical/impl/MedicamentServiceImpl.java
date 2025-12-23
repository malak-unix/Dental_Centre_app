package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.dentalTech.service.modules.dossierMedical.api.MedicamentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentServiceImpl(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    @Override
    public List<Medicament> getAll() {
        return medicamentRepository.findAll();
    }

    @Override
    public Medicament getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return medicamentRepository.findById(id);
    }

    @Override
    public void create(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null");
        if (m.getNom() == null || m.getNom().isBlank()) throw new IllegalArgumentException("nom obligatoire");

        // valeurs par défaut (optionnel)
        if (m.getPrixUnitaire() != null && m.getPrixUnitaire() < 0) m.setPrixUnitaire(0.0);
        if (m.getDateCreation() == null) m.setDateCreation(LocalDateTime.now());

        medicamentRepository.create(m);
    }

    @Override
    public void update(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null");
        if (m.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (m.getNom() == null || m.getNom().isBlank()) throw new IllegalArgumentException("nom obligatoire");

        if (m.getPrixUnitaire() != null && m.getPrixUnitaire() < 0) m.setPrixUnitaire(0.0);
        if (m.getDateDerniereModification() == null) m.setDateDerniereModification(LocalDateTime.now());

        medicamentRepository.update(m);
    }

    @Override
    public void delete(Medicament m) {
        medicamentRepository.delete(m);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        medicamentRepository.deleteById(id);
    }

    @Override
    public List<Medicament> searchByNom(String keyword) {
        return medicamentRepository.searchByNom(keyword == null ? "" : keyword);
    }

    @Override
    public Optional<Medicament> getByNomExact(String nom) {
        if (nom == null || nom.isBlank()) return Optional.empty();
        return medicamentRepository.findByNomExact(nom);
    }

    @Override
    public List<Medicament> getByRemboursable(boolean remboursable) {
        return medicamentRepository.findByRemboursable(remboursable);
    }

    @Override
    public List<Medicament> getByLaboratoire(String laboratoire) {
        if (laboratoire == null) laboratoire = "";
        return medicamentRepository.findByLaboratoire(laboratoire);
    }

    @Override
    public List<Medicament> getByType(String typeMedicament) {
        if (typeMedicament == null) typeMedicament = "";
        return medicamentRepository.findByType(typeMedicament);
    }

    @Override
    public List<Medicament> getByForme(FormeMedicament forme) {
        if (forme == null) throw new IllegalArgumentException("forme obligatoire");
        return medicamentRepository.findByForme(forme);
    }

    @Override
    public List<Medicament> getByPrixBetween(Double min, Double max) {
        return medicamentRepository.findByPrixBetween(min, max);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return medicamentRepository.existsById(id);
    }

    @Override
    public long count() {
        return medicamentRepository.count();
    }

    @Override
    public List<Medicament> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit > 0");
        if (offset < 0) throw new IllegalArgumentException("offset >= 0");
        return medicamentRepository.findPage(limit, offset);
    }
}
