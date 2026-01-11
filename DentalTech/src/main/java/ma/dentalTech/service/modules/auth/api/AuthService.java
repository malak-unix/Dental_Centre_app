package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;

/**
 * Interface pour la gestion de l'authentification.
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     * @param request DTO contenant le login et le mot de passe.
     * @return AuthResult contenant le succès ou l'échec et les infos utilisateur.
     */
    AuthResultDTO authenticate(AuthRequestDTO request);

}