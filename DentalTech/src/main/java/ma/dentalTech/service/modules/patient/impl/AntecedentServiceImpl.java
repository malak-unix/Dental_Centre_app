package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;

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
            throw new ServiceException("Erreur getAll antecedents", e);
        }
    }

    @Override
    public Antecedents getById(Long id) {
        requireId(id, "antecedentId");
        try {
            Antecedents a = repo.findById(id);
            if (a == null) throw ServiceException.notFound("Antecedent introuvable id=" + id);
            return a;
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur getById antecedent", e);
        }
    }

    @Override
    public void create(Antecedents a) {
        validateCreate(a);
        try {
            repo.create(a);
        } catch (Exception e) {
            throw new ServiceException("Erreur create antecedent", e);
        }
    }

    @Override
    public void update(Antecedents a) {
        validateUpdate(a);
        try {
            Antecedents old = repo.findById(a.getId());
            if (old == null) throw ServiceException.notFound("Antecedent introuvable id=" + a.getId());
            repo.update(a);
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            throw new ServiceException("Erreur update antecedent", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        requireId(id, "antecedentId");
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur deleteById antecedent", e);
        }
    }

    @Override
    public List<Antecedents> getByPatientId(Long patientId) {
        requireId(patientId, "patientId");
        try {
            return repo.findByPatientId(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur getByPatientId antecedent", e);
        }
    }

    private void requireId(Long id, String name) {
        if (id == null || id <= 0) throw ServiceException.validation(name + " obligatoire");
    }

    private void validateCreate(Antecedents a) {
        if (a == null) throw ServiceException.validation("Antecedent null");
        if (a.getId() != null) throw ServiceException.validation("Création: id doit être null");
        validateCommon(a);
    }

    private void validateUpdate(Antecedents a) {
        if (a == null) throw ServiceException.validation("Antecedent null");
        requireId(a.getId(), "id");
        validateCommon(a);
    }

    private void validateCommon(Antecedents a) {
        requireId(a.getPatientId(), "patientId");
        if (a.getNom() == null || a.getNom().isBlank()) throw ServiceException.validation("nom obligatoire");
    }
}
