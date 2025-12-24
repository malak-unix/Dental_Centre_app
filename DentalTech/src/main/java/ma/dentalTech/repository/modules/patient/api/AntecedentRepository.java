package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.patient.Antecedents;

import java.util.List;

public interface AntecedentRepository {

    void create(Antecedents a) throws DaoException;
    void update(Antecedents a) throws DaoException;
    void delete(Antecedents a) throws DaoException;
    void deleteById(Long id) throws DaoException;

    Antecedents findById(Long id) throws DaoException;
    List<Antecedents> findAll() throws DaoException;

    List<Antecedents> findByPatientId(Long patientId) throws DaoException;
}
