package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    // ======== Mapping helper ========
    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setMessage(rs.getString("message"));
        n.setLue(rs.getBoolean("lue"));

        // titre (String/Enum)
        try {
            String t = rs.getString("titre");
            if (t != null && !t.isBlank()) n.setTitre(TitreNotification.valueOf(t));
        } catch (Exception ignored) {}

        // priorite (String/Enum)
        try {
            String p = rs.getString("priorite");
            if (p != null && !p.isBlank()) n.setPriorite(PrioriteNotification.valueOf(p));
        } catch (Exception ignored) {}

        // type (optionnel selon ton schema)
        try {
            String ty = rs.getString("type");
            if (ty != null && !ty.isBlank()) n.setType(TypeNotification.valueOf(ty));
        } catch (SQLException ignored) {
            // colonne absente -> ok
        } catch (Exception ignored) {}

        // date_notification (si existe) sinon fallback date/time
        Timestamp ts = null;
        try {
            ts = rs.getTimestamp("date_notification");
        } catch (SQLException ignored) {}

        if (ts != null) {
            n.setDate(ts.toLocalDateTime().toLocalDate());
            n.setTime(ts.toLocalDateTime().toLocalTime());
        } else {
            // fallback si ton schema a date + time
            try {
                Date d = rs.getDate("date");
                if (d != null) n.setDate(d.toLocalDate());
            } catch (SQLException ignored) {}

            try {
                Time t = rs.getTime("time");
                if (t != null) n.setTime(t.toLocalTime());
            } catch (SQLException ignored) {}
        }

        // utilisateur minimal (id)
        try {
            long userId = rs.getLong("utilisateur_id");
            Utilisateur u = new Utilisateur();
            u.setId(userId);
            n.setUtilisateur(u);
        } catch (Exception ignored) {}

        return n;
    }

    private List<Notification> queryList(String sql, Object... params) {
        List<Notification> out = new ArrayList<>();
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("NotificationRepository error: " + e.getMessage(), e);
        }
        return out;
    }

    // ======== API ========

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        return queryList("SELECT * FROM notification WHERE utilisateur_id = ? ORDER BY id DESC", utilisateurId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        return queryList("SELECT * FROM notification WHERE utilisateur_id = ? AND lue = false ORDER BY id DESC", utilisateurId);
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        // si date_notification existe : compare date(ts) = ?
        return queryList(
                "SELECT * FROM notification WHERE utilisateur_id = ? AND DATE(date_notification) = ? ORDER BY id DESC",
                utilisateurId, Date.valueOf(date)
        );
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        // seulement si colonne type existe
        return queryList(
                "SELECT * FROM notification WHERE utilisateur_id = ? AND type = ? ORDER BY id DESC",
                utilisateurId, type != null ? type.name() : null
        );
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        return queryList(
                "SELECT * FROM notification WHERE utilisateur_id = ? AND titre = ? ORDER BY id DESC",
                utilisateurId, titre != null ? titre.name() : null
        );
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        return queryList(
                "SELECT * FROM notification WHERE utilisateur_id = ? AND priorite = ? ORDER BY id DESC",
                utilisateurId, priorite != null ? priorite.name() : null
        );
    }

    @Override
    public void markAsRead(Long notificationId) {
        String sql = "UPDATE notification SET lue = true WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("markAsRead error: " + e.getMessage(), e);
        }
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        String sql = "UPDATE notification SET lue = true WHERE utilisateur_id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("markAllAsReadForUser error: " + e.getMessage(), e);
        }
    }

    // ======== CrudRepository ========

    @Override
    public Notification findById(Long id) {
        List<Notification> list = queryList("SELECT * FROM notification WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Notification> findAll() {
        return queryList("SELECT * FROM notification ORDER BY id DESC");
    }

    @Override
    public void create(Notification n) {
        String sql = "INSERT INTO notification(utilisateur_id, titre, lue, message, date_notification, priorite) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long userId = (n.getUtilisateur() != null) ? n.getUtilisateur().getId() : null;
            ps.setLong(1, userId != null ? userId : 0L);

            ps.setString(2, n.getTitre() != null ? n.getTitre().name() : null);
            ps.setBoolean(3, n.isLue());
            ps.setString(4, n.getMessage());

            // date_notification
            LocalDate d = n.getDate() != null ? n.getDate() : LocalDate.now();
            LocalTime t = n.getTime() != null ? n.getTime() : LocalTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(d.atTime(t)));

            ps.setString(6, n.getPriorite() != null ? n.getPriorite().name() : "MOYENNE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) n.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("create Notification error: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Notification n) {
        String sql = "UPDATE notification SET titre=?, message=?, priorite=?, lue=? WHERE id=?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, n.getTitre() != null ? n.getTitre().name() : null);
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getPriorite() != null ? n.getPriorite().name() : null);
            ps.setBoolean(4, n.isLue());
            ps.setLong(5, n.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update Notification error: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete Notification error: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Notification n) {
        if (n != null && n.getId() != null) deleteById(n.getId());
    }
}
