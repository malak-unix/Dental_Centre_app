package ma.dentalTech.service.modules.auth.api;

import java.util.Map;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;

/**
 * Interface pour la validation des données du formulaire de connexion.
 */
public interface LoginFormValidator {

    /**
     * Valide les champs du formulaire.
     * @param request Le DTO contenant les identifiants saisis.
     * @return Une Map contenant les erreurs (Clé: nom du champ, Valeur: message d'erreur).
     * Retourne une Map vide si aucune erreur n'est trouvée.
     */
    Map<String, String> validate(AuthRequestDTO request);

}