package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.RoleManagementService;
import java.util.List;

public class RoleManagementServiceImpl implements RoleManagementService {
    private final RoleRepository roleRepository;

    public RoleManagementServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {
        // Si votre repository renvoie void, on appelle la méthode puis on retourne l'objet
        roleRepository.create(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        // Même logique ici pour corriger l'erreur "Incompatible types"
        roleRepository.update(role);
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role getRoleByType(LibelleRole type) {
        // Utilisez findByType de votre repository
        return roleRepository.findByType(type).orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRolePrivileges(Long roleId, List<String> privileges) {
        // À implémenter si nécessaire, sinon retourner null pour l'instant
        return null;
    }
}