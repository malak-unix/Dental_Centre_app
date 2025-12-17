package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.role.Role;
import java.util.List;

public interface RoleService {
    // Pour afficher la liste des rôles disponibles (Dropdown menu)
    List<Role> getAllRoles();

    // Pour récupérer un rôle précis quand on crée un utilisateur
    // (Ex: on cherche le rôle "MEDECIN" pour l'attribuer)
    Role getRoleParNom(String nomRole);
}