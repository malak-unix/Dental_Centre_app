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

    // =====================================================================================
    // Mapping ResultSet → Revenues
    // =====================================================================================
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

    // =====================================================================================
    // CRUD
    // =====================================================================================

    @Override
    public List<Revenues> findAll() {
        String sql = "SELECT * FROM revenu ORDER BY date_revenu DESC";
        List<Revenues> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findAll() Revenues", e);
        }
    }

    @Override
    public Revenues findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM revenu WHERE id = ?";
        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findById() Revenues", e);
        }
    }

    @Override
    public void create(Revenues entity) {
        String sql = "INSERT INTO revenu(cabinet_id, titre, description, montant, date_revenu) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getCabinetId());
            ps.setString(2, entity.getTitre());
            ps.setString(3, entity.getDescription());
            ps.setDouble(4, entity.getMontant() != null ? entity.getMontant() : 0);

            if (entity.getDateRevenu() != null)
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateRevenu()));
            else
                ps.setNull(5, Types.TIMESTAMP);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getLong(1));
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur create() Revenues", e);
        }
    }

    @Override
    public void update(Revenues entity) {
        if (entity.getId() == null)
            throw new IllegalArgumentException("Revenues update: id est null");

        String sql = "UPDATE revenu SET cabinet_id=?, titre=?, description=?, montant=?, date_revenu=? " +
                "WHERE id=?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, entity.getCabinetId());
            ps.setString(2, entity.getTitre());
            ps.setString(3, entity.getDescription());
            ps.setDouble(4, entity.getMontant() != null ? entity.getMontant() : 0);

            if (entity.getDateRevenu() != null)
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateRevenu()));
            else
                ps.setNull(5, Types.TIMESTAMP);

            ps.setLong(6, entity.getId());

            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur update() Revenues", e);
        }
    }

    @Override
    public void delete(Revenues entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM revenu WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur deleteById() Revenues", e);
        }
    }

    // =====================================================================================
    // Méthodes spéciales
    // =====================================================================================

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM revenu WHERE date_revenu BETWEEN ? AND ? ORDER BY date_revenu";

        List<Revenues> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }

            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByDateBetween() Revenues", e);
        }
    }

    @Override
    public Double calculateTotalOtherRevenue(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(montant) AS total FROM revenu WHERE date_revenu BETWEEN ? AND ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble("total");
                    return rs.wasNull() ? 0.0 : v;
                }
            }

            return 0.0;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur calculateTotalOtherRevenue()", e);
        }
    }
}
