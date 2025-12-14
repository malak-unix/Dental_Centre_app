package ma.dentalTech.service.modules.notification.impl;

import ma.dentalTech.service.common.ServiceException;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.modules.notification.api.NotificationRepository;
import ma.dentalTech.service.modules.notification.api.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = Objects.requireNonNull(notificationRepository);
    }

    @Override
    public Notification envoyerNotification(Long utilisateurId,
                                            String titre,
                                            String message,
                                            PrioriteNotification priorite,
                                            String utilisateurSysteme) {

        if (utilisateurId == null) {
            throw ServiceException.validation("utilisateurId obligatoire pour envoyer une notification");
        }
        if (titre == null || titre.isBlank()) {
            throw ServiceException.validation("titre obligatoire pour envoyer une notification");
        }
        if (message == null || message.isBlank()) {
            throw ServiceException.validation("message obligatoire pour envoyer une notification");
        }

        try {
            Notification notif = Notification.builder()
                    .utilisateurId(utilisateurId)
                    .titre(titre)
                    .message(message)
                    .priorite(priorite != null ? priorite : PrioriteNotification.MOYENNE)
                    .dateNotification(LocalDateTime.now())
                    .dateEnvoi(LocalDateTime.now())
                    .creePar(utilisateurSysteme != null ? utilisateurSysteme : "SYSTEM")
                    .modifiePar(utilisateurSysteme != null ? utilisateurSysteme : "SYSTEM")
                    .build();

            notificationRepository.create(notif);
            return notif;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'envoi de la notification", "NOTIF_SEND_ERROR", e);
        }
    }

    @Override
    public List<Notification> getNotificationsUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) {
            throw ServiceException.validation("utilisateurId obligatoire pour lister les notifications");
        }

        try {
            return notificationRepository.findByUtilisateurId(utilisateurId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des notifications de l'utilisateur " + utilisateurId,
                    "NOTIF_LIST_ERROR", e);
        }
    }

    @Override
    public List<Notification> getDernieresNotificationsUtilisateur(Long utilisateurId, int limit) {
        if (utilisateurId == null) {
            throw ServiceException.validation("utilisateurId obligatoire pour lister les notifications récentes");
        }
        if (limit <= 0) {
            throw ServiceException.validation("limit doit être > 0 pour récupérer les notifications récentes");
        }

        try {
            return notificationRepository.findRecentForUser(utilisateurId, limit);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des dernières notifications (utilisateur " + utilisateurId + ")",
                    "NOTIF_RECENT_ERROR", e);
        }
    }

    @Override
    public void supprimerNotification(Long id) {
        if (id == null) {
            throw ServiceException.validation("id obligatoire pour supprimer une notification");
        }

        try {
            Notification n = notificationRepository.findById(id);
            if (n == null) {
                throw ServiceException.notFound("Notification non trouvée pour id=" + id);
            }
            notificationRepository.deleteById(id);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de la notification id=" + id,
                    "NOTIF_DELETE_ERROR", e);
        }
    }
}
