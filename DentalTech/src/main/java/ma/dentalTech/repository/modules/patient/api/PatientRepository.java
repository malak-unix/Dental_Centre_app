package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;

public interface PatientRepository {
    void create(Patient p) throws DaoException;
    void update(Patient p) throws DaoException;
    void delete(Patient p) throws DaoException;
    void deleteById(Long id) throws DaoException;

    Patient findById(Long id) throws DaoException;
    List<Patient> findAll() throws DaoException;
    List<Patient> findByNom(String nom) throws DaoException;
    Patient findByTelephone(String telephone) throws DaoException;

    long countAll() throws DaoException;

}
