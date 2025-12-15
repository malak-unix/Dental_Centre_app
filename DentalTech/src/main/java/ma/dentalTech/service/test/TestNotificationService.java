package ma.dentalTech.service.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.modules.notification.api.NotificationRepository;
import ma.dentalTech.service.modules.notification.api.NotificationService;
import ma.dentalTech.service.modules.notification.impl.NotificationServiceImpl;

import java.util.List;

public class TestNotificationService {

    private final NotificationService notificationService;

    private Long notificationId;

    public TestNotificationService() {
        // on récupère le repo depuis ApplicationContext
        NotificationRepository notifRepo = ApplicationContext.getBean(NotificationRepository.class);
        // on instancie le service à partir du repo
        this.notificationService = new NotificationServiceImpl(notifRepo);
    }

    // =====================================================
    // INSERT : tester envoyerNotification(...)
    // =====================================================
    void insertProcess() {

        System.out.println("=== [NotificationService] INSERT ===");

        // utilisateur_id = 1 (ADMIN) vient des seeds
        Notification n = notificationService.envoyerNotification(
                1L,                                // utilisateurId
                "Notification de test service",    // titre
                "Ceci est un test du service.",    // message
                PrioriteNotification.MOYENNE,      // priorité
                "TEST_SERVICE"                     // utilisateur système
        );

        notificationId = n.getId();
        System.out.println("Notification créée via service, id = " + notificationId);
    }

    // =====================================================
    // SELECT : tester getNotificationsUtilisateur(...)
    // =====================================================
    void selectProcess() {

        System.out.println("=== [NotificationService] SELECT ===");

        if (notificationId != null) {
            // on pourrait aller relire la notif spécifique
            List<Notification> listUser = notificationService.getNotificationsUtilisateur(1L);
            System.out.println("Notifications pour utilisateur 1 = " + listUser.size());

            // affichage simple
            listUser.forEach(System.out::println);
        } else {
            System.out.println("Aucune notification créée pour le moment.");
        }
    }

    // =====================================================
    // UPDATE : ton service ne propose pas encore de méthode de mise à jour
    // =====================================================
    void updateProcess() {

        System.out.println("=== [NotificationService] UPDATE ===");
        // Idem que pour OrdonnanceService :
        // Quand tu ajouteras par ex. mettreAJourContenu(...),
        // on l’utilisera ici.
    }

    // =====================================================
    // DELETE : tester supprimerNotification(...)
    // =====================================================
    void deleteProcess() {

        System.out.println("=== [NotificationService] DELETE ===");

        if (notificationId != null) {
            notificationService.supprimerNotification(notificationId);
            System.out.println("Notification supprimée via service, id = " + notificationId);
        } else {
            System.out.println("Aucune notification à supprimer (notificationId == null)");
        }
    }

    public static void main(String[] args) {
        TestNotificationService t = new TestNotificationService();

        t.insertProcess();
        t.selectProcess();
        t.updateProcess();   // pour l’instant, neutre
        t.selectProcess();
        t.deleteProcess();
    }
}
