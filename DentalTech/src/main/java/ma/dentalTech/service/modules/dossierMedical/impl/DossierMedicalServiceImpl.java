package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierMedicalRepository;

    public DossierMedicalServiceImpl(DossierMedicalRepository dossierMedicalRepository) {
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public List<DossierMedical> getAll() {
        return dossierMedicalRepository.findAll();
    }

    @Override
    public DossierMedical getById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return dossierMedicalRepository.findById(id);
    }

    @Override
    public void create(DossierMedical d) {
        if (d == null) throw new IllegalArgumentException("DossierMedical null");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire (SQL NOT NULL)");
        if (d.getDateCreation() == null) d.setDateCreation(LocalDateTime.now());

        dossierMedicalRepository.create(d);
    }

    @Override
    public void update(DossierMedical d) {
        if (d == null) throw new IllegalArgumentException("DossierMedical null");
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        if (d.getDateDerniereModification() == null) d.setDateDerniereModification(LocalDateTime.now());

        dossierMedicalRepository.update(d);
    }

    @Override
    public void delete(DossierMedical d) {
        dossierMedicalRepository.delete(d);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        dossierMedicalRepository.deleteById(id);
    }

    @Override
    public Optional<DossierMedical> getByPatientId(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId obligatoire");
        return dossierMedicalRepository.findByPatientId(patientId);
    }

    @Override
    public List<DossierMedical> getByMedecinId(Long medecinId) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId obligatoire");
        return dossierMedicalRepository.findByMedecinId(medecinId);
    }

    @Override
    public List<DossierMedical> searchByNotes(String keyword) {
        return dossierMedicalRepository.searchByNotes(keyword == null ? "" : keyword);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return dossierMedicalRepository.existsById(id);
    }

    @Override
    public long count() {
        return dossierMedicalRepository.count();
    }

    @Override
    public List<DossierMedical> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit > 0");
        if (offset < 0) throw new IllegalArgumentException("offset >= 0");
        return dossierMedicalRepository.findPage(limit, offset);
    }

    @Override
    public Integer countActifs() {
        return dossierMedicalRepository.countActifs();
    }
}
