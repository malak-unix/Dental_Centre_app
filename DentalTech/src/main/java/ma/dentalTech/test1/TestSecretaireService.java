package ma.dentalTech.test.services;

import ma.dentalTech.entities.secretaire.Secretaire;
import ma.dentalTech.service.modules.users.api.SecretaireService;
import ma.dentalTech.service.modules.users.impl.SecretaireServiceImpl;

public class TestSecretaireService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : SECRETAIRE ---");
        SecretaireService secretaireService = new SecretaireServiceImpl();

        try {
            Secretaire s = new Secretaire();
            s.setNom("Ibrahimi");
            s.setPrenom("Salma");
            s.setEmail("salma.ibrahimi" + System.currentTimeMillis() + "@clinique.ma");
            s.setLogin("salma" + System.currentTimeMillis());
            s.setMotDePass_hash("secret123");
            s.setActif(true);

            // Hérité de Staff
            s.setSalaire(5500.0);

            // Action
            secretaireService.recruterSecretaire(s);

            System.out.println("✅ SUCCÈS : Secrétaire " + s.getNom() + " recrutée.");
            System.out.println("   ID généré : " + s.getId());

        } catch (Exception e) {
            System.out.println("❌ ÉCHEC : " + e.getMessage());
            e.printStackTrace();
        }
    }
}