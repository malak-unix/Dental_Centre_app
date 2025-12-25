package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedents;

import java.util.List;

public interface AntecedentRepository {

    void create(Antecedents a);
    void update(Antecedents a);
    void delete(Antecedents a);
    void deleteById(Long id);

    Antecedents findById(Long id);
    List<Antecedents> findAll();

    List<Antecedents> findByPatientId(Long patientId);
}
