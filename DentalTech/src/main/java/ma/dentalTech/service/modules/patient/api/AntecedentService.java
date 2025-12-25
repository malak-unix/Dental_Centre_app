package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedents;

import java.util.List;

public interface AntecedentService {
    List<Antecedents> getAll();

    Antecedents getById(Long id);

    void create(Antecedents a);

    void update(Antecedents a);

    void delete(Antecedents a);

    void deleteById(Long id);

    List<Antecedents> getByPatientId(Long patientId);
}
