package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryJdbcImpl implements ChargesRepository {

    private static final String INSERT_SQL = """
        INSERT INTO charge (cabinet_id, titre, description, montant, date_charge)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE charge
           SET cabinet_id = ?,
               titre = ?,
               description = ?,
               montant = ?,
               date_charge = ?
         WHERE id = ?
        """;

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM charge WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_charge
          FROM charge
         WHERE id = ?
        """;

    private static final String SELECT_ALL_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_charge
          FROM charge
        """;

    private static final String SELECT_BETWEEN_DATES_SQL = """
        SELECT id, cabinet_id, titre, description, montant, date_charge
          FROM charge
         WHERE date_charge BETWEEN ? AND ?
         ORDER BY date_charge ASC
        """;

    private static final String TOTAL_CHARGES_SQL = """
        SELECT SUM(montant) AS total
          FROM charge
         WHERE date_charge BETWEEN ? AND ?
        """;

    // =============== MAPPER ===============
    private Charges map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long cabinetId = rs.getLong("cabinet_id");
        if (rs.wasNull()) cabinetId = null;

        String titre = rs.getString("titre");
        String description = rs.getString("description");

        Double montant = rs.getDouble("montant");
        if (rs.wasNull()) montant = null;

        Timestamp ts = rs.getTimestamp("date_charge");
        LocalDateTime dateCharge = ts != null ? ts.toLocalDateTime() : null;

        return Charges.builder()
                .id(id)
                .cabinetId(cabinetId)
                .titre(titre)
                .description(description)
                .montant(montant)
                .dateCharge(dateCharge)
                .build();
    }

    // =============== CRUD ===============
    @Override
    public List<Charges> findAll() throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            List<Charges> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findAll() Charges", e);
        }
    }

    @Override
    public Charges findById(Long id) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findById() Charges, id=" + id, e);
        }
    }

    @Override
    public void create(Charges entity) throws DaoException {
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

            if (entity.getDateCharge() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateCharge()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur create() Charges", e);
        }
    }

    @Override
    public void update(Charges entity) throws DaoException {
        if (entity.getId() == null) throw new DaoException("update() Charges sans id");

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

            if (entity.getDateCharge() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDateCharge()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setLong(6, entity.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new DaoException("Aucune charge mise à jour, id=" + entity.getId());
        } catch (SQLException e) {
            throw new DaoException("Erreur update() Charges", e);
        }
    }

    @Override
    public void delete(Charges entity) throws DaoException {
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
            throw new DaoException("Erreur deleteById() Charges, id=" + id, e);
        }
    }

    // =============== Spécifiques ===============
    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BETWEEN_DATES_SQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            List<Charges> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findByDateBetween() Charges", e);
        }
    }

    @Override
    public Double calculateTotalCharges(LocalDateTime start, LocalDateTime end) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(TOTAL_CHARGES_SQL)) {

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
            throw new DaoException("Erreur calculateTotalCharges()", e);
        }
    }
}
