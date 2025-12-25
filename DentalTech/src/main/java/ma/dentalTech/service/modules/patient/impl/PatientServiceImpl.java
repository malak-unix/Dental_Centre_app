package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.common.ServiceException;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;

    public PatientServiceImpl(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Override
    public List<Patient> getAll() {
        try {
            return patientRepo.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Erreur getAll patients", e);
        }
    }

    @Override
    public Patient getById(Long id) {
        requireId(id, "patientId");
        try {
            Patient p = patientRepo.findById(id);
            if (p == null) throw ServiceException.notFound("Patient introuvable id=" + id);
            return p;
        } catch (ServiceException se) {
            throw se;
        } catch (DaoException e) {
            throw new ServiceException("Erreur getById patient", e);
        }
    }

    @Override
    public void create(Patient p) {
        validateCreate(p);
        try {
            // si telephone obligatoire chez vous => déjà validé
            // si vous voulez l'unicité:
            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Patient exist = patientRepo.findByTelephone(p.getTelephone());
                if (exist != null) throw ServiceException.validation("Téléphone déjà utilisé");
            }
            patientRepo.create(p);
        } catch (ServiceException se) {
            throw se;
        } catch (DaoException e) {
            throw new ServiceException("Erreur create patient", e);
        }
    }

    @Override
    public void update(Patient p) {
        validateUpdate(p);
        try {
            Patient old = patientRepo.findById(p.getId());
            if (old == null) throw ServiceException.notFound("Patient introuvable id=" + p.getId());

            // unicité téléphone si changé
            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Patient existTel = patientRepo.findByTelephone(p.getTelephone());
                if (existTel != null && !existTel.getId().equals(p.getId())) {
                    throw ServiceException.validation("Téléphone déjà utilisé");
                }
            }

            // si baseEntityId manquant, garder l'ancien
            if (p.getBaseEntityId() == null) {
                p.setBaseEntityId(old.getBaseEntityId());
            }

            patientRepo.update(p);

        } catch (ServiceException se) {
            throw se;
        } catch (DaoException e) {
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
        } catch (DaoException e) {
            throw new ServiceException("Erreur deleteById patient", e);
        }
    }

    @Override
    public List<Patient> searchByNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw ServiceException.validation("nom obligatoire");
        }
        try {
            return patientRepo.findByNom(nom);
        } catch (DaoException e) {
            throw new ServiceException("Erreur recherche patient par nom", e);
        }
    }

    @Override
    public Patient getByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw ServiceException.validation("telephone obligatoire");
        }
        try {
            Patient p = patientRepo.findByTelephone(telephone);
            if (p == null) {
                throw ServiceException.notFound("Aucun patient trouvé pour ce téléphone");
            }
            return p;
        } catch (ServiceException se) {
            throw se;
        } catch (DaoException e) {
            throw new ServiceException("Erreur recherche patient par téléphone", e);
        }
    }


    @Override
    public List<Patient> findByNom(String nom) {
        if (nom == null || nom.isBlank()) throw ServiceException.validation("nom obligatoire");
        try {
            return patientRepo.findByNom(nom);
        } catch (DaoException e) {
            throw new ServiceException("Erreur findByNom patient", e);
        }
    }

    @Override
    public Patient findByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) throw ServiceException.validation("telephone obligatoire");
        try {
            return patientRepo.findByTelephone(telephone);
        } catch (DaoException e) {
            throw new ServiceException("Erreur findByTelephone patient", e);
        }
    }

    @Override
    public long countAll() {
        try {
            return patientRepo.countAll();
        } catch (DaoException e) {
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
}
