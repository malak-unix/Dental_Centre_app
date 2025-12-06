package ma.dentalTech.repository.modules.agenda.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DetailJourneeRepositoryJdbcImpl implements DetailJourneeRepository {

    // ============================
    //  MAPPER ResultSet -> Entity
    // ============================
    private DetailJournee map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long agendaId = rs.getLong("agenda_id");
        if (rs.wasNull()) agendaId = null;

        Date d = rs.getDate("date_jour");
        Time hDebut = rs.getTime("heure_debut_travail");
        Time hFin = rs.getTime("heure_fin_travail");
        String etatStr = rs.getString("etat_jour");
        String commentaire = rs.getString("commentaire");

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");

        return DetailJournee.builder()
                .id(id)
                .agendaId(agendaId)
                .dateJour(d != null ? d.toLocalDate() : null)
                .heureDebutTravaillee(hDebut != null ? hDebut.toLocalTime() : null)
                .heureFinTravaillee(hFin != null ? hFin.toLocalTime() : null)
                .etatJour(etatStr != null ? StatutJournee.valueOf(etatStr) : null)
                .commentaire(commentaire)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
                .dateDerniereModification(tsModification != null ? tsModification.toLocalDateTime() : null)
                .build();
    }

    // ============================
    //  CRUD
    // ============================

    @Override
    public List<DetailJournee> findAll() {
        String sql = "SELECT * FROM detail_journee";
        List<DetailJournee> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findAll() DetailJournee", e);
        }
    }

    @Override
    public DetailJournee findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM detail_journee WHERE id = ?";

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
            throw new RuntimeException("Erreur findById() DetailJournee, id=" + id, e);
        }
    }

    @Override
    public void create(DetailJournee d) {
        String sql = """
                INSERT INTO detail_journee
                    (agenda_id, date_jour, heure_debut_travail, heure_fin_travail, etat_jour, commentaire)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (d.getAgendaId() == null) {
                throw new IllegalArgumentException("agendaId obligatoire pour créer une DetailJournee");
            }

            ps.setLong(1, d.getAgendaId());

            if (d.getDateJour() != null) {
                ps.setDate(2, Date.valueOf(d.getDateJour()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (d.getHeureDebutTravaillee() != null) {
                ps.setTime(3, Time.valueOf(d.getHeureDebutTravaillee()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            if (d.getHeureFinTravaillee() != null) {
                ps.setTime(4, Time.valueOf(d.getHeureFinTravaillee()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            ps.setString(5, d.getEtatJour() != null ? d.getEtatJour().name() : null);
            ps.setString(6, d.getCommentaire());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getLong(1));
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur create() DetailJournee", e);
        }
    }

    @Override
    public void update(DetailJournee d) {
        if (d.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une DetailJournee sans id");
        }

        String sql = """
                UPDATE detail_journee
                   SET agenda_id = ?,
                       date_jour = ?,
                       heure_debut_travail = ?,
                       heure_fin_travail = ?,
                       etat_jour = ?,
                       commentaire = ?
                 WHERE id = ?
                """;

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (d.getAgendaId() == null) {
                throw new IllegalArgumentException("agendaId obligatoire pour update");
            }

            ps.setLong(1, d.getAgendaId());

            if (d.getDateJour() != null) {
                ps.setDate(2, Date.valueOf(d.getDateJour()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (d.getHeureDebutTravaillee() != null) {
                ps.setTime(3, Time.valueOf(d.getHeureDebutTravaillee()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            if (d.getHeureFinTravaillee() != null) {
                ps.setTime(4, Time.valueOf(d.getHeureFinTravaillee()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            ps.setString(5, d.getEtatJour() != null ? d.getEtatJour().name() : null);
            ps.setString(6, d.getCommentaire());
            ps.setLong(7, d.getId());

            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur update() DetailJournee, id=" + d.getId(), e);
        }
    }

    @Override
    public void delete(DetailJournee entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM detail_journee WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur deleteById() DetailJournee, id=" + id, e);
        }
    }

    // ============================
    //  Méthodes spécifiques
    // ============================

    @Override
    public List<DetailJournee> findByAgenda(Long agendaId) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ? ORDER BY date_jour";
        List<DetailJournee> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByAgenda(), agendaId=" + agendaId, e);
        }
    }

    @Override
    public DetailJournee findByAgendaAndDate(Long agendaId, LocalDate date) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ? AND date_jour = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException(
                    "Erreur findByAgendaAndDate(), agendaId=" + agendaId + ", date=" + date,
                    e
            );
        }
    }
}
