package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;

import java.time.LocalDateTime;
import java.util.List;

public interface ActeService {

    // CRUD
    List<Acte> getAll();
    Acte getById(Long id);
    void create(Acte a);
    void update(Acte a);
    void delete(Acte a);
    void deleteById(Long id);

    // Extras repo
    List<Acte> getByCategorie(String categorie);
    List<Acte> searchByLibelle(String keyword);

    boolean existsById(Long id);
    long count();
    List<Acte> findPage(int limit, int offset);

    Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
    Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end);
}
