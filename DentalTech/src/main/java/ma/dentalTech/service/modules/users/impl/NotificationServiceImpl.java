package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.impl.NotificationRepositoryImpl;
import ma.dentalTech.service.modules.users.api.NotificationService; // Import Interface

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    // On a besoin de DEUX repositories ici
    private final NotificationRepository notificationRepo = new NotificationRepositoryImpl();
    private final UtilisateurRepository utilisateurRepo = new UtilisateurRepositoryImpl();

    @Override
    public void envoyerNotification(Long utilisateurId, String titre, String message) {
        // 1. Vérifier que le destinataire existe
        Utilisateur u = utilisateurRepo.findById(utilisateurId);
        if (u == null) {
            throw new RuntimeException("Erreur : Utilisateur destinataire introuvable.");
        }

        // 2. Création de l'objet
        Notification n = new Notification();
        n.setUtilisateurId(utilisateurId); // Lien BDD (Clé étrangère)
        n.setTitre(titre);
        n.setMessage(message);

        // Note : La date et la priorité sont souvent gérées par défaut dans le constructeur
        // ou le repo, sinon tu peux les set ici (n.setDateCreation(LocalDateTime.now()))

        // 3. Sauvegarde
        notificationRepo.create(n);
        System.out.println("Notification envoyée à " + u.getNom());
    }

    @Override
    public List<Notification> getNotificationsUtilisateur(Long utilisateurId) {
        // A. On récupère les notifications brutes (juste les données de la table Notification)
        List<Notification> notifs = notificationRepo.findByUtilisateurId(utilisateurId);

        if (notifs.isEmpty()) return notifs;

        // B. On récupère l'utilisateur complet (avec Nom, Prénom, etc.)
        Utilisateur user = utilisateurRepo.findById(utilisateurId);

        // C. --- LIAISON MANUELLE (Hydratation) ---
        // On remplit l'objet 'utilisateur' dans chaque notification pour l'affichage
        // C'est ce qui permet de faire n.getUtilisateur().getNom() plus tard
        if (user != null) {
            for (Notification n : notifs) {
                n.setUtilisateur(user);
            }
        }

        return notifs;
    }
}