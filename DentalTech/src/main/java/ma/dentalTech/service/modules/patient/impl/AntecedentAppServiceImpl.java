package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentAppService;

import java.util.List;

public class AntecedentAppServiceImpl implements AntecedentAppService {

    private final AntecedentRepository repo;

    public AntecedentAppServiceImpl(AntecedentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Antecedents> lister() throws ServiceException {
        try {
            return repo.findAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur listing antecedents", e);
        }
    }

    @Override
    public Antecedents consulter(Long id) throws ValidationException, ServiceException {
        requireId(id, "antecedentId");
        try {
            Antecedents a = repo.findById(id);
            if (a == null) throw new ValidationException("Antecedent introuvable");
            return a;
        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur consultation antecedent", e);
        }
    }

    @Override
    public Antecedents creer(Antecedents a) throws ValidationException, ServiceException {
        validate(a);
        if (a.getId() != null) throw new ValidationException("Création: id doit être null");

        try {
            repo.create(a);
            return a;
        } catch (Exception e) {
            throw new ServiceException("Erreur création antecedent", e);
        }
    }

    @Override
    public Antecedents modifier(Long id, Antecedents a) throws ValidationException, ServiceException {
        requireId(id, "antecedentId");
        validate(a);

        try {
            Antecedents old = repo.findById(id);
            if (old == null) throw new ValidationException("Antecedent introuvable");

            a.setId(id);
            repo.update(a);
            return a;

        } catch (ValidationException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ServiceException("Erreur modification antecedent", e);
        }
    }

    @Override
    public void supprimer(Long id) throws ValidationException, ServiceException {
        requireId(id, "antecedentId");
        try {
            repo.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Erreur suppression antecedent", e);
        }
    }

    @Override
    public List<Antecedents> listerParPatient(Long patientId) throws ValidationException, ServiceException {
        requireId(patientId, "patientId");
        try {
            return repo.findByPatientId(patientId);
        } catch (Exception e) {
            throw new ServiceException("Erreur listing antecedents par patient", e);
        }
    }

    private void validate(Antecedents a) throws ValidationException {
        if (a == null) throw new ValidationException("Antecedent null");
        requireId(a.getPatientId(), "patientId");
        if (a.getNom() == null || a.getNom().isBlank()) throw new ValidationException("nom obligatoire");
    }

    private void requireId(Long id, String name) throws ValidationException {
        if (id == null || id <= 0) throw new ValidationException(name + " obligatoire");
    }
}
