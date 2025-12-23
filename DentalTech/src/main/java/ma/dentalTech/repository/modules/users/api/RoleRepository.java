package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.role.Role;           // Import correct selon votre code entity
import ma.dentalTech.entities.enums.LibelleRole;   // Import de votre Enum
import ma.dentalTech.repository.common.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    // Recherche un rôle par son Enum (ADMIN, MEDECIN, etc.)
    Optional<Role> findByLibelle(LibelleRole libelle);

}