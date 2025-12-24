package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import java.util.List;

public interface RoleManagementService {

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long roleId);

    Role getRoleById(Long id);

    Role getRoleByType(LibelleRole type);

    List<Role> getAllRoles();

    // Permet d'ajouter ou supprimer des permissions à un rôle
    Role updateRolePrivileges(Long roleId, List<String> privileges);
}