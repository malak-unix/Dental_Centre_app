package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryJdbcImpl implements FactureRepository {

    private static final String INSERT_SQL = """
        INSERT INTO facture (consultation_id, date_facture, total_facture, total_paye, statut)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE facture
           SET consultation_id = ?,
               date_facture = ?,
               total_facture = ?,
               total_paye = ?,
               statut = ?
         WHERE id = ?
        """;

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM facture WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, consultation_id, date_facture, total_facture, total_paye, reste, statut
          FROM facture
         WHERE id = ?
        """;

    private static final String SELECT_ALL_SQL = """
        SELECT id, consultation_id, date_facture, total_facture, total_paye, reste, statut
          FROM facture
        """;

    private static final String SELECT_BETWEEN_DATES_SQL = """
        SELECT id, consultation_id, date_facture, total_facture, total_paye, reste, statut
          FROM facture
         WHERE date_facture BETWEEN ? AND ?
         ORDER BY date_facture ASC
        """;

    private static final String TOTAL_FACTURES_SQL = """
        SELECT SUM(total_facture) AS total
          FROM facture
         WHERE date_facture BETWEEN ? AND ?
        """;

    private static final String TOTAL_REGLE_SQL = """
        SELECT SUM(total_paye) AS total
          FROM facture
         WHERE date_facture BETWEEN ? AND ?
        """;

    private static final String TOTAL_NON_REGLE_SQL = """
        SELECT SUM(total_facture - total_paye) AS total
          FROM facture
         WHERE date_facture BETWEEN ? AND ?
        """;

    // =============== MAPPER ===============
    private Facture map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long consultationId = rs.getLong("consultation_id");
        if (rs.wasNull()) consultationId = null;

        Date sqlDate = rs.getDate("date_facture");
        LocalDate dateFacture = sqlDate != null ? sqlDate.toLocalDate() : null;

        Double totalFacture = rs.getDouble("total_facture");
        if (rs.wasNull()) totalFacture = null;

        Double totalPaye = rs.getDouble("total_paye");
        if (rs.wasNull()) totalPaye = null;

        Double reste = rs.getDouble("reste");
        if (rs.wasNull()) reste = null;

        String statutStr = rs.getString("statut");
        StatutFacture statut = statutStr != null ? StatutFacture.valueOf(statutStr) : null;

        return Facture.builder()
                .id(id)
                .consultationId(consultationId)
                .dateFacture(dateFacture)
                .totalFacture(totalFacture)
                .totalPaye(totalPaye)
                .reste(reste)
                .statut(statut)
                .build();
    }

    // =============== CRUD ===============
    @Override
    public List<Facture> findAll() throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            List<Facture> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findAll() Facture", e);
        }
    }

    @Override
    public Facture findById(Long id) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findById() Facture, id=" + id, e);
        }
    }

    @Override
    public void create(Facture entity) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            if (entity.getConsultationId() != null) {
                ps.setLong(1, entity.getConsultationId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            if (entity.getDateFacture() != null) {
                ps.setDate(2, Date.valueOf(entity.getDateFacture()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (entity.getTotalFacture() != null) {
                ps.setDouble(3, entity.getTotalFacture());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            if (entity.getTotalPaye() != null) {
                ps.setDouble(4, entity.getTotalPaye());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.setString(5, entity.getStatut() != null ? entity.getStatut().name() : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur create() Facture", e);
        }
    }

    @Override
    public void update(Facture entity) throws DaoException {
        if (entity.getId() == null) throw new DaoException("update() Facture sans id");

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            if (entity.getConsultationId() != null) {
                ps.setLong(1, entity.getConsultationId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            if (entity.getDateFacture() != null) {
                ps.setDate(2, Date.valueOf(entity.getDateFacture()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (entity.getTotalFacture() != null) {
                ps.setDouble(3, entity.getTotalFacture());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            if (entity.getTotalPaye() != null) {
                ps.setDouble(4, entity.getTotalPaye());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.setString(5, entity.getStatut() != null ? entity.getStatut().name() : null);
            ps.setLong(6, entity.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new DaoException("Aucune facture mise à jour, id=" + entity.getId());
        } catch (SQLException e) {
            throw new DaoException("Erreur update() Facture", e);
        }
    }

    @Override
    public void delete(Facture entity) throws DaoException {
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
            throw new DaoException("Erreur deleteById() Facture, id=" + id, e);
        }
    }

    // =============== Spécifiques ===============
    @Override
    public List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end) throws DaoException {
        // date_facture est un DATE → on ignore l'heure dans la requête
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BETWEEN_DATES_SQL)) {

            ps.setDate(1, Date.valueOf(start.toLocalDate()));
            ps.setDate(2, Date.valueOf(end.toLocalDate()));

            List<Facture> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findByDateBetween() Facture", e);
        }
    }

    @Override
    public Double calculateTotalFactures(LocalDateTime start, LocalDateTime end) throws DaoException {
        return executeTotalQuery(TOTAL_FACTURES_SQL, start, end, "Erreur calculateTotalFactures()");
    }

    @Override
    public Double calculateTotalRegle(LocalDateTime start, LocalDateTime end) throws DaoException {
        return executeTotalQuery(TOTAL_REGLE_SQL, start, end, "Erreur calculateTotalRegle()");
    }

    @Override
    public Double calculateTotalNonRegle(LocalDateTime start, LocalDateTime end) throws DaoException {
        return executeTotalQuery(TOTAL_NON_REGLE_SQL, start, end, "Erreur calculateTotalNonRegle()");
    }

    private Double executeTotalQuery(String sql, LocalDateTime start, LocalDateTime end, String errorMessage)
            throws DaoException {

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start.toLocalDate()));
            ps.setDate(2, Date.valueOf(end.toLocalDate()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    if (rs.wasNull()) return 0.0;
                    return total;
                }
                return 0.0;
            }
        } catch (SQLException e) {
            throw new DaoException(errorMessage, e);
        }
    }
}
