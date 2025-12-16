package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.repository.common.CrudRepository;
import java.util.List;

public interface StaffRepository extends CrudRepository<Staff, Long> {

    // Trouver un membre du staff par son CIN
    Staff findByCin(String cin);

    // Trouver le staff recruté après une certaine date
    List<Staff> findByDateRecrutementAfter(java.time.LocalDate date);
}