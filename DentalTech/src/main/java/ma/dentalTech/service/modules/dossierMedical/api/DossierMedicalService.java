package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.util.List;
import java.util.Optional;

public interface DossierMedicalService {

    // CRUD
    List<DossierMedical> getAll();
    DossierMedical getById(Long id);
    void create(DossierMedical d);
    void update(DossierMedical d);
    void delete(DossierMedical d);
    void deleteById(Long id);

    // Extras repo
    Optional<DossierMedical> getByPatientId(Long patientId);
    List<DossierMedical> getByMedecinId(Long medecinId);
    List<DossierMedical> searchByNotes(String keyword);

    boolean existsById(Long id);
    long count();
    List<DossierMedical> findPage(int limit, int offset);

    Integer countActifs();
}
