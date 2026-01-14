package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.service.modules.users.api.NotificationService;

import java.time.LocalDate;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    private void requireId(Long id, String field) throws ServiceException {
        if (id == null || id <= 0) throw new ServiceException(field + " invalide");
    }

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        return repo.findByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        return repo.findUnreadByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        return repo.findByDate(utilisateurId, date);
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        return repo.findByType(utilisateurId, type);
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        return repo.findByTitre(utilisateurId, titre);
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return repo.findByPriorite(utilisateurId, priorite);
    }

    @Override
    public void markAsRead(Long notificationId) {
        repo.markAsRead(notificationId);
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        repo.markAllAsReadForUser(utilisateurId);
    }

    @Override
    public Notification findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<Notification> findAll() {
        return repo.findAll();
    }

    @Override
    public void create(Notification n) {
        repo.create(n);
    }

    @Override
    public void update(Notification n) {
        repo.update(n);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
