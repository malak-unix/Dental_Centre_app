package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeListItemDTO;

import java.util.List;

/**
 * Controller MVC (Swing) : expose des méthodes simples pour l'UI.
 */
public interface ActeController {

    /**
     * Retourne la liste de tous les actes.
     */
    List<ActeListItemDTO> findAll();

    /**
     * Recherche d'actes par catégorie ou mot-clé.
     */
    List<ActeListItemDTO> search(String categorie, String keyword);

    /**
     * Création d'un acte.
     */
    Long create(ActeDTO acte, String username);

    /**
     * Mise à jour d'un acte.
     */
    void update(ActeDTO acte, String username);

    /**
     * Suppression d'un acte.
     */
    void delete(Long acteId);

    /**
     * Récupération d'un acte par ID.
     */
    ActeDTO getById(Long acteId);
}
