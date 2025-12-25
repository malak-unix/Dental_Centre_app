package ma.dentalTech.service.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.common.AlerteDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {

    private final NotificationRepository notificationRepo;

    public DashboardServiceImpl(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public DashboardDTO getDashboard(Long utilisateurId) throws ServiceException {
        if (utilisateurId == null) {
            throw new ServiceException("utilisateurId null");
        }

        // Ici: on construit au minimum le dashboard secrétaire (notifications + alertes).
        // Si tu as déjà la logique admin/medecin ailleurs, tu pourras la brancher après.

        SecretaireDashboardResponseDTO secretaire = buildSecretaire(utilisateurId);

        DashboardFeaturesDTO features = DashboardFeaturesDTO.builder()
                .voirRdvEtFileAttente(true)
                .voirClientEnCours(false)
                .voirStatsAdmin(false)
                .voirCaisse(true)
                .voirNotifications(true)
                .build();

        return DashboardDTO.builder()
                .features(features)
                .secretaire(secretaire)
                .medecin(null)
                .admin(null)
                .build();
    }

    private SecretaireDashboardResponseDTO buildSecretaire(Long utilisateurId) throws ServiceException {

        // ========== NOTIFICATIONS ==========
        List<Notification> all = safeFindAllNotifications(utilisateurId);
        all.sort(Comparator.comparing(this::safeDateTime).reversed());

        List<NotificationDTO> notifications = new ArrayList<>();
        for (Notification n : all) {
            notifications.add(toNotificationDTO(n));
        }

        int nbNotificationsNonLues = safeCountUnread(utilisateurId);

        // ========== ALERTES ==========
        // Règle simple: une alerte = notification de priorité HAUTE/URGENTE (ou autre selon ton enum)
        List<AlerteDTO> alertes = new ArrayList<>();
        for (Notification n : all) {
            if (isAlerte(n)) {
                alertes.add(toAlerteDTO(n));
            }
        }

        int nbAlertesNonLues = (int) alertes.stream().filter(a -> !a.isLue()).count();

        // Tu gardes tes champs existants (rdv/fileAttente/kpis) si tu les remplis ailleurs.
        // Ici, on ne les casse pas : on met null/0 si tu ne les as pas encore branchés.
        return SecretaireDashboardResponseDTO.builder()
                .nbPatients(null)
                .nbRdvDuJour(0)
                .nbEnAttente(0)
                .recetteDuJour(null)
                .rdvDuJour(List.of())
                .fileAttente(List.of())

                // ✅ Ajouts
                .notifications(notifications)
                .nbNotificationsNonLues(nbNotificationsNonLues)
                .alertes(alertes)
                .nbAlertesNonLues(nbAlertesNonLues)

                .build();
    }

    private List<Notification> safeFindAllNotifications(Long utilisateurId) {
        try {
            List<Notification> list = notificationRepo.findByUtilisateur(utilisateurId);
            return list == null ? List.of() : list;
        } catch (Exception e) {
            return List.of();
        }
    }

    private int safeCountUnread(Long utilisateurId) {
        try {
            List<Notification> unread = notificationRepo.findUnreadByUtilisateur(utilisateurId);
            return unread == null ? 0 : unread.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isAlerte(Notification n) {
        if (n == null) return false;
        try {
            PrioriteNotification p = n.getPriorite();
            // adapte si ton enum a d'autres valeurs
            return p == PrioriteNotification.HAUTE || p == PrioriteNotification.HAUTE;
        } catch (Exception e) {
            return false;
        }
    }

    private NotificationDTO toNotificationDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .source(n.getType() != null ? n.getType().name() : "SYSTEM")
                .titre(n.getTitre() != null ? n.getTitre().name() : "Notification")
                .message(n.getMessage())
                .date(safeDateTime(n))
                .action(null)
                .referenceId(null)
                .build();
    }

    private AlerteDTO toAlerteDTO(Notification n) {
        return AlerteDTO.builder()
                .id(n.getId())
                .type(n.getType() != null ? n.getType().name() : "ALERTE")
                .priorite(n.getPriorite() != null ? n.getPriorite().name() : "HAUTE")
                .titre(n.getTitre() != null ? n.getTitre().name() : "Alerte")
                .message(n.getMessage())
                .date(safeDateTime(n))
                .action(null)
                .referenceId(null)
                .build();
    }

    private LocalDateTime safeDateTime(Notification n) {
        try {
            // adapte si ton Notification hérite BaseEntity: getDateCreation()
            if (n.getDateCreation() != null) return n.getDateCreation();
        } catch (Exception ignore) {}
        return LocalDateTime.now();
    }
}
