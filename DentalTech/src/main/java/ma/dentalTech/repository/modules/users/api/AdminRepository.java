package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.admin.Admin;
import ma.dentalTech.repository.common.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    // Vous pouvez ajouter des méthodes spécifiques ici si besoin
}