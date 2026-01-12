package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.common.exceptions.AuthorizationException;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * Implémentation du service d'autorisation.
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipalDTO principal, LibelleRole role) {
        if (principal == null || role == null) return false;

        // 1) check direct sur rolePrincipal
        if (principal.rolePrincipal() == role) return true;

        // 2) check sur set roles
        return principal.roles() != null && principal.roles().contains(role);
    }

    @Override
    public boolean hasAnyRole(UserPrincipalDTO principal, LibelleRole... roles) {
        if (principal == null || roles == null || roles.length == 0) return false;

        // Si rolePrincipal suffit
        LibelleRole rp = principal.rolePrincipal();
        if (rp != null && Arrays.stream(roles).anyMatch(r -> r == rp)) return true;

        // Sinon check dans le set
        if (principal.roles() == null) return false;
        return Arrays.stream(roles)
                .filter(Objects::nonNull)
                .anyMatch(principal.roles()::contains);
    }

    @Override
    public boolean hasPrivilege(UserPrincipalDTO principal, String privilege) {
        if (principal == null || privilege == null) return false;
        if (principal.privileges() == null || principal.privileges().isEmpty()) return false;

        String wanted = normalize(privilege);
        if (wanted.isBlank()) return false;

        // Compare normalize(privilege) contre normalize de chaque élément existant
        return principal.privileges().stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .anyMatch(wanted::equals);
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

    private String normalize(String s) {
        return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
    }
}
