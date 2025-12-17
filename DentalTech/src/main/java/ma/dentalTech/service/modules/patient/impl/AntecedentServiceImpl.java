package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
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
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Antecedents getById(Long id) {
        try {
            return repo.findById(id);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Antecedents a) {
        if (a == null) throw new IllegalArgumentException("Antecedent null");
        if (a.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        try {
            repo.create(a);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Antecedents a) {
        if (a == null || a.getId() == null) throw new IllegalArgumentException("Antecedent id obligatoire");
        if (a.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire");
        try {
            repo.update(a);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Antecedents a) {
        try {
            repo.delete(a);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            repo.deleteById(id);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Antecedents> getByPatientId(Long patientId) {
        try {
            return repo.findByPatientId(patientId);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }
}
