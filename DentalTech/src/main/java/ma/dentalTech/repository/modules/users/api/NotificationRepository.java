package ma.dentalTech.repository.modules.users.api;

// Import correct basé sur votre structure
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.common.CrudRepository;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    // Méthode spécifique pour trouver les notifs d'un utilisateur
    List<Notification> findByUtilisateurId(Long utilisateurId);
}