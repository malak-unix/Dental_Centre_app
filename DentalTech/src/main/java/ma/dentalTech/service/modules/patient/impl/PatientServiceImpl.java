package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAll() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public void create(Patient p) {
        if (p == null) throw new IllegalArgumentException("Patient null");
        if (p.getNom() == null || p.getNom().isBlank()) throw new IllegalArgumentException("Nom obligatoire");
        if (p.getPrenom() == null || p.getPrenom().isBlank()) throw new IllegalArgumentException("Prenom obligatoire");
        patientRepository.create(p);
    }

    @Override
    public void update(Patient p) {
        if (p == null || p.getId() == null) throw new IllegalArgumentException("Patient id obligatoire");
        patientRepository.update(p);
    }

    @Override
    public void delete(Patient p) {
        patientRepository.delete(p);
    }

    @Override
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public List<Patient> searchByNom(String nomPart) {
        return patientRepository.findByNomLike(nomPart);
    }

    @Override
    public Patient getByTelephone(String telephone) {
        return patientRepository.findByTelephone(telephone);
    }
}
