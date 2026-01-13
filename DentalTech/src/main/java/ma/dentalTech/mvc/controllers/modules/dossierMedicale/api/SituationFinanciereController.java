package ma.dentalTech.mvc.controllers.modules.dossierMedicale.api;

import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListRequestDTO;

import java.util.List;

/**
 * Controller MVC (Swing) pour les situations financières.
 */
public interface SituationFinanciereController {

    /**
     * Retourne la liste des situations financières pour un médecin.
     */
    List<SituationFinanciereListItemDTO> searchForList(SituationFinanciereListRequestDTO request);

    /**
     * Récupère les détails d'une situation financière avec ses factures et consultations.
     */
    SituationFinanciereDetailDTO getDetail(Long situationFinanciereId);

    /**
     * Réinitialise une situation financière.
     */
    void reset(Long situationFinanciereId, String username);
}
