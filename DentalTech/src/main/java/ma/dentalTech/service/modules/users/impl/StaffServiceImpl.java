package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.repository.modules.users.api.StaffRepository;
import ma.dentalTech.repository.modules.users.impl.StaffRepositoryImpl;
import ma.dentalTech.service.modules.users.api.StaffService; // Import de l'interface

import java.util.List;

public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepo = new StaffRepositoryImpl();

    @Override
    public List<Staff> getAllStaff() {
        return staffRepo.findAll();
    }

    @Override
    public Staff getStaffParId(Long id) {
        Staff s = staffRepo.findById(id);
        if (s == null) {
            throw new RuntimeException("Erreur : Membre du staff introuvable (ID " + id + ")");
        }
        return s;
    }

    @Override
    public void mettreAJourSalaire(Long id, Double nouveauSalaire) {
        // 1. Vérifier si l'employé existe
        Staff s = staffRepo.findById(id);
        if (s == null) {
            throw new RuntimeException("Erreur : Impossible de mettre à jour, employé introuvable.");
        }

        // 2. Validation Métier
        if (nouveauSalaire < 0) {
            throw new RuntimeException("Erreur : Le salaire ne peut pas être négatif.");
        }

        // 3. Mise à jour
        s.setSalaire(nouveauSalaire);
        staffRepo.update(s);

        System.out.println("Succès : Le salaire de " + s.getNom() + " a été mis à jour (" + nouveauSalaire + " DH).");
    }

    @Override
    public void supprimerStaff(Long id) {
        if (staffRepo.findById(id) == null) {
            throw new RuntimeException("Erreur : ID introuvable.");
        }
        staffRepo.deleteById(id);
    }
}