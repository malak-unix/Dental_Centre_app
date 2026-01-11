package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.common.exceptions.AuthorizationException;

import java.util.Arrays;

/**
 * Implémentation du service d'autorisation adaptée du code professeur.
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipalDTO principal, LibelleRole role) {
        if (principal == null || role == null) return false;
        // Utilisation de la syntaxe Record : .roles()
        return principal.roles() != null && principal.roles().contains(role);
    }

    @Override
    public boolean hasAnyRole(UserPrincipalDTO principal, LibelleRole... roles) {
        if (principal == null || roles == null || roles.length == 0) return false;
        if (principal.roles() == null) return false;

        return Arrays.stream(roles).anyMatch(principal.roles()::contains);
    }

    @Override
    public boolean hasPrivilege(UserPrincipalDTO principal, String privilege) {
        if (principal == null || privilege == null || privilege.isBlank()) return false;
        // Utilisation de la syntaxe Record : .privileges()
        return principal.privileges() != null && principal.privileges().contains(privilege);
    }

    @Override
    public void checkRole(UserPrincipalDTO principal, LibelleRole role) {
        if (!hasRole(principal, role)) {
            throw new AuthorizationException("Accès refusé : rôle requis = " + role);
        }
    }

    @Override
    public void checkAnyRole(UserPrincipalDTO principal, LibelleRole... roles) {
        if (!hasAnyRole(principal, roles)) {
            throw new AuthorizationException("Accès refusé : un des rôles requis = " + Arrays.toString(roles));
        }
    }

    @Override
    public void checkPrivilege(UserPrincipalDTO principal, String privilege) {
        if (!hasPrivilege(principal, privilege)) {
            throw new AuthorizationException("Accès refusé : privilège requis = " + privilege);
        }
    }
}