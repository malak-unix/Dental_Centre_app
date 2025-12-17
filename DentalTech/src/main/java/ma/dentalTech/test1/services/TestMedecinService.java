package ma.dentalTech.test1.services;

import ma.dentalTech.entities.medecin.Medecin;
import ma.dentalTech.service.modules.users.api.MedecinService;
import ma.dentalTech.service.modules.users.impl.MedecinServiceImpl;

import java.time.LocalDate;

public class TestMedecinService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : MEDECIN ---");

        // 1. Instanciation directe (Pas d'ApplicationContext)
        MedecinService medecinService = new MedecinServiceImpl();

        try {
            // 2. Préparation des données
            Medecin dr = new Medecin();
            dr.setNom("Tazi");
            dr.setPrenom("Mehdi");
            dr.setEmail("dr.tazi" + System.currentTimeMillis() + "@clinique.ma");
            dr.setLogin("tazi" + System.currentTimeMillis()); // Login unique
            dr.setMotDePass_hash("123456");
            dr.setActif(true);

            // Spécifique Médecin/Staff
            dr.setSpecialite("Dentiste Généraliste");
            dr.setSalaire(12000.0);
            dr.setPourcentage(0.15); // 15%

            // 3. Appel du service
            medecinService.recruterMedecin(dr);

            System.out.println("✅ SUCCÈS : Médecin créé avec ID " + dr.getId());

            // 4. Vérification lecture
            Medecin lecture = medecinService.getMedecinParId(dr.getId());
            System.out.println("   Lecture en base : " + lecture.getNom() + " - " + lecture.getSpecialite());

        } catch (Exception e) {
            System.out.println("❌ ÉCHEC : " + e.getMessage());
            e.printStackTrace();
        }
    }
}