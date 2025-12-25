package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CertificatRepository extends CrudRepository<Certificat, Long> {

    List<Certificat> findByDossierId(Long dossierId);
    List<Certificat> findByDateDebut(LocalDate dateDebut);

    List<Certificat> findByDateBetween(LocalDate start, LocalDate end);


    boolean existsById(Long id);

    long count();

    List<Certificat> findPage(int limit, int offset);

    List<Certificat> searchByNote(String keyword);
}
