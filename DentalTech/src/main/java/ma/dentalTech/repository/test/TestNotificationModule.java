package ma.dentalTech.repository.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.modules.notification.api.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TestNotificationModule {

    private final NotificationRepository notificationRepo;
    private Long idNotification;

    public TestNotificationModule() {
        this.notificationRepo = ApplicationContext.getBean(NotificationRepository.class);
    }

    // INSERT
    void insertProcess() {

        System.out.println("=== [Notification] INSERT ===");

        Notification n = Notification.builder()
                .utilisateurId(1L) // ADMIN (seeds)
                .titre("Notif test Malak")
                .message("Ceci est une notification de test.")
                .priorite(PrioriteNotification.MOYENNE)
                .dateNotification(LocalDateTime.now())
                .dateEnvoi(LocalDateTime.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        notificationRepo.create(n);
        idNotification = n.getId();
        System.out.println("Notification créée id=" + idNotification);
    }

    // SELECT
    void selectProcess() {

        System.out.println("=== [Notification] SELECT ===");

        if (idNotification != null) {
            Notification n = notificationRepo.findById(idNotification);
            System.out.println("Notification trouvée : " + n);
        }

        List<Notification> listUser = notificationRepo.findByUtilisateurId(1L);
        System.out.println("Nb notifications utilisateur 1 = " + listUser.size());
    }

    // UPDATE
    void updateProcess() {

        System.out.println("=== [Notification] UPDATE ===");
        if (idNotification == null) return;

        Notification n = notificationRepo.findById(idNotification);
        if (n == null) return;

        n.setMessage("Message MODIFIÉ par le test");
        n.setPriorite(PrioriteNotification.HAUTE);
        n.setModifiePar("TEST_UPDATE");

        notificationRepo.update(n);
        System.out.println("Notification après update : " + notificationRepo.findById(idNotification));
    }

    // DELETE
    void deleteProcess() {

        System.out.println("=== [Notification] DELETE ===");
        if (idNotification == null) return;

        notificationRepo.deleteById(idNotification);
        System.out.println("Notification supprimée id=" + idNotification);
    }

    public static void main(String[] args) {
        TestNotificationModule t = new TestNotificationModule();
        t.insertProcess();
        t.selectProcess();
        t.updateProcess();
        t.selectProcess();
        t.deleteProcess();
    }
}
