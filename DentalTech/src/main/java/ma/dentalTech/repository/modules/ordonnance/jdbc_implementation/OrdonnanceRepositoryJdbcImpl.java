package ma.dentalTech.repository.modules.ordonnance.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryJdbcImpl implements OrdonnanceRepository {

    // =========================================================================================
    // Méthode de mapping : ResultSet -> Ordonnance
    // =========================================================================================
    private Ordonnance map(ResultSet rs) throws SQLException {
        Timestamp tsCreation = rs.getTimestamp("date_creation");

        return Ordonnance.builder()
                .id(rs.getLong("id"))
                // pour l’instant on ne mappe que la date + métadonnées de base
                .date(rs.getDate("date_ordo") != null
                        ? rs.getDate("date_ordo").toLocalDate()
                        : null)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
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

            // TODO : quand ton entité Ordonnance aura dossier / consultation,
            //       remplace ces null par les vraies valeurs.
            ps.setObject(1, null); // dossier_id
            ps.setObject(2, null); // consultation_id

            if (ordonnance.getDate() != null) {
                ps.setDate(3, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

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
        } catch (SQLException | DaoException e) {
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
                       date_creation = ?
                 WHERE id = ?
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // TODO : idem, à adapter quand tu ajoutes dossier / consultation dans l'entité
            ps.setObject(1, null); // dossier_id
            ps.setObject(2, null); // consultation_id

            if (ordonnance.getDate() != null) {
                ps.setDate(3, Date.valueOf(ordonnance.getDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            LocalDateTime dc = ordonnance.getDateCreation() != null
                    ? ordonnance.getDateCreation()
                    : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dc));

            ps.setLong(5, ordonnance.getId());

            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ordonnance par ID", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les ordonnances", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ordonnance", e);
        }
    }

    // =========================================================================================
    // Méthodes spécifiques de OrdonnanceRepository
    // =========================================================================================

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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances par date", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances par période", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du comptage des ordonnances", e);
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
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de la page d'ordonnances", e);
        }

        return list;
    }
}
