package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.staff.Staff;
import java.util.List;

public interface StaffService {
    // Récupère tout le monde (Médecins + Secrétaires)
    List<Staff> getAllStaff();

    Staff getStaffParId(Long id);

    // Une méthode purement RH
    void mettreAJourSalaire(Long id, Double nouveauSalaire);

    void supprimerStaff(Long id);
}