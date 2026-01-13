package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;

import java.util.List;

/**
 * Controller MVC (Swing) : expose des méthodes simples pour l'UI.
 *
 * Remarque:
 * - La couche service travaille avec des DTO request/result (IdRequestDTO, ListResponseDTO, ...)
 * - Le controller adapte ces DTO "techniques" vers une API pratique pour Swing.
 */
public interface ConsultationController {

    /**
     * Retourne la liste à afficher dans l'écran "Consultations" (filtre statut, date, patient...)
     */
    List<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO in);

    /**
     * Suppression d'une consultation.
     */
    void delete(Long consultationId);
}
