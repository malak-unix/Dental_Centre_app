package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.mvc.dto.users.RoleDTO;
import ma.dentalTech.entities.enums.LibelleRole;
import java.util.List;

public interface RoleManagementService {

    RoleDTO createRole(RoleDTO roleAndPrivileges);

    RoleDTO updateRole(RoleDTO roleDto);

    void deleteRole(Long roleId);

    RoleDTO getRoleById(Long id);

    RoleDTO getRoleByType(LibelleRole type);

    List<RoleDTO> getAllRoles();

    RoleDTO updateRolePrivileges(Long roleId, List<String> privileges);
}