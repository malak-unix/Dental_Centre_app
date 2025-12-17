package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // -----------------------------
    // CRUD
    // -----------------------------
    @Override
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }

    @Override
    public Prescription findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return prescriptionRepository.findById(id);
    }

    @Override
    public void create(Prescription p) {
        validateForCreate(p);

        if (p.getQuantite() <= 0) p.setQuantite(1);
        if (p.getDureeEnJours() < 0) p.setDureeEnJours(0);
        if (p.getDateCreation() == null) p.setDateCreation(LocalDateTime.now());

        prescriptionRepository.create(p);
    }

    @Override
    public void update(Prescription p) {
        validateForUpdate(p);

        if (p.getQuantite() <= 0) p.setQuantite(1);
        if (p.getDureeEnJours() < 0) p.setDureeEnJours(0);
        if (p.getDateDerniereModification() == null) p.setDateDerniereModification(LocalDateTime.now());

        prescriptionRepository.update(p);
    }

    @Override
    public void delete(Prescription p) {
        prescriptionRepository.delete(p);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        prescriptionRepository.deleteById(id);
    }

    // -----------------------------
    // Spécifiques
    // -----------------------------
    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }

    @Override
    public void deleteByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
        prescriptionRepository.deleteByOrdonnanceId(ordonnanceId);
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        if (medicamentId == null) throw new IllegalArgumentException("medicamentId obligatoire");
        return prescriptionRepository.findByMedicamentId(medicamentId);
    }

    @Override
    public long countByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
        return prescriptionRepository.countByOrdonnanceId(ordonnanceId);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return prescriptionRepository.existsById(id);
    }

    // -----------------------------
    // Helpers
    // -----------------------------
    private void validateForCreate(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
        // medicamentId est nullable en DB -> OK
    }

    private void validateForUpdate(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null");
        if (p.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
    }
}
