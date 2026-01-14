package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.PrioriteNotification;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class NotificationRepositoryImpl implements NotificationRepository {

    @SuppressWarnings("unused")
    private final Connection connection;

    public NotificationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public NotificationRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) return List.of();

        String sql = """
            SELECT *
            FROM notification
            WHERE utilisateur_id = ?
            ORDER BY date_notification DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByUtilisateur(Notification) userId=" + utilisateurId, e);
        }
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) return List.of();

        String sql = """
            SELECT *
            FROM notification
            WHERE utilisateur_id = ? AND lue = false
            ORDER BY date_notification DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findUnreadByUtilisateur(Notification) userId=" + utilisateurId, e);
        }
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        if (utilisateurId == null || date == null) return List.of();

        // Filtre par date sur DATETIME
        String sql = """
            SELECT *
            FROM notification
            WHERE utilisateur_id = ?
              AND DATE(date_notification) = ?
            ORDER BY date_notification DESC
        """;

        List<Notification> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDate(Notification)", e);
        }
    }

    // Non présent dans ton schema (type/titre/priorite enums custom) => à implémenter plus tard si tu ajoutes colonnes
    @Override public List<Notification> findByType(Long utilisateurId, TypeNotification type) { return List.of(); }
    @Override public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) { return List.of(); }
    @Override public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) { return List.of(); }

    @Override
    public void markAsRead(Long notificationId) {
        if (notificationId == null) return;

        String sql = "UPDATE notification SET lue = true WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, notificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur markAsRead(Notification) id=" + notificationId, e);
        }
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        if (utilisateurId == null) return;

        String sql = "UPDATE notification SET lue = true WHERE utilisateur_id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur markAllAsReadForUser(Notification) userId=" + utilisateurId, e);
        }
    }

    // CrudRepository stubs
    @Override public Notification findById(Long id) { return null; }
    @Override public List<Notification> findAll() { return new ArrayList<>(); }
    @Override public void create(Notification n) {}
    @Override public void update(Notification n) {}
    @Override public void deleteById(Long id) {}
    @Override public void delete(Notification n) { if (n != null) deleteById(n.getId()); }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setLue(rs.getBoolean("lue"));
        n.setMessage(rs.getString("message"));

        // Si ton entité Notification a setTitre / setPriorite / setDateNotification, adapte ici.
        // Sinon laisse minimum: message + lue.

        // date_notification DATETIME
        Timestamp ts = rs.getTimestamp("date_notification");
        if (ts != null) {
            // si ton entité a LocalDateTime
            try {
                n.getClass().getMethod("setDateNotification", java.time.LocalDateTime.class)
                        .invoke(n, ts.toLocalDateTime());
            } catch (Exception ignored) {}
        }

        try {
            String titre = rs.getString("titre");
            if (titre != null) {
                n.getClass().getMethod("setTitre", String.class).invoke(n, titre);
            }
        } catch (Exception ignored) {}

        return n;
    }
}
