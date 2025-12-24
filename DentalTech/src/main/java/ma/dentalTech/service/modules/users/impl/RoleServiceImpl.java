package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.role.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl;
import ma.dentalTech.service.modules.users.api.RoleService; // Import Interface

import java.util.List;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepo = new RoleRepositoryImpl();

    @Override
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Role getRoleParNom(String nomRole) {
        // 1. On récupère tous les rôles
        List<Role> roles = roleRepo.findAll();

        // 2. On cherche celui qui correspond au nom demandé (ex: "ADMIN")
        if (roles != null) {
            for (Role r : roles) {
                // On compare les libellés (en ignorant majuscules/minuscules)
                if (r.getLibelle() != null && r.getLibelle().name().equalsIgnoreCase(nomRole)) {
                    return r;
                }
            }
        }

        // Si on ne trouve rien
        throw new RuntimeException("Erreur : Le rôle '" + nomRole + "' n'existe pas en base de données.");
    }
}