package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;

    public PatientServiceImpl(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Override
    public List<Patient> getAll() {
        try {
            return patientRepo.findAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur getAll patients", e);
        }
    }

    @Override
    public Patient getById(Long id) {
        requireId(id, "patientId");
        try {
            Optional<Patient> opt = patientRepo.findById(id);
            return opt.orElseThrow(() -> ServiceException.notFound("Patient introuvable id=" + id));
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur getById patient", e);
        }
    }

    @Override
    public void create(Patient p) {
        validateCreate(p);
        try {
            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Optional<Patient> exist = patientRepo.findByTelephone(p.getTelephone());
                if (exist.isPresent()) throw ServiceException.validation("Téléphone déjà utilisé");
            }
            patientRepo.create(p);
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur create patient", e);
        }
    }

    @Override
    public void update(Patient p) {
        validateUpdate(p);
        try {
            Optional<Patient> oldOpt = patientRepo.findById(p.getId());
            Patient old = oldOpt.orElseThrow(() -> ServiceException.notFound("Patient introuvable id=" + p.getId()));

            // Unicité téléphone si changé
            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Optional<Patient> existTel = patientRepo.findByTelephone(p.getTelephone());
                if (existTel.isPresent() && !existTel.get().getId().equals(p.getId())) {
                    throw ServiceException.validation("Téléphone déjà utilisé");
                }
            }

            // Pas de baseEntityId ici (car la méthode n'existe pas dans ton Patient)
            // Si tu veux garder des champs d'audit, fais-le dans l'entité BaseEntity (dateModification/modifiePar...) si existant.

            patientRepo.update(p);

        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur update patient", e);
        }
    }

    @Override
    public void delete(Patient p) {
        if (p == null || p.getId() == null) return;
        deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        requireId(id, "patientId");
        try {
            patientRepo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur deleteById patient", e);
        }
    }

    /**
     * Ton repo n'a pas findByNom => on filtre en mémoire via findAll()
     */
    @Override
    public List<Patient> searchByNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw ServiceException.validation("nom obligatoire");
        }
        try {
            String needle = normalize(nom);
            return patientRepo.findAll().stream()
                    .filter(p -> normalize(p.getNom()).contains(needle))
                    .toList();
        } catch (Exception e) {
            throw new ServiceException("Erreur recherche patient par nom", e);
        }
    }

    @Override
    public Patient getByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw ServiceException.validation("telephone obligatoire");
        }
        try {
            Optional<Patient> opt = patientRepo.findByTelephone(telephone);
            return opt.orElseThrow(() -> ServiceException.notFound("Aucun patient trouvé pour ce téléphone"));
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur recherche patient par téléphone", e);
        }
    }

    // Si ton interface PatientService contient encore ces deux méthodes,
    // on les laisse compatibles (sans repo.findByNom, et Optional pour téléphone).

    @Override
    public List<Patient> findByNom(String nom) {
        return searchByNom(nom);
    }

    @Override
    public Patient findByTelephone(String telephone) {
        return getByTelephone(telephone);
    }

    @Override
    public long countAll() {
        try {
            return patientRepo.countAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur countAll patient", e);
        }
    }

    // =======================
    // Validations
    // =======================
    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw ServiceException.validation(name + " obligatoire");
    }

    private void validateCreate(Patient p) {
        if (p == null) throw ServiceException.validation("Patient null");
        if (p.getId() != null) throw ServiceException.validation("Création: id doit être null");
        validateCommon(p);
    }

    private void validateUpdate(Patient p) {
        if (p == null) throw ServiceException.validation("Patient null");
        requireId(p.getId(), "id");
        validateCommon(p);
    }

    private void validateCommon(Patient p) {
        if (p.getNom() == null || p.getNom().isBlank()) throw ServiceException.validation("Nom obligatoire");
        if (p.getPrenom() == null || p.getPrenom().isBlank()) throw ServiceException.validation("Prénom obligatoire");
        if (p.getTelephone() == null || p.getTelephone().isBlank()) throw ServiceException.validation("Téléphone obligatoire");
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase(Locale.ROOT);
    }
}
