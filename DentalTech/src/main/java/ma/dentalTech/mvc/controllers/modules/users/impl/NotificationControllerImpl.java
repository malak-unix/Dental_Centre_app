package ma.dentalTech.mvc.controllers.modules.users.impl;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.mvc.controllers.modules.users.api.NotificationController;
import ma.dentalTech.service.modules.users.api.NotificationService;

import java.time.LocalDate;
import java.util.List;

public class NotificationControllerImpl implements NotificationController {

    private final NotificationService service;

    public NotificationControllerImpl(NotificationService service) {
        this.service = service;
    }

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        return service.findByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        return service.findUnreadByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        return service.findByDate(utilisateurId, date);
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        return service.findByType(utilisateurId, type);
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        return service.findByTitre(utilisateurId, titre);
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return service.findByPriorite(utilisateurId, priorite);
    }

    @Override
    public void markAsRead(Long notificationId) {
        service.markAsRead(notificationId);
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        service.markAllAsReadForUser(utilisateurId);
    }
}
