package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.notification.Notification;
import java.util.List;

public interface NotificationService {
    // Envoie une notif à un utilisateur précis
    void envoyerNotification(Long utilisateurId, String titre, String message);

    // Récupère les notifs d'un utilisateur (avec les infos utilisateur remplies)
    List<Notification> getNotificationsUtilisateur(Long utilisateurId);
}