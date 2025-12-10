package ma.dentalTech.repository.modules.certificat.api;

import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CertificatRepository extends CrudRepository<Certificat, Long> {

    // Certificats commençant à une date précise
    List<Certificat> findByDateDebut(LocalDate dateDebut);

    // Certificats sur une période
    List<Certificat> findByDateBetween(LocalDate start, LocalDate end);

    // Nombre total de certificats
    long count();

    // Pagination simple
    List<Certificat> findPage(int limit, int offset);
}
