package ma.dentalTech.test1.services;

import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.service.modules.users.api.NotificationService;
import ma.dentalTech.service.modules.users.impl.NotificationServiceImpl;

import java.util.List;

public class TestNotificationService {
    public static void main(String[] args) {
        System.out.println("--- TEST SERVICE : NOTIFICATION ---");

        NotificationService notifService = new NotificationServiceImpl();
        Long idUtilisateurCible = 1L; // Assure-toi que l'ID 1 existe en base

        try {
            // 1. Envoi
            notifService.envoyerNotification(idUtilisateurCible, "Rappel", "N'oubliez pas la réunion.");
            System.out.println("✅ Notification envoyée.");

            // 2. Lecture (Test de la liaison Java)
            List<Notification> notifs = notifService.getNotificationsUtilisateur(idUtilisateurCible);

            if (!notifs.isEmpty()) {
                Notification n = notifs.get(notifs.size() - 1); // La dernière
                System.out.println("   Titre : " + n.getTitre());

                // TEST CRUCIAL : Est-ce que getUtilisateur() est rempli ?
                if (n.getUtilisateur() != null) {
                    System.out.println("✅ LIAISON OK : Notification pour " + n.getUtilisateur().getNom());
                } else {
                    System.out.println("❌ LIAISON KO : Utilisateur est null dans la notif !");
                }
            } else {
                System.out.println("⚠️ Aucune notification trouvée pour l'ID " + idUtilisateurCible);
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
        }
    }
}