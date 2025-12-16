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
     * Dernières notifications (ex : pour le dashboard).
     */
    List<Notification> findRecentForUser(Long utilisateurId, int limit);


    Integer countNonLuesPourSecretaire(Long utilisateurId);
    Integer countAlertesImportantesPourSecretaire(Long utilisateurId);
    Integer countNotificationsSystemeNonLues();

    // Methodes utilise f mon dashboard - aya berday
    List<Notification> findByUtilisateurIdAndDateBetween(Long utilisateurId, LocalDateTime start, LocalDateTime end);
    List<Notification> findByUtilisateurIdAndPriorite(Long utilisateurId, PrioriteNotification priorite);



}


