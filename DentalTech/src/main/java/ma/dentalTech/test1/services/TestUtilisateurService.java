package ma.dentalTech.test1.services;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.service.modules.users.api.UtilisateurService;
import ma.dentalTech.service.modules.users.impl.UtilisateurServiceImpl;

import java.util.List;

public class TestUtilisateurService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : UTILISATEUR (Générique) ---");

        // Instanciation
        UtilisateurService utilisateurService = new UtilisateurServiceImpl();
        Long idTest = null;

        // ---------------------------------------------------------
        // 1. TEST CRÉATION (Cas Nominal)
        // ---------------------------------------------------------
        try {
            System.out.println("\n👉 1. Tentative de création d'un utilisateur standard...");

            Utilisateur u = new Utilisateur();
            u.setNom("Lambda");
            u.setPrenom("Jean");
            // Astuce : System.currentTimeMillis() pour avoir un email unique à chaque test
            u.setEmail("jean.lambda" + System.currentTimeMillis() + "@test.com");
            u.setLogin("jean" + System.currentTimeMillis());
            u.setMotDePass_hash("pass1234"); // Valide (> 4 caractères)
            u.setActif(true);

            utilisateurService.creerUtilisateur(u);
            idTest = u.getId(); // On sauvegarde l'ID pour la suite

            System.out.println("✅ SUCCÈS : Utilisateur créé avec l'ID " + idTest);

        } catch (Exception e) {
            System.out.println("❌ ÉCHEC CRÉATION : " + e.getMessage());
            e.printStackTrace();
        }

        // ---------------------------------------------------------
        // 2. TEST VALIDATION MOT DE PASSE (Cas Erreur)
        // ---------------------------------------------------------
        try {
            System.out.println("\n👉 2. Test mot de passe trop court...");
            Utilisateur weakUser = new Utilisateur();
            weakUser.setNom("Faible");
            weakUser.setEmail("faible@test.com");
            weakUser.setLogin("faible");
            weakUser.setMotDePass_hash("123"); // Trop court (< 4)

            utilisateurService.creerUtilisateur(weakUser);
            System.out.println("❌ ERREUR : J'aurais dû échouer (mot de passe trop court) !");
        } catch (RuntimeException e) {
            System.out.println("✅ SUCCÈS : Le système a bien bloqué le mot de passe court (" + e.getMessage() + ")");
        }

        // ---------------------------------------------------------
        // 3. TEST MODIFICATION
        // ---------------------------------------------------------
        if (idTest != null) {
            try {
                System.out.println("\n👉 3. Test de modification (Update)...");

                // On récupère l'utilisateur créé en étape 1
                Utilisateur aModifier = utilisateurService.getUtilisateurParId(idTest);
                System.out.println("   Nom avant : " + aModifier.getNom());

                // On change son nom
                aModifier.setNom("Lambda Modifié");
                utilisateurService.modifierUtilisateur(aModifier);

                // Vérification
                Utilisateur verif = utilisateurService.getUtilisateurParId(idTest);
                if ("Lambda Modifié".equals(verif.getNom())) {
                    System.out.println("✅ SUCCÈS : Nom modifié correctement.");
                } else {
                    System.out.println("❌ ÉCHEC : Le nom n'a pas changé.");
                }

            } catch (Exception e) {
                System.out.println("❌ ERREUR UPDATE : " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // 4. TEST SUPPRESSION
        // ---------------------------------------------------------
        if (idTest != null) {
            try {
                System.out.println("\n👉 4. Test de suppression...");
                utilisateurService.supprimerUtilisateur(idTest);

                // On essaie de le retrouver pour vérifier qu'il n'existe plus
                Utilisateur fantome = utilisateurService.getUtilisateurParId(idTest);
                if (fantome == null) {
                    System.out.println("✅ SUCCÈS : Utilisateur supprimé.");
                } else {
                    System.out.println("⚠️ Bizarre : L'utilisateur existe encore (ou le service renvoie null au lieu d'exception).");
                }
            } catch (RuntimeException e) {
                // Si getUtilisateurParId lance une erreur quand l'ID n'existe pas, c'est aussi un succès
                System.out.println("✅ SUCCÈS : Utilisateur introuvable après suppression.");
            }
        }
    }
}