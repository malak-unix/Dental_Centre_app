package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final Connection connection;

    public NotificationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    // --- 1. CORRECTION DE L'OVERRIDE : Renommé en findByUtilisateur pour correspondre à l'interface ---
    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE utilisateur_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getLong("id"));
                    n.setMessage(rs.getString("message"));

                    // --- 2. CORRECTION setLue : Utilise 'lue' comme défini dans votre entité ---
                    n.setLue(rs.getBoolean("lue"));

                    // --- 3. CORRECTION AMBIGUÏTÉ DATE : Utilise java.sql explicitement ---
                    java.sql.Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        n.setDate(sqlDate.toLocalDate());
                    }

                    java.sql.Time sqlTime = rs.getTime("time");
                    if (sqlTime != null) {
                        n.setTime(sqlTime.toLocalTime());
                    }

                    notifications.add(n);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        return List.of();
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        return List.of();
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        return List.of();
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        return List.of();
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return List.of();
    }

    @Override
    public void markAsRead(Long id) {
        String sql = "UPDATE notification SET lue = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {

    }

    // --- Méthodes obligatoires du CrudRepository ---
    @Override public Notification findById(Long id) { return null; }
    @Override public List<Notification> findAll() { return new ArrayList<>(); }
    @Override public void create(Notification n) {}
    @Override public void update(Notification n) {}
    @Override public void deleteById(Long id) {}
    @Override public void delete(Notification n) { if (n != null) deleteById(n.getId()); }
}