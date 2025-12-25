package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.service.common.ServiceException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
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
    public Patient getById(Long id) throws ServiceException {
        requireId(id, "patientId");
        try {
            Patient p = patientRepo.findById(id);
            if (p == null) throw new ServiceException("Patient introuvable id=" + id);
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
            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Patient exist = patientRepo.findByTelephone(p.getTelephone());
                if (exist != null) throw new ServiceException("Téléphone déjà utilisé");
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
            if (old == null) throw new ServiceException("Patient introuvable id=" + p.getId());

            if (p.getTelephone() != null && !p.getTelephone().isBlank()) {
                Patient existTel = patientRepo.findByTelephone(p.getTelephone());
                if (existTel != null && !existTel.getId().equals(p.getId())) {
                    throw new ServiceException("Téléphone déjà utilisé");
                }
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
        if (nom == null || nom.isBlank()) throw new ServiceException("nom obligatoire");
        try {
            // ✅ ton repo a findByNom
            return patientRepo.findByNom(nom);
        } catch (DaoException e) {
            throw new ServiceException("Erreur searchByNom patient", e);
        }
    }

    @Override
    public Patient getByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) throw new ServiceException("telephone obligatoire");
        try {
            Patient p = patientRepo.findByTelephone(telephone);
            if (p == null) throw new ServiceException("Aucun patient trouvé pour ce téléphone");
            return p;
        } catch (ServiceException se) {
            throw se;
        } catch (DaoException e) {
            throw new ServiceException("Erreur getByTelephone patient", e);
        }
    }

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
        } catch (DaoException e) {
            throw new ServiceException("Erreur countAll patient", e);
        }
    }

    // =======================
    // Validation
    // =======================
    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw new ServiceException(name + " obligatoire");
    }

    private void validateCreate(Patient p) {
        if (p == null) throw new ServiceException("Patient null");
        if (p.getId() != null) throw new ServiceException("Création: id doit être null");
        validateCommon(p);
    }

    private void validateUpdate(Patient p) {
        if (p == null) throw new ServiceException("Patient null");
        requireId(p.getId(), "id");
        validateCommon(p);
    }

    private void validateCommon(Patient p) {
        if (p.getNom() == null || p.getNom().isBlank()) throw new ServiceException("Nom obligatoire");
        if (p.getPrenom() == null || p.getPrenom().isBlank()) throw new ServiceException("Prénom obligatoire");
        if (p.getTelephone() == null || p.getTelephone().isBlank()) throw new ServiceException("Téléphone obligatoire");
    }
}
