package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface StaffRepository extends CrudRepository<Staff, Long> {
    // Méthode utile pour la gestion RH (ex: trouver tous ceux qui ont un salaire < X)
    List<Staff> findBySalaireInferieurA(Double montant);
}