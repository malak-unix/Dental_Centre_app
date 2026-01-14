package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;

import java.time.LocalDate;
import java.util.List;

public interface NotificationService {
    List<Notification> findByUtilisateur(Long utilisateurId);
    List<Notification> findUnreadByUtilisateur(Long utilisateurId);

    List<Notification> findByDate(Long utilisateurId, LocalDate date);
    List<Notification> findByType(Long utilisateurId, TypeNotification type);
    List<Notification> findByTitre(Long utilisateurId, TitreNotification titre);
    List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite);

    void markAsRead(Long notificationId);
    void markAllAsReadForUser(Long utilisateurId);

    // CRUD si tu veux une page admin de gestion
    Notification findById(Long id);
    List<Notification> findAll();
    void create(Notification n);
    void update(Notification n);
    void deleteById(Long id);
}
