package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.admin.Admin;
import ma.dentalTech.repository.common.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    // Méthodes spécifiques si besoin
}