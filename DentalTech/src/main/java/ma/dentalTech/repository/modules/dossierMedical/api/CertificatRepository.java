package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CertificatRepository extends CrudRepository<Certificat, Long> {

    // -------- Recherche principale --------

    /**
     * Tous les certificats d'un dossier médical.
     */
    List<Certificat> findByDossierId(Long dossierId);

    /**
     * Certificats commençant à une date précise.
     */
    List<Certificat> findByDateDebut(LocalDate dateDebut);

    /**
     * Certificats sur une période [start, end].
     */
    List<Certificat> findByDateBetween(LocalDate start, LocalDate end);

    // -------- Utilitaires (comme ActeRepository) --------

    /**
     * Vérifie si un certificat existe.
     */
    boolean existsById(Long id);

    /**
     * Nombre total de certificats.
     */
    long count();

    /**
     * Pagination simple.
     */
    List<Certificat> findPage(int limit, int offset);

    // -------- Touch perso utile --------

    /**
     * Recherche par mot-clé dans note_medecin (LIKE %keyword%).
     * Très pratique côté UI.
     */
    List<Certificat> searchByNote(String keyword);
}
