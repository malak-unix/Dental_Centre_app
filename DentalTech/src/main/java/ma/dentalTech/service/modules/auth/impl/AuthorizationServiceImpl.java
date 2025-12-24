package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;

public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipalDTO principal, LibelleRole role) {
        // Utilise le rôle stocké dans ton DTO
        return principal != null && principal.getRole() == role;
    }

    @Override
    public boolean hasAnyRole(UserPrincipalDTO principal, LibelleRole... roles) {
        if (principal == null) return false;
        for (LibelleRole role : roles) {
            if (principal.getRole() == role) return true;
        }
        return false;
    }

    @Override
    public void checkRole(UserPrincipalDTO principal, LibelleRole role) {
        if (!hasRole(principal, role)) {
            throw new SecurityException("Accès refusé : rôle " + role + " requis.");
        }
    }
}