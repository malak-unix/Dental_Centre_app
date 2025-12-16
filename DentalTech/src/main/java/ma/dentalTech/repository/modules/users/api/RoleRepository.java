package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.role.Role;
import ma.dentalTech.repository.common.CrudRepository;
// import ma.dentalTech.entities.enums.RoleType; // Décommente si tu utilises l'enum

public interface RoleRepository extends CrudRepository<Role, Long> {
    // Role findByType(RoleType type); // Décommente si besoin
}