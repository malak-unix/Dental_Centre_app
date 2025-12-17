package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;

public interface CertificatService {

    // CRUD
    List<Certificat> getAll();
    Certificat getById(Long id);
    void create(Certificat cert);
    void update(Certificat cert);
    void delete(Certificat cert);
    void deleteById(Long id);

    // Extras repo
    List<Certificat> getByDossierId(Long dossierId);
    List<Certificat> getByDateDebut(LocalDate dateDebut);
    List<Certificat> getByDateBetween(LocalDate start, LocalDate end);
    List<Certificat> searchByNote(String keyword);

    boolean existsById(Long id);
    long count();
    List<Certificat> findPage(int limit, int offset);
}
