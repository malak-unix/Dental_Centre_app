package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListRequestDTO;

import java.util.List;

/**
 * Controller MVC (Swing) : expose des méthodes simples pour l'UI.
 */
public interface OrdonnanceController {

    /**
     * Retourne la liste à afficher dans l'écran "Ordonnances" (filtre par médecin, patient, dates...)
     */
    List<OrdonnanceListItemDTO> searchForList(OrdonnanceListRequestDTO in);

    /**
     * Création d'une ordonnance.
     */
    Long create(OrdonnanceDTO ordonnance, String username);

    /**
     * Mise à jour d'une ordonnance.
     */
    void update(OrdonnanceDTO ordonnance, String username);

    /**
     * Suppression d'une ordonnance.
     */
    void delete(Long ordonnanceId);

    /**
     * Récupération d'une ordonnance par ID.
     */
    OrdonnanceDTO getById(Long ordonnanceId);

    /**
     * Récupération des détails complets d'une ordonnance (avec prescriptions).
     */
    OrdonnanceDetailDTO getDetail(Long ordonnanceId);
}
