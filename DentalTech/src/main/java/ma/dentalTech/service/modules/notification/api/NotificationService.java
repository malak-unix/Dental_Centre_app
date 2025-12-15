package ma.dentalTech.service.modules.notification.api;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;

import java.util.List;

public interface NotificationService {

    Notification envoyerNotification(
            Long utilisateurId,
            String titre,
            String message,
            PrioriteNotification priorite,
            String utilisateurSysteme
    );

    List<Notification> getNotificationsUtilisateur(Long utilisateurId);

    List<Notification> getDernieresNotificationsUtilisateur(Long utilisateurId, int limit);

    void supprimerNotification(Long id);
}
