package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDetailEnrichedDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListEnrichedItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListRequestDTO;

import java.util.List;

/**
 * Controller MVC (Swing) pour les dossiers médicaux.
 */
public interface DossierMedicalController {

    /**
     * Retourne la liste enrichie des dossiers médicaux avec infos patient.
     */
    List<DossierListEnrichedItemDTO> searchForList(DossierListRequestDTO request);

    /**
     * Récupère les détails complets d'un dossier médical avec toutes les données associées.
     */
    DossierDetailEnrichedDTO getDetail(Long dossierId);

    /**
     * Crée un nouveau dossier médical.
     */
    Long create(DossierDTO dossier, String username);

    /**
     * Met à jour un dossier médical existant.
     */
    void update(DossierDTO dossier, String username);

    /**
     * Supprime un dossier médical.
     */
    void delete(Long dossierId, String username);
}
