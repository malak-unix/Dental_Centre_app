package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListRequestDTO;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {

    /**
     * Toutes les ordonnances d'un dossier médical.
     */
    List<Ordonnance> findByDossierId(Long dossierId);

    /**
     * Ordonnances générées pour une consultation donnée.
     */
    List<Ordonnance> findByConsultationId(Long consultationId);

    /**
     * Toutes les ordonnances d'une date donnée.
     */
    List<Ordonnance> findByDate(LocalDate date);

    /**
     * Ordonnances entre deux dates (incluses).
     */
    List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end);

    /**
     * Nombre total d'ordonnances (pour stats / pagination).
     */
    long count();

    /**
     * Pagination simple.
     */
    List<Ordonnance> findPage(int limit, int offset);
    
    // Méthode pour la liste avec nom du patient (JOIN)
    List<OrdonnanceListItemDTO> searchForList(OrdonnanceListRequestDTO req);
    long countForList(OrdonnanceListRequestDTO req);
}
