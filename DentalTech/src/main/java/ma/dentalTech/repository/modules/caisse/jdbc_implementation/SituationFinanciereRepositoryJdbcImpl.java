package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SituationFinanciereRepositoryJdbcImpl implements SituationFinanciereRepository {

    private static final String INSERT_SQL = """
        INSERT INTO situation_financiere
            (dossier_id, medecin_id, total_des_actes, total_paye, credit, statut)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE situation_financiere
           SET dossier_id = ?,
               medecin_id = ?,
               total_des_actes = ?,
               total_paye = ?,
               credit = ?,
               statut = ?
         WHERE id = ?
        """;

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM situation_financiere WHERE id = ?";

    private static final String SELECT_BY_ID_SQL = """
        SELECT id, dossier_id, medecin_id, total_des_actes, total_paye, credit, statut
          FROM situation_financiere
         WHERE id = ?
        """;

    private static final String SELECT_ALL_SQL = """
        SELECT id, dossier_id, medecin_id, total_des_actes, total_paye, credit, statut
          FROM situation_financiere
        """;

    private static final String SELECT_LAST_SQL = """
        SELECT id, dossier_id, medecin_id, total_des_actes, total_paye, credit, statut
          FROM situation_financiere
         ORDER BY id DESC
         LIMIT 1
        """;

    // =============== MAPPER ===============
    private SituationFinanciere map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long dossierId = rs.getLong("dossier_id");
        if (rs.wasNull()) dossierId = null;

        Long medecinId = rs.getLong("medecin_id");
        if (rs.wasNull()) medecinId = null;

        Double totalDesActes = rs.getDouble("total_des_actes");
        if (rs.wasNull()) totalDesActes = null;

        Double totalPaye = rs.getDouble("total_paye");
        if (rs.wasNull()) totalPaye = null;

        Double credit = rs.getDouble("credit");
        if (rs.wasNull()) credit = null;

        String statutStr = rs.getString("statut");
        StatutSituationFinanciere statut = statutStr != null ? StatutSituationFinanciere.valueOf(statutStr) : null;

        return SituationFinanciere.builder()
                .id(id)
                .dossierId(dossierId)
                .medecinId(medecinId)
                .totalDesActes(totalDesActes)
                .totalPaye(totalPaye)
                .credit(credit)
                .statut(statut)
                .build();
    }

    // =============== CRUD ===============
    @Override
    public List<SituationFinanciere> findAll() throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            List<SituationFinanciere> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur findAll() SituationFinanciere", e);
        }
    }

    @Override
    public SituationFinanciere findById(Long id) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur findById() SituationFinanciere, id=" + id, e);
        }
    }

    @Override
    public void create(SituationFinanciere entity) throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getDossierId()); // NOT NULL en base

            if (entity.getMedecinId() != null) {
                ps.setLong(2, entity.getMedecinId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            if (entity.getTotalDesActes() != null) {
                ps.setDouble(3, entity.getTotalDesActes());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            if (entity.getTotalPaye() != null) {
                ps.setDouble(4, entity.getTotalPaye());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            if (entity.getCredit() != null) {
                ps.setDouble(5, entity.getCredit());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            ps.setString(6, entity.getStatut() != null ? entity.getStatut().name() : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur create() SituationFinanciere", e);
        }
    }

    @Override
    public void update(SituationFinanciere entity) throws DaoException {
        if (entity.getId() == null) throw new DaoException("update() SituationFinanciere sans id");

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setLong(1, entity.getDossierId());

            if (entity.getMedecinId() != null) {
                ps.setLong(2, entity.getMedecinId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            if (entity.getTotalDesActes() != null) {
                ps.setDouble(3, entity.getTotalDesActes());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            if (entity.getTotalPaye() != null) {
                ps.setDouble(4, entity.getTotalPaye());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            if (entity.getCredit() != null) {
                ps.setDouble(5, entity.getCredit());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            ps.setString(6, entity.getStatut() != null ? entity.getStatut().name() : null);
            ps.setLong(7, entity.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new DaoException("Aucune situation financière mise à jour, id=" + entity.getId());
        } catch (SQLException e) {
            throw new DaoException("Erreur update() SituationFinanciere", e);
        }
    }

    @Override
    public void delete(SituationFinanciere entity) throws DaoException {
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
            throw new DaoException("Erreur deleteById() SituationFinanciere, id=" + id, e);
        }
    }

    @Override
    public SituationFinanciere findLast() throws DaoException {
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_LAST_SQL);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return map(rs);
            return null;
        } catch (SQLException e) {
            throw new DaoException("Erreur findLast() SituationFinanciere", e);
        }
    }
}
