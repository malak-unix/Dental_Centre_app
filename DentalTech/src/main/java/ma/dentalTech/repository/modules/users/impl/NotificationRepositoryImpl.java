package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
// IMPORTS IMPORTANTS (Vérifiez qu'ils correspondent à vos dossiers)
import ma.dentalTech.entities.notification.Notification;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    // --- MAPPING (ResultSet -> Notification) ---
    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        // Champs de base
        n.setId(rs.getLong("id"));
        n.setTitre(rs.getString("titre"));
        n.setMessage(rs.getString("message"));

        // Champ Utilisateur (C'est ici que ça changeait : on set l'ID directement)
        long uId = rs.getLong("utilisateur_id");
        if (!rs.wasNull()) {
            n.setUtilisateurId(uId);
        }

        // Gestion des dates
        Timestamp dateNotif = rs.getTimestamp("date_notification");
        if (dateNotif != null) n.setDateNotification(dateNotif.toLocalDateTime());

        // On suppose que date_creation en base correspond à dateEnvoi ou dateCreation dans l'entité
        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) {
            n.setDateCreation(dateCrea.toLocalDateTime());
            n.setDateEnvoi(dateCrea.toLocalDateTime()); // On remplit aussi dateEnvoi au cas où
        }

        // Gestion de l'ENUM PrioriteNotification
        String prioStr = rs.getString("priorite");
        if (prioStr != null) {
            try {
                n.setPriorite(PrioriteNotification.valueOf(prioStr));
            } catch (IllegalArgumentException e) {
                // Si la base contient une valeur inconnue, on ignore ou on met une valeur par défaut
                // n.setPriorite(PrioriteNotification.MOYENNE);
            }
        }

        // Champs BaseEntity
        n.setCreePar(rs.getString("cree_par"));
        n.setModifiePar(rs.getString("modifie_par"));

        return n;
    }

    // --- IMPLEMENTATION DES METHODES ---

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
            throw new RuntimeException("Erreur findByUtilisateurId", e);
        }
        return list;
    }

    @Override
    public void create(Notification n) {
        String sql = """
            INSERT INTO notification 
            (utilisateur_id, titre, message, date_notification, priorite, date_creation, cree_par) 
            VALUES (?, ?, ?, ?, ?, NOW(), ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Utilisateur ID (Directement le Long)
            if (n.getUtilisateurId() != null) {
                ps.setLong(1, n.getUtilisateurId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, n.getTitre());
            ps.setString(3, n.getMessage());

            // 4. Date Notification
            if (n.getDateNotification() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(n.getDateNotification()));
            } else {
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            // 5. Priorité (Enum -> String)
            if (n.getPriorite() != null) {
                ps.setString(5, n.getPriorite().name());
            } else {
                ps.setNull(5, Types.VARCHAR); // Ou mettre une valeur par défaut "MOYENNE"
            }

            ps.setString(6, n.getCreePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) n.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création Notification", e);
        }
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Notification n) {
        if (n != null) deleteById(n.getId());
    }

    @Override
    public void update(Notification n) {
        // Optionnel : Update si besoin
    }
}