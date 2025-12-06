package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenuesRepositoryJdbcImpl implements RevenuesRepository {

    private static final String INSERT_SQL = """
        INSERT INTO revenu (cabinet_id, titre, description, montant, date_revenu)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE revenu
           SET cabinet_id = ?,
               titre = ?,
               description = ?,
               montant = ?,
               date_revenu = ?
         WHERE id = ?
        """;

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM revenu WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_revenu
          FROM revenu
         WHERE id = ?
        """;

    private static final String SELECT_ALL_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_revenu
          FROM revenu
        """;

    private static final String SELECT_BETWEEN_DATES_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_revenu
          FROM revenu
         WHERE date_revenu BETWEEN ? AND ?
         ORDER BY date_revenu ASC
        """;

    private static final String TOTAL_REVENUS_SQL = """
        SELECT SUM(montant) AS total
          FROM revenu
         WHERE date_revenu BETWEEN ? AND ?
        """;

    // ===================== MAPPER =====================
    private Revenues map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long cabinetId = rs.getLong("cabinet_id");
        if (rs.wasNull()) cabinetId = null;

        String titre = rs.getString("titre");
        String description = rs.getString("description");

        Double montant = rs.getDouble("montant");
        if (rs.wasNull()) montant = null;

        Timestamp ts = rs.getTimestamp("date_revenu");
        LocalDateTime dateRevenu = ts != null ? ts.toLocalDateTime() : null;

        return Revenues.builder()
                .id(id)
                .cabinetId(cabinetId)
                .titre(titre)
                .description(description)
                .montant(montant)
                .dateRevenu(dateRevenu)
                .build();
    }

    // ===================== CRUD =====================

    @Override
    public List<Revenues> findAll() throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            List<Revenues> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findAll() Revenues", e);
        }
    }

    @Override
    public Revenues findById(Long id) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findById() Revenues, id=" + id, e);
        }
    }

    @Override
    public void create(Revenues entity) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            if (entity.getCabinetId() != null) {
                ps.setLong(1, entity.getCabinetId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, entity.getTitre());
            ps.setString(3, entity.getDescription());

            if (entity.getMontant() != null) {
                ps.setDouble(4, entity.getMontant());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            if (entity.getDateRevenu() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateRevenu()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur create() Revenues", e);
        }
    }

    @Override
    public void update(Revenues entity) throws DaoException {
        if (entity.getId() == null) {
            throw new DaoException("update() Revenues sans id");
        }

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            if (entity.getCabinetId() != null) {
                ps.setLong(1, entity.getCabinetId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setString(2, entity.getTitre());
            ps.setString(3, entity.getDescription());

            if (entity.getMontant() != null) {
                ps.setDouble(4, entity.getMontant());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            if (entity.getDateRevenu() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateRevenu()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setLong(6, entity.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DaoException("Aucun revenu mis à jour, id=" + entity.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur update() Revenues", e);
        }
    }

    @Override
    public void delete(Revenues entity) throws DaoException {
        if (entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID_SQL)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erreur deleteById() Revenues, id=" + id, e);
        }
    }

    // ===================== Spécifiques =====================

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BETWEEN_DATES_SQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            List<Revenues> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findByDateBetween() Revenues", e);
        }
    }

    @Override
    public Double calculateTotalOtherRevenue(LocalDateTime start, LocalDateTime end) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(TOTAL_REVENUS_SQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    if (rs.wasNull()) return 0.0;
                    return total;
                }
                return 0.0;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur calculateTotalOtherRevenue()", e);
        }
    }
}
