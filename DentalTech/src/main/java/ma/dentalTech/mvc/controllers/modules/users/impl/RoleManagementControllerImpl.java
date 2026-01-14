package ma.dentalTech.mvc.controllers.modules.users.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.users.RoleDTO;
import ma.dentalTech.mvc.controllers.modules.users.api.RoleManagementController;
import ma.dentalTech.service.modules.users.api.RoleManagementService;
import java.util.List;

@RequiredArgsConstructor
public class RoleManagementControllerImpl implements RoleManagementController {

    private final RoleManagementService roleManagementService;

    @Override
    public RoleDTO createRole(RoleDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("dto cannot be null");
        return roleManagementService.createRole(dto);
    }

    @Override
    public RoleDTO updateRole(RoleDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("dto cannot be null");
        return roleManagementService.updateRole(dto);
    }

    @Override
    public void deleteRole(Long roleId) {
        if (roleId == null)
            throw new IllegalArgumentException("ID cannot be null");
        roleManagementService.deleteRole(roleId);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        return roleManagementService.getRoleById(id);
    }

    @Override
    public RoleDTO getRoleByType(LibelleRole type) {
        return roleManagementService.getRoleByType(type);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleManagementService.getAllRoles();
    }

    @Override
    public RoleDTO updatePrivileges(Long roleId, List<String> privileges) {
        if (roleId == null)
            throw new IllegalArgumentException("ID cannot be null");
        return roleManagementService.updateRolePrivileges(roleId, privileges);
    }
}
