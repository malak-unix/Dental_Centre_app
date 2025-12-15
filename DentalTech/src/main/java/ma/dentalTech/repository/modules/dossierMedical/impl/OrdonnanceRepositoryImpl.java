package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // =========================================================================================
    // Méthode de mapping : ResultSet -> Ordonnance
    // =========================================================================================
    private Ordonnance map(ResultSet rs) throws SQLException {
        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");

        Long dossierId = rs.getLong("dossier_id");
        if (rs.wasNull()) {
            dossierId = null;
        }

        Long consultationId = rs.getLong("consultation_id");
        if (rs.wasNull()) {
            consultationId = null;
        }

        Date dateOrdo = rs.getDate("date_ordo");

        return Ordonnance.builder()
                .id(rs.getLong("id"))
                .dossierId(dossierId)
                .consultationId(consultationId)
                .date(dateOrdo != null ? dateOrdo.toLocalDate() : null)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
                .dateDerniereModification(tsModification != null ? tsModification.toLocalDateTime() : null)
                .build();
    }

    // =========================================================================================
    // CRUD : méthodes de CrudRepository
    // =========================================================================================

    @Override
    public void create(Ordonnance ordonnance) {
        String sql = """
                INSERT INTO ordonnance(dossier_id, consultation_id, date_ordo, date_creation)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // dossier_id
            if (ordonnance.getDossierId() != null) {
                ps.setLong(1, ordonnance.getDossierId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            // consultation_id
            if (ordonnance.getConsultationId() != null) {
                ps.setLong(2, ordonnance.getConsultationId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            // date_ordo
            if (ordonnance.getDate() != null) {
                ps.setDate(3, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // date_creation
            LocalDateTime dc = ordonnance.getDateCreation() != null
                    ? ordonnance.getDateCreation()
                    : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dc));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ordonnance.setId(rs.getLong(1));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public void update(Ordonnance ordonnance) {
        String sql = """
                UPDATE ordonnance
                   SET dossier_id = ?,
                       consultation_id = ?,
                       date_ordo = ?,
                       date_modification = ?
                 WHERE id = ?
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // dossier_id
            if (ordonnance.getDossierId() != null) {
                ps.setLong(1, ordonnance.getDossierId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            // consultation_id
            if (ordonnance.getConsultationId() != null) {
                ps.setLong(2, ordonnance.getConsultationId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            // date_ordo
            if (ordonnance.getDate() != null) {
                ps.setDate(3, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // date_modification -> mapped sur BaseEntity.dateDerniereModification
            LocalDateTime dm = ordonnance.getDateDerniereModification() != null
                    ? ordonnance.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dm));

            ps.setLong(5, ordonnance.getId());

            ps.executeUpdate();
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
        return null;
    }

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance ORDER BY id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
        return list;
    }

    @Override
    public void delete(Ordonnance ordonnance) {
        if (ordonnance != null && ordonnance.getId() != null) {
            deleteById(ordonnance.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    // =========================================================================================
    // Méthodes spécifiques de OrdonnanceRepository
    // =========================================================================================

    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM ordonnance WHERE dossier_id = ? ORDER BY date_ordo, id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM ordonnance WHERE consultation_id = ? ORDER BY date_ordo, id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM ordonnance WHERE date_ordo = ? ORDER BY id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
                SELECT * FROM ordonnance
                 WHERE date_ordo BETWEEN ? AND ?
                 ORDER BY date_ordo, id
                """;
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM ordonnance";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return 0;
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM ordonnance
                 ORDER BY date_ordo DESC, id DESC
                 LIMIT ? OFFSET ?
                """;
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }
}
