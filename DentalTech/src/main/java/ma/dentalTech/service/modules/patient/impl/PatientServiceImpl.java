package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository repo;

    public PatientServiceImpl(PatientRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Patient> getAll() {
        try {
            return repo.findAll();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Patient getById(Long id) {
        try {
            return repo.findById(id);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Patient p) {
        if (p == null) throw new IllegalArgumentException("Patient null");
        try {
            repo.create(p);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Patient p) {
        if (p == null || p.getId() == null) throw new IllegalArgumentException("Patient id obligatoire");
        try {
            repo.update(p);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Patient p) {
        try {
            repo.delete(p);
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
    public List<Patient> searchByNom(String nom) {
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Nom vide");

        try {
            return repo.findByNom(nom);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Patient getByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank())
            throw new IllegalArgumentException("Téléphone vide");

        try {
            return repo.findByTelephone(telephone);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long countAll() {
        try {
            return repo.countAll();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }
}
