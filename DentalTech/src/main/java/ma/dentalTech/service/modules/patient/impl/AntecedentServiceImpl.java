package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository repo;

    public AntecedentServiceImpl(AntecedentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Antecedents> getAll() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur récupération liste antecedents", e);
        }
    }

    @Override
    public Antecedents getById(Long id) {
        if (id == null || id <= 0) {
            throw ServiceException.validation("id antecedent obligatoire");
        }
        try {
            Optional<Antecedents> opt = repo.findById(id);
            return opt.orElseThrow(() -> ServiceException.notFound("Antecedent introuvable (id=" + id + ")"));
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur récupération antecedent (id=" + id + ")", e);
        }
    }

    @Override
    public void create(Antecedents a) {
        validateCreate(a);
        try {
            repo.create(a);
        } catch (Exception e) {
            throw new ServiceException("Erreur création antecedent", e);
        }
    }

    @Override
    public void update(Antecedents a) {
        validateUpdate(a);
        try {
            Optional<Antecedents> existing = repo.findById(a.getId());
            if (existing.isEmpty()) throw ServiceException.notFound("Antecedent introuvable (id=" + a.getId() + ")");

            repo.update(a);
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur modification antecedent (id=" + a.getId() + ")", e);
        }
    }

    @Override
    public void delete(Antecedents a) {
        if (a == null || a.getId() == null) return;
        deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw ServiceException.validation("id antecedent obligatoire");
        }
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression antecedent (id=" + id + ")", e);
        }
    }

    @Override
    public List<Antecedents> getByPatientId(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw ServiceException.validation("patientId obligatoire");
        }
        try {
            return repo.findByPatientId(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur récupération antecedents par patientId=" + patientId, e);
        }
    }

    // =========================
    // Validations
    // =========================
    private void validateCreate(Antecedents a) {
        if (a == null) throw ServiceException.validation("Antecedent null");
        if (a.getId() != null) throw ServiceException.validation("Création: id doit être null");
        validateCommon(a);
    }

    private void validateUpdate(Antecedents a) {
        if (a == null) throw ServiceException.validation("Antecedent null");
        if (a.getId() == null || a.getId() <= 0) throw ServiceException.validation("id antecedent obligatoire");
        validateCommon(a);
    }

    private void validateCommon(Antecedents a) {
        // ✅ plus de a.getPatientId() (car absent)
        if (a.getNom() == null || a.getNom().isBlank()) {
            throw ServiceException.validation("nom antecedent obligatoire");
        }
        // categorie / niveauDeRisque / description : optionnels selon vos règles
    }
}
