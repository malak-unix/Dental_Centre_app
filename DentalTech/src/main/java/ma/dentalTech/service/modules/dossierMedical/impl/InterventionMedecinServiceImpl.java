package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.dentalTech.service.modules.dossierMedical.api.InterventionMedecinService;

import java.time.LocalDateTime;
import java.util.List;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {

    private final InterventionMedecinRepository interventionRepo;

    public InterventionMedecinServiceImpl(InterventionMedecinRepository interventionRepo) {
        this.interventionRepo = interventionRepo;
    }

    @Override
    public List<InterventionMedecin> getAll() {
        return interventionRepo.findAll();
    }

    @Override
    public InterventionMedecin getById(Long id) {
        if (id == null) return null;
        return interventionRepo.findById(id);
    }

    @Override
    public void create(InterventionMedecin i) {
        validateForCreate(i);
        normalize(i);
        interventionRepo.create(i);
    }

    @Override
    public void update(InterventionMedecin i) {
        validateForUpdate(i);
        normalize(i);
        interventionRepo.update(i);
    }

    @Override
    public void delete(InterventionMedecin i) {
        interventionRepo.delete(i);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        interventionRepo.deleteById(id);
    }

    @Override
    public List<InterventionMedecin> getByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        return interventionRepo.findByConsultationId(consultationId);
    }

    @Override
    public void deleteByConsultationId(Long consultationId) {
        if (consultationId == null) return;
        interventionRepo.deleteByConsultationId(consultationId);
    }

    @Override
    public List<InterventionMedecin> getByActeId(Long acteId) {
        if (acteId == null) return List.of();
        return interventionRepo.findByActeId(acteId);
    }

    @Override
    public List<InterventionMedecin> getByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return interventionRepo.findByDossierId(dossierId);
    }

    @Override
    public List<InterventionMedecin> getByPatientId(Long patientId) {
        if (patientId == null) return List.of();
        return interventionRepo.findByPatientId(patientId);
    }

    @Override
    public List<InterventionMedecin> getByDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return List.of();
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return interventionRepo.findByDateBetween(start, end);
    }

    @Override
    public List<InterventionMedecin> getByActeIdAndDateBetween(Long acteId, LocalDateTime start, LocalDateTime end) {
        if (acteId == null || start == null || end == null) return List.of();
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return interventionRepo.findByActeIdAndDateBetween(acteId, start, end);
    }

    @Override
    public Integer countPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0;
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return interventionRepo.countPourMedecinEtDate(medecinId, start, end);
    }

    @Override
    public Double sumMontantPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0.0;
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        Double v = interventionRepo.sumMontantPourMedecinEtDate(medecinId, start, end);
        return v == null ? 0.0 : v;
    }

    @Override
    public Double sumMontantPourConsultation(Long consultationId) {
        if (consultationId == null) return 0.0;
        Double v = interventionRepo.sumMontantPourConsultation(consultationId);
        return v == null ? 0.0 : v;
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return interventionRepo.existsById(id);
    }

    @Override
    public long count() {
        return interventionRepo.count();
    }

    @Override
    public List<InterventionMedecin> findPage(int limit, int offset) {
        if (limit <= 0) limit = 20;
        if (offset < 0) offset = 0;
        return interventionRepo.findPage(limit, offset);
    }

    // -------------------------
    // Helpers "métier"
    // -------------------------
    private void validateForCreate(InterventionMedecin i) {
        if (i == null) throw new IllegalArgumentException("InterventionMedecin null");
        if (i.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire");
        validateCommon(i);
    }

    private void validateForUpdate(InterventionMedecin i) {
        if (i == null || i.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (i.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire");
        validateCommon(i);
    }

    private void validateCommon(InterventionMedecin i) {
        if (i.getPrixDePatient() != null && i.getPrixDePatient() < 0) {
            throw new IllegalArgumentException("prixDePatient ne peut pas être négatif");
        }
        if (i.getNumDent() != null && (i.getNumDent() < 1 || i.getNumDent() > 48)) {
            // 1..32 (dents permanentes) ou 48 si vous utilisez une numérotation étendue
            throw new IllegalArgumentException("numDent invalide : " + i.getNumDent());
        }
    }

    private void normalize(InterventionMedecin i) {
        // Normalisation : si prix null => 0 (comme ton repo)
        if (i.getPrixDePatient() == null) i.setPrixDePatient(0.0);
    }
}
