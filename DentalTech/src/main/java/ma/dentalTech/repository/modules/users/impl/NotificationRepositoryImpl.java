package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    // =========================================================================
    // Mapping ResultSet -> Notification
    // =========================================================================
    private Notification map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long utilisateurId = rs.getLong("utilisateur_id");
        if (rs.wasNull()) utilisateurId = null;

        String prioriteStr = rs.getString("priorite");
        PrioriteNotification priorite = null;
        if (prioriteStr != null) {
            priorite = PrioriteNotification.valueOf(prioriteStr);
        }

        Timestamp tNotif = rs.getTimestamp("date_notification");
        LocalDateTime dateNotif = (tNotif != null) ? tNotif.toLocalDateTime() : null;

        Timestamp tCreate = rs.getTimestamp("date_creation");
        LocalDateTime dateCreation = (tCreate != null) ? tCreate.toLocalDateTime() : null;

        Timestamp tModif = rs.getTimestamp("date_modification");
        LocalDateTime dateModif = (tModif != null) ? tModif.toLocalDateTime() : null;

        // Ici, on mappe aussi dateEnvoi sur date_notification, à toi de l’exploiter côté métier
        return Notification.builder()
                .id(id)
                .utilisateurId(utilisateurId)
                .titre(rs.getString("titre"))
                .message(rs.getString("message"))
                .priorite(priorite)
                .dateNotification(dateNotif)
                .dateEnvoi(dateNotif)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =========================================================================
    // CRUD (CrudRepository)
    // =========================================================================

    @Override
    public void create(Notification n) {
        String sql = """
                INSERT INTO notification
                (utilisateur_id, titre, message, priorite,
                 date_notification, date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (n.getUtilisateurId() == null) {
                throw new IllegalArgumentException("utilisateurId ne doit pas être null pour Notification");
            }
            ps.setLong(1, n.getUtilisateurId());

            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());

            if (n.getPriorite() != null) {
                ps.setString(4, n.getPriorite().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            // date_notification : on prend dateNotification si fournie, sinon maintenant
            LocalDateTime dn = (n.getDateNotification() != null)
                    ? n.getDateNotification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dn));

            // date_creation
            LocalDateTime dc = (n.getDateCreation() != null) ? n.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dc));

            ps.setString(7, n.getCreePar());
            ps.setString(8, n.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    n.setId(rs.getLong(1));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    @Override
    public void update(Notification n) {
        String sql = """
                UPDATE notification
                   SET utilisateur_id = ?,
                       titre = ?,
                       message = ?,
                       priorite = ?,
                       date_notification = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (n.getUtilisateurId() == null) {
                throw new IllegalArgumentException("utilisateurId ne doit pas être null pour Notification");
            }
            ps.setLong(1, n.getUtilisateurId());

            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());

            if (n.getPriorite() != null) {
                ps.setString(4, n.getPriorite().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            LocalDateTime dn = (n.getDateNotification() != null)
                    ? n.getDateNotification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dn));

            LocalDateTime dm = (n.getDateDerniereModification() != null)
                    ? n.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dm));

            ps.setString(7, n.getModifiePar());
            ps.setLong(8, n.getId());

            ps.executeUpdate();

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return null;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification ORDER BY date_notification DESC, id DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return list;
    }

    @Override
    public void delete(Notification n) {
        if (n != null && n.getId() != null) {
            deleteById(n.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    // =========================================================================
    // Méthodes spécifiques (NotificationRepository)
    // =========================================================================

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        String sql = """
                SELECT * FROM notification
                 WHERE utilisateur_id = ?
                 ORDER BY date_notification DESC, id DESC
                """;
        List<Notification> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return list;
    }

    @Override
    public List<Notification> findByUtilisateurIdAndPriorite(Long utilisateurId,
                                                             PrioriteNotification priorite) {
        String sql = """
                SELECT * FROM notification
                 WHERE utilisateur_id = ?
                   AND priorite = ?
                 ORDER BY date_notification DESC, id DESC
                """;
        List<Notification> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setString(2, priorite.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return list;
    }

    @Override
    public List<Notification> findByUtilisateurIdAndDateBetween(Long utilisateurId,
                                                                LocalDateTime start,
                                                                LocalDateTime end) {
        String sql = """
                SELECT * FROM notification
                 WHERE utilisateur_id = ?
                   AND date_notification BETWEEN ? AND ?
                 ORDER BY date_notification DESC, id DESC
                """;
        List<Notification> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return list;
    }

    @Override
    public List<Notification> findRecentForUser(Long utilisateurId, int limit) {
        String sql = """
                SELECT * FROM notification
                 WHERE utilisateur_id = ?
                 ORDER BY date_notification DESC, id DESC
                 LIMIT ?
                """;
        List<Notification> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }

        return list;
    }
}
