package ma.dentalTech.service.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.common.AlerteDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {

    private final NotificationRepository notificationRepo;
    private final UtilisateurRepository utilisateurRepo;

    // ✅ constructeur principal (celui qu’on veut utiliser)
    public DashboardServiceImpl(NotificationRepository notificationRepo, UtilisateurRepository utilisateurRepo) {
        this.notificationRepo = notificationRepo;
        this.utilisateurRepo = utilisateurRepo;
    }

    // ✅ fallback si jamais (ApplicationContext garde compatibilité)
    public DashboardServiceImpl(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
        this.utilisateurRepo = null;
    }

    @Override
    public DashboardDTO getDashboard(Long utilisateurId) throws ServiceException {
        if (utilisateurId == null) throw new ServiceException("utilisateurId null");

        // ✅ détecter rôle via utilisateur.role_id
        LibelleRole role = detectRole(utilisateurId);

        DashboardFeaturesDTO features = buildFeatures(role);

        DashboardDTO.DashboardDTOBuilder b = DashboardDTO.builder()
                .role(role.name())
                .features(features);

        switch (role) {
            case ADMIN -> b.admin(buildAdmin());
            case MEDECIN -> b.medecin(buildMedecin());
            default -> b.secretaire(buildSecretaire(utilisateurId));
        }

        return b.build();
    }

    private LibelleRole detectRole(Long userId) {
        if (utilisateurRepo == null) return LibelleRole.SECRETAIRE;

        try {
            List<String> roles = utilisateurRepo.getRoleLibellesOfUser(userId);
            if (roles == null || roles.isEmpty()) return LibelleRole.SECRETAIRE;
            return LibelleRole.valueOf(roles.get(0));
        } catch (Exception e) {
            return LibelleRole.SECRETAIRE;
        }
    }

    private DashboardFeaturesDTO buildFeatures(LibelleRole role) {
        if (role == LibelleRole.ADMIN) {
            return DashboardFeaturesDTO.builder()
                    .voirStatsAdmin(true)
                    .voirNotifications(true)
                    .voirAlertes(true)
                    .voirCaisse(false)
                    .voirClientEnCours(false)
                    .voirRdvEtFileAttente(false)
                    .build();
        }
        if (role == LibelleRole.MEDECIN) {
            return DashboardFeaturesDTO.builder()
                    .voirRdvEtFileAttente(true)
                    .voirClientEnCours(true)
                    .voirCaisse(false)
                    .voirStatsAdmin(false)
                    .voirNotifications(true)
                    .voirAlertes(true)
                    .build();
        }
        // SECRETAIRE
        return DashboardFeaturesDTO.builder()
                .voirRdvEtFileAttente(true)
                .voirClientEnCours(false)
                .voirCaisse(true)
                .voirStatsAdmin(false)
                .voirNotifications(true)
                .voirAlertes(true)
                .build();
    }

    // ========================= SECRETAIRE =========================

    private SecretaireDashboardResponseDTO buildSecretaire(Long utilisateurId) {

        List<Notification> all = safeFindAllNotifications(utilisateurId);
        all.sort(Comparator.comparing(this::safeDateTime).reversed());

        List<NotificationDTO> notifications = new ArrayList<>();
        for (Notification n : all) notifications.add(toNotificationDTO(n));

        int nbNotificationsNonLues = safeCountUnread(utilisateurId);

        List<AlerteDTO> alertes = new ArrayList<>();
        for (Notification n : all) {
            if (isAlerte(n)) alertes.add(toAlerteDTO(n));
        }

        int nbAlertesNonLues = alertes.size();

        return SecretaireDashboardResponseDTO.builder()
                .nbPatients(0)
                .nbRdvDuJour(0)
                .nbEnAttente(0)
                .recetteDuJour(BigDecimal.ZERO)
                .rdvDuJour(List.of())
                .fileAttente(List.of())
                .nbNotificationsNonLues(nbNotificationsNonLues)
                .nbAlertesNonLues(nbAlertesNonLues)
                .alertes(alertes)
                .notifications(notifications)
                .build();
    }

    // ========================= MEDECIN =========================
    private MedecinDashboardResponseDTO buildMedecin() {
        return MedecinDashboardResponseDTO.builder()
                .nbPatientsDuJour(0)
                .nbRdvDuJour(0)
                .nbActesRealises(0)
                .recetteDuJour(BigDecimal.ZERO)
                .rdvDuJour(List.of())
                .patientEnCours(null)
                .build();
    }

    // ========================= ADMIN =========================
    private AdminDashboardResponseDTO buildAdmin() {
        return AdminDashboardResponseDTO.builder()
                .nbUtilisateurs(0)
                .nbAdmins(0)
                .nbActesRealises(0)
                .recetteDuJour(BigDecimal.ZERO)
                .utilisateurs(List.of())
                .referentiels(null)
                .sauvegarde(null)
                .build();
    }

    // ========================= HELPERS NOTIFS =========================

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
        try {
            return n.getPriorite() == PrioriteNotification.HAUTE;
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
            if (n.getDateCreation() != null) return n.getDateCreation();
        } catch (Exception ignore) {}
        return LocalDateTime.now();
    }
}
