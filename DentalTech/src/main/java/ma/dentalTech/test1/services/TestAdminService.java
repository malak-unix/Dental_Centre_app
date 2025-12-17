package ma.dentalTech.test1.services;

import ma.dentalTech.entities.admin.Admin;
import ma.dentalTech.service.modules.users.api.AdminService;
import ma.dentalTech.service.modules.users.impl.AdminServiceImpl;

import java.util.List;

public class TestAdminService {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("      TEST UNITAIRE : ADMIN SERVICE      ");
        System.out.println("=========================================");

        // 1. Instanciation du Service
        AdminService adminService = new AdminServiceImpl();

        // Variable pour stocker l'ID créé
        Long idNouveauAdmin = null;

        try {
            // ---------------------------------------------------------
            // ÉTAPE 1 : CRÉATION (CREATE)
            // ---------------------------------------------------------
            System.out.println("\n--- 1. Tentative de création d'un Admin ---");

            Admin admin = new Admin();
            admin.setNom("Director");
            admin.setPrenom("Testeur");
            // Utilisation du temps pour garantir un email/login unique à chaque test
            long timestamp = System.currentTimeMillis();
            admin.setEmail("admin." + timestamp + "@dental.ma");
            admin.setLogin("admin" + timestamp);
            admin.setMotDePass_hash("admin123");
            admin.setActif(true);

            // Appel du service
            adminService.creerAdmin(admin);

            // On suppose que l'objet admin a été mis à jour avec son ID (si le repo le fait)
            // Sinon, on le retrouvera dans la liste à l'étape suivante.
            System.out.println("✅ Commande de création envoyée avec succès.");


            // ---------------------------------------------------------
            // ÉTAPE 2 : LECTURE (READ)
            // ---------------------------------------------------------
            System.out.println("\n--- 2. Liste des Admins en base ---");

            List<Admin> admins = adminService.getAllAdmins();

            if (admins.isEmpty()) {
                System.out.println("⚠️ La liste est vide ! (Vérifiez la connexion BDD)");
            } else {
                for (Admin a : admins) {
                    System.out.println("   👤 Admin trouvé : ID=" + a.getId() + " | " + a.getNom() + " " + a.getPrenom() + " (" + a.getLogin() + ")");

                    // Si c'est celui qu'on vient de créer, on garde son ID pour le test de suppression
                    if (a.getLogin().equals(admin.getLogin())) {
                        idNouveauAdmin = a.getId();
                    }
                }
            }

            // ---------------------------------------------------------
            // ÉTAPE 3 : SUPPRESSION (DELETE) - Optionnel
            // ---------------------------------------------------------
            if (idNouveauAdmin != null) {
                System.out.println("\n--- 3. Suppression de l'Admin créé (Nettoyage) ---");
                adminService.supprimerAdmin(idNouveauAdmin);

                // Vérification
                List<Admin> checkList = adminService.getAllAdmins();
                boolean estToujoursLa = false;
                for (Admin a : checkList) {
                    if (a.getId().equals(idNouveauAdmin)) {
                        estToujoursLa = true;
                        break;
                    }
                }
                if (!estToujoursLa) {
                    System.out.println("✅ Admin (ID " + idNouveauAdmin + ") supprimé avec succès.");
                } else {
                    System.out.println("❌ Erreur : L'admin est toujours dans la liste.");
                }
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERREUR PENDANT LE TEST :");
            e.printStackTrace();
        }

        System.out.println("\n=========================================");
        System.out.println("             FIN DU TEST");
        System.out.println("=========================================");
    }
}