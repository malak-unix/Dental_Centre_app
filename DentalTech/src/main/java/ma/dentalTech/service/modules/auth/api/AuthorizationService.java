package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;

public interface AuthorizationService {

    boolean hasRole(UserPrincipalDTO principal, LibelleRole role);

    boolean hasAnyRole(UserPrincipalDTO principal, LibelleRole... roles);

    /**
     * Vérifie si l'utilisateur a le rôle, sinon lance une exception.
     */
    void checkRole(UserPrincipalDTO principal, LibelleRole role);
}