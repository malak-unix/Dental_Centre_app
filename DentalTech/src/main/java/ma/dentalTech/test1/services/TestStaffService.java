package ma.dentalTech.test1.services;

import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.service.modules.users.api.StaffService;
import ma.dentalTech.service.modules.users.impl.StaffServiceImpl;

import java.util.List;

public class TestStaffService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : STAFF (RH) ---");
        StaffService staffService = new StaffServiceImpl();

        try {
            // 1. Lister tout le personnel
            List<Staff> equipe = staffService.getAllStaff();
            System.out.println("📋 Effectif total : " + equipe.size() + " employés.");

            if (!equipe.isEmpty()) {
                // On prend le premier employé trouvé pour le tester
                Staff chanceux = equipe.get(0);
                System.out.println("👉 Employé choisi : " + chanceux.getNom() + " (Salaire actuel : " + chanceux.getSalaire() + " DH)");

                // 2. Augmentation de salaire
                double nouveauSalaire = chanceux.getSalaire() + 1000.0;
                System.out.println("💰 Tentative d'augmentation à " + nouveauSalaire + " DH...");

                staffService.mettreAJourSalaire(chanceux.getId(), nouveauSalaire);

                // 3. Vérification immédiate
                Staff verif = staffService.getStaffParId(chanceux.getId());
                if (verif.getSalaire().equals(nouveauSalaire)) {
                    System.out.println("✅ SUCCÈS : Le salaire a bien été mis à jour !");
                } else {
                    System.out.println("❌ ÉCHEC : Le salaire n'a pas changé.");
                }
            } else {
                System.out.println("⚠️ Aucun staff pour tester l'augmentation. Lancez TestMedecin ou TestSecretaire avant.");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
}