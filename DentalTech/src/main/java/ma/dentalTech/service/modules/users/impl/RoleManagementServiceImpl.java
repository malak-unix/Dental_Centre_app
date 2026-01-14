package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.mvc.dto.users.RoleDTO;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.RoleManagementService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleManagementServiceImpl implements RoleManagementService {
    private final RoleRepository roleRepository;

    public RoleManagementServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO createRole(RoleDTO dto) {
        if (dto == null)
            return null;
        Role entity = mapToEntity(dto);
        roleRepository.create(entity);
        // ID should be populated by repo implementation if it supports generated keys
        return mapToDTO(entity);
    }

    @Override
    public RoleDTO updateRole(RoleDTO dto) {
        if (dto == null || dto.getId() == null)
            return null;
        Role entity = roleRepository.findById(dto.getId());
        if (entity != null) {
            entity.setLibelle(dto.getLibelle());
            entity.setPrivileges(dto.getPrivileges());
            roleRepository.update(entity);
            return mapToDTO(entity);
        }
        return null;
    }

    @Override
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role r = roleRepository.findById(id);
        return mapToDTO(r);
    }

    @Override
    public RoleDTO getRoleByType(LibelleRole type) {
        return roleRepository.findByType(type)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO updateRolePrivileges(Long roleId, List<String> privileges) {
        Role role = roleRepository.findById(roleId);
        if (role == null)
            return null;

        String privString = (privileges == null || privileges.isEmpty()) ? "" : String.join(",", privileges);
        role.setPrivileges(privString);
        roleRepository.update(role);
        return mapToDTO(role);
    }

    // --- Mappers ---

    private RoleDTO mapToDTO(Role r) {
        if (r == null)
            return null;
        return RoleDTO.builder()
                .id(r.getId())
                .libelle(r.getLibelle())
                .privileges(r.getPrivileges())
                .build();
    }

    private Role mapToEntity(RoleDTO d) {
        if (d == null)
            return null;
        Role r = new Role();
        r.setId(d.getId());
        r.setLibelle(d.getLibelle());
        r.setPrivileges(d.getPrivileges());
        return r;
    }
}
