package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    /**
     * Toutes les notifications d'un utilisateur.
     */
    List<Notification> findByUtilisateurId(Long utilisateurId);

    /**
     * Notifications d'un utilisateur pour une priorité donnée.
     */
    List<Notification> findByUtilisateurIdAndPriorite(Long utilisateurId,
                                                      PrioriteNotification priorite);

    /**
     * Notifications d'un utilisateur sur un intervalle [start, end].
     */
    List<Notification> findByUtilisateurIdAndDateBetween(Long utilisateurId,
                                                         LocalDateTime start,
                                                         LocalDateTime end);

    /**
     * Dernières notifications (ex : pour le dashboard).
     */
    List<Notification> findRecentForUser(Long utilisateurId, int limit);
}
