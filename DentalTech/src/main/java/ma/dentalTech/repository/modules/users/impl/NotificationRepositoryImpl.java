package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    // =========================================================================
    // MAPPING (ResultSet -> Notification)
    // =========================================================================
    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        // --- Champs hérités de BaseEntity ---
        Long id = rs.getLong("id");
        if (!rs.wasNull()) {
            n.setId(id);
        }

        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) n.setDateCreation(dateCrea.toLocalDateTime());

        // Adaptez le nom du setter selon votre BaseEntity (setDateModification ou setDateDerniereModification)
        Timestamp dateModif = rs.getTimestamp("date_modification");
// CORRECTION
        if (dateModif != null) n.setDateDerniereModification(dateModif.toLocalDateTime());
        n.setCreePar(rs.getString("cree_par"));
        n.setModifiePar(rs.getString("modifie_par"));

        // --- Champs de Notification ---
        Long uId = rs.getLong("utilisateur_id");
        if (!rs.wasNull()) {
            n.setUtilisateurId(uId);
        }

        n.setTitre(rs.getString("titre"));
        n.setMessage(rs.getString("message"));

        // Gestion de l'Enum Priorité
        String prioriteStr = rs.getString("priorite");
        if (prioriteStr != null) {
            try {
                n.setPriorite(PrioriteNotification.valueOf(prioriteStr));
            } catch (IllegalArgumentException e) {
                n.setPriorite(null);
            }
        }

        Timestamp tNotif = rs.getTimestamp("date_notification");
        if (tNotif != null) n.setDateNotification(tNotif.toLocalDateTime());

        Timestamp tEnvoi = rs.getTimestamp("date_envoi");
        if (tEnvoi != null) n.setDateEnvoi(tEnvoi.toLocalDateTime());

        return n;
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public void create(Notification n) {
        String sql = """
                INSERT INTO notification
                (utilisateur_id, titre, message, priorite,
                 date_notification, date_envoi,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Utilisateur ID
            if (n.getUtilisateurId() != null) {
                ps.setLong(1, n.getUtilisateurId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            // 2. Titre & Message
            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());

            // 4. Priorité
            if (n.getPriorite() != null) {
                ps.setString(4, n.getPriorite().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            // 5. Date Notification
            if (n.getDateNotification() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(n.getDateNotification()));
            } else {
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            // 6. Date Envoi
            if (n.getDateEnvoi() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(n.getDateEnvoi()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            // 7, 8, 9. Audit (Date Creation, Cree Par, Modifie Par)
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, n.getCreePar());
            ps.setString(9, n.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    n.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
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
                       date_envoi = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (n.getUtilisateurId() != null) {
                ps.setLong(1, n.getUtilisateurId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());

            if (n.getPriorite() != null) {
                ps.setString(4, n.getPriorite().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (n.getDateNotification() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(n.getDateNotification()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            if (n.getDateEnvoi() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(n.getDateEnvoi()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            // Date Modification = Maintenant
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, n.getModifiePar());

            ps.setLong(9, n.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la notification id=" + n.getId(), e);
        }
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche notification id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification ORDER BY date_notification DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des notifications", e);
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
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression notification id=" + id, e);
        }
    }

    // =========================================================================
    // Méthodes spécifiques (NotificationRepository)
    // =========================================================================

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        String sql = "SELECT * FROM notification WHERE utilisateur_id = ? ORDER BY date_notification DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche notifications pour utilisateur=" + utilisateurId, e);
        }
        return list;
    }

    @Override
    public List<Notification> findByUtilisateurIdAndPriorite(Long utilisateurId, PrioriteNotification priorite) {
        String sql = "SELECT * FROM notification WHERE utilisateur_id = ? AND priorite = ? ORDER BY date_notification DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setString(2, priorite.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche notif user=" + utilisateurId + " prio=" + priorite, e);
        }
        return list;
    }

    @Override
    public List<Notification> findByUtilisateurIdAndDateBetween(Long utilisateurId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM notification WHERE utilisateur_id = ? AND date_notification BETWEEN ? AND ? ORDER BY date_notification DESC";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche notif par date", e);
        }
        return list;
    }

    @Override
    public List<Notification> findRecentForUser(Long utilisateurId, int limit) {
        String sql = "SELECT * FROM notification WHERE utilisateur_id = ? ORDER BY date_notification DESC LIMIT ?";
        List<Notification> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche notif récentes", e);
        }
        return list;
    }
}