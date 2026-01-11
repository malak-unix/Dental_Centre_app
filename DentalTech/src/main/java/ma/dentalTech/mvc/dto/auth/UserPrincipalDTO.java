package ma.dentalTech.mvc.dto.auth;

import java.util.Set;
import ma.dentalTech.entities.enums.LibelleRole;

public record UserPrincipalDTO(
        Long id,
        String nom,
        String email,
        String login,
        LibelleRole rolePrincipal,
        Set<LibelleRole> roles,
        Set<String> privileges
) {}