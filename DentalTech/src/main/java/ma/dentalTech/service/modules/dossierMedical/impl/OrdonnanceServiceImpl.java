package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.dentalTech.service.modules.dossierMedical.api.OrdonnanceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final PrescriptionRepository prescriptionRepository;

    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository,
                                 PrescriptionRepository prescriptionRepository) {
        this.ordonnanceRepository = ordonnanceRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // -----------------------------
    // CRUD
    // -----------------------------
    @Override
    public List<Ordonnance> findAll() {
        return ordonnanceRepository.findAll();
    }

    @Override
    public Ordonnance findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        return ordonnanceRepository.findById(id);
    }

    @Override
    public void create(Ordonnance o) {
        validateForCreate(o);

        if (o.getDate() == null) o.setDate(LocalDate.now()); // DB: date_ordo NOT NULL
        if (o.getDateCreation() == null) o.setDateCreation(LocalDateTime.now());

        ordonnanceRepository.create(o);
    }

    @Override
    public void update(Ordonnance o) {
        validateForUpdate(o);

        if (o.getDate() == null) o.setDate(LocalDate.now());
        if (o.getDateDerniereModification() == null) o.setDateDerniereModification(LocalDateTime.now());

        ordonnanceRepository.update(o);
    }

    @Override
    public void delete(Ordonnance o) {
        ordonnanceRepository.delete(o);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        ordonnanceRepository.deleteById(id);
    }

    // -----------------------------
    // Recherche
    // -----------------------------
    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        if (dossierId == null) throw new IllegalArgumentException("dossierId obligatoire");
        return ordonnanceRepository.findByDossierId(dossierId);
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        if (consultationId == null) throw new IllegalArgumentException("consultationId obligatoire");
        return ordonnanceRepository.findByConsultationId(consultationId);
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        return ordonnanceRepository.findByDate(date);
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end obligatoires");
        if (end.isBefore(start)) throw new IllegalArgumentException("end doit être >= start");
        return ordonnanceRepository.findByDateBetween(start, end);
    }

    // -----------------------------
    // Utilitaires
    // -----------------------------
    @Override
    public long count() {
        return ordonnanceRepository.count();
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {   // ✅ IMPORTANT: findPage (pas getPage)
        if (limit <= 0) throw new IllegalArgumentException("limit doit être > 0");
        if (offset < 0) throw new IllegalArgumentException("offset doit être >= 0");
        return ordonnanceRepository.findPage(limit, offset);
    }

    @Override
    public Ordonnance findLastByDossierId(Long dossierId) {
        if (dossierId == null) throw new IllegalArgumentException("dossierId obligatoire");

        // si ton repo n'a pas encore implémenté findLast..., on fait fallback
        Ordonnance last = ordonnanceRepository.findLastByDossierId(dossierId);
        if (last != null) return last;

        List<Ordonnance> list = ordonnanceRepository.findByDossierId(dossierId);
        return (list == null || list.isEmpty()) ? null : list.get(0); // tri DESC dans repo -> le premier = last
    }

    @Override
    public Ordonnance findLastByConsultationId(Long consultationId) {
        if (consultationId == null) throw new IllegalArgumentException("consultationId obligatoire");

        Ordonnance last = ordonnanceRepository.findLastByConsultationId(consultationId);
        if (last != null) return last;

        List<Ordonnance> list = ordonnanceRepository.findByConsultationId(consultationId);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return ordonnanceRepository.existsById(id);
    }

    // -----------------------------
    // Métier: ordonnance + prescriptions
    // -----------------------------
    @Override
    public void createWithPrescriptions(Ordonnance ordonnance, List<Prescription> prescriptions) {
        validateForCreate(ordonnance);

        if (ordonnance.getDate() == null) ordonnance.setDate(LocalDate.now());
        if (ordonnance.getDateCreation() == null) ordonnance.setDateCreation(LocalDateTime.now());

        // 1) créer ordonnance
        ordonnanceRepository.create(ordonnance);

        // 2) créer prescriptions
        if (prescriptions != null) {
            for (Prescription p : prescriptions) {
                if (p == null) continue;
                p.setOrdonnanceId(ordonnance.getId());
                normalizePrescription(p);
                prescriptionRepository.create(p);
            }
        }
    }

    @Override
    public void replacePrescriptions(Long ordonnanceId, List<Prescription> prescriptions) {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");

        if (!ordonnanceRepository.existsById(ordonnanceId)) {
            throw new IllegalArgumentException("Ordonnance introuvable: id=" + ordonnanceId);
        }

        // 1) delete anciennes
        prescriptionRepository.deleteByOrdonnanceId(ordonnanceId);

        // 2) insert nouvelles
        if (prescriptions != null) {
            for (Prescription p : prescriptions) {
                if (p == null) continue;
                p.setOrdonnanceId(ordonnanceId);
                normalizePrescription(p);
                prescriptionRepository.create(p);
            }
        }
    }

    @Override
    public List<Prescription> findPrescriptions(Long ordonnanceId) {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }

    // -----------------------------
    // Validation helpers
    // -----------------------------
    private void validateForCreate(Ordonnance o) {
        if (o == null) throw new IllegalArgumentException("Ordonnance null");

        // DB autorise null, mais métier: éviter une ordonnance "orpheline"
        if (o.getDossierId() == null && o.getConsultationId() == null) {
            throw new IllegalArgumentException("Ordonnance doit avoir dossierId OU consultationId");
        }
    }

    private void validateForUpdate(Ordonnance o) {
        if (o == null) throw new IllegalArgumentException("Ordonnance null");
        if (o.getId() == null) throw new IllegalArgumentException("id obligatoire");
    }

    private void normalizePrescription(Prescription p) {
        if (p.getOrdonnanceId() == null) {
            throw new IllegalArgumentException("ordonnanceId obligatoire dans Prescription");
        }
        if (p.getQuantite() <= 0) p.setQuantite(1);
        if (p.getDureeEnJours() < 0) p.setDureeEnJours(0);
        if (p.getDateCreation() == null) p.setDateCreation(LocalDateTime.now());
    }
}
