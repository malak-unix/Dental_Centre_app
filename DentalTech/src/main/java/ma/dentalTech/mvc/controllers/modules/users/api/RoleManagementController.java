package ma.dentalTech.mvc.controllers.modules.users.api;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.users.RoleDTO;
import java.util.List;

public interface RoleManagementController {

    RoleDTO createRole(RoleDTO dto);

    RoleDTO updateRole(RoleDTO dto);

    void deleteRole(Long roleId);

    RoleDTO getRoleById(Long id);

    RoleDTO getRoleByType(LibelleRole type);

    List<RoleDTO> getAllRoles();

    RoleDTO updatePrivileges(Long roleId, List<String> privileges);
}
