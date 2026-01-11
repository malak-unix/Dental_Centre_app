package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.entities.enums.LibelleRole; // Assurez-vous que c'est votre Enum pour ADMIN, MEDECIN, etc.
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;

/**
 * Interface pour la gestion des permissions et des accès.
 */
public interface AuthorizationService {

    /**
     * Vérifie si l'utilisateur possède un rôle spécifique.
     */
    boolean hasRole(UserPrincipalDTO principal, LibelleRole role);

    /**
     * Vérifie si l'utilisateur possède au moins l'un des rôles cités.
     */
    boolean hasAnyRole(UserPrincipalDTO principal, LibelleRole... roles);

    /**
     * Vérifie si l'utilisateur possède un privilège particulier (ex: "MODIFIER_PATIENT").
     */
    boolean hasPrivilege(UserPrincipalDTO principal, String privilege);

    /**
     * Lève une exception si le rôle est absent.
     */
    void checkRole(UserPrincipalDTO principal, LibelleRole role);

    /**
     * Lève une exception si aucun des rôles n'est présent.
     */
    void checkAnyRole(UserPrincipalDTO principal, LibelleRole... roles);

    /**
     * Lève une exception si le privilège est absent.
     */
    void checkPrivilege(UserPrincipalDTO principal, String privilege);
}