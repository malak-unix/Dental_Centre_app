package ma.dentalTech.repository.modules.agenda.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaMensuelRepositoryJdbcImpl implements AgendaMensuelRepository {

    // ============================
    //  MAPPER ResultSet -> Entity
    // ============================
    private AgendaMensuel map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long medecinId = rs.getLong("medecin_id");
        if (rs.wasNull()) medecinId = null;

        String moisStr = rs.getString("mois");
        Integer annee = rs.getInt("annee");
        if (rs.wasNull()) annee = null;

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");

        return AgendaMensuel.builder()
                .id(id)
                .medecinId(medecinId)
                .mois(moisStr != null ? Mois.valueOf(moisStr) : null)
                .annee(annee != null ? annee : 0)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
                .dateDerniereModification(tsModification != null ? tsModification.toLocalDateTime() : null)
                .build();
    }

    // ============================
    //  CRUD (impl CrudRepository)
    // ============================

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM agenda_mensuel";
        List<AgendaMensuel> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findAll() AgendaMensuel", e);
        }
    }

    @Override
    public AgendaMensuel findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM agenda_mensuel WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
                return null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findById() AgendaMensuel, id=" + id, e);
        }
    }

    @Override
    public void create(AgendaMensuel agenda) {
        String sql = """
            INSERT INTO agenda_mensuel (medecin_id, mois, annee)
            VALUES (?, ?, ?)
            """;

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (agenda.getMedecinId() == null) {
                throw new IllegalArgumentException("medecinId obligatoire pour créer un AgendaMensuel");
            }

            ps.setLong(1, agenda.getMedecinId());
            ps.setString(2, agenda.getMois() != null ? agenda.getMois().name() : null);
            ps.setInt(3, agenda.getAnnee());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    agenda.setId(keys.getLong(1));
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur create() AgendaMensuel", e);
        }
    }

    @Override
    public void update(AgendaMensuel agenda) {
        if (agenda.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un AgendaMensuel sans id");
        }

        String sql = """
            UPDATE agenda_mensuel
               SET medecin_id = ?,
                   mois       = ?,
                   annee      = ?
             WHERE id = ?
            """;

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (agenda.getMedecinId() == null) {
                throw new IllegalArgumentException("medecinId obligatoire pour mettre à jour un AgendaMensuel");
            }

            ps.setLong(1, agenda.getMedecinId());
            ps.setString(2, agenda.getMois() != null ? agenda.getMois().name() : null);
            ps.setInt(3, agenda.getAnnee());
            ps.setLong(4, agenda.getId());

            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur update() AgendaMensuel, id=" + agenda.getId(), e);
        }
    }

    @Override
    public void delete(AgendaMensuel entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM agenda_mensuel WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur deleteById() AgendaMensuel, id=" + id, e);
        }
    }

    // ============================
    //  Méthodes spécifiques
    // ============================

    @Override
    public AgendaMensuel findByMedecinAndMonth(Long medecinId, String mois, int annee) {
        String sql = "SELECT * FROM agenda_mensuel WHERE medecin_id = ? AND mois = ? AND annee = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setString(2, mois);
            ps.setInt(3, annee);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByMedecinAndMonth(), medecinId=" + medecinId, e);
        }
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM agenda_mensuel WHERE medecin_id = ?";
        List<AgendaMensuel> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByMedecin(), medecinId=" + medecinId, e);
        }
    }
}
