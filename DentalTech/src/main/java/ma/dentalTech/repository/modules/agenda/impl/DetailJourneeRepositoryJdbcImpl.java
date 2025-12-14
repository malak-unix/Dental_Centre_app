package ma.dentalTech.repository.modules.agenda.impl;

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

    // =========================================
    //  MAPPER ResultSet -> Entity DetailJournee
    // =========================================
    private DetailJournee map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long agendaId = rs.getLong("agenda_id");
        if (rs.wasNull()) agendaId = null;

        Date dateSql = rs.getDate("date_jour");
        LocalDate dateJour = dateSql != null ? dateSql.toLocalDate() : null;

        Time tDebut = rs.getTime("heure_debut_travail");
        LocalTime heureDebut = tDebut != null ? tDebut.toLocalTime() : null;

        Time tFin = rs.getTime("heure_fin_travail");
        LocalTime heureFin = tFin != null ? tFin.toLocalTime() : null;

        String etatStr = rs.getString("etat_jour");
        StatutJournee etat = null;
        if (etatStr != null) {
            try {
                etat = StatutJournee.valueOf(etatStr);
            } catch (IllegalArgumentException e) {
                // Valeur inconnue en DB → on laisse etat = null
            }
        }

        String commentaire = rs.getString("commentaire");

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");

        return DetailJournee.builder()
                .id(id)
                .agendaId(agendaId) // assure-toi que tu as bien ce champ dans l'entité DetailJournee
                .dateJour(dateJour)
                .heureDebutTravaillee(heureDebut)
                .heureFinTravaillee(heureFin)
                .etatJour(etat)
                .commentaire(commentaire)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
                .dateDerniereModification(tsModification != null ? tsModification.toLocalDateTime() : null)
                .build();
    }

    // ====================================================
    // Implémentation CrudRepository<DetailJournee, Long>
    // ====================================================

    @Override
    public List<DetailJournee> findAll() {
        String sql = "SELECT * FROM detail_journee ORDER BY date_jour";
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
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findById() DetailJournee, id=" + id, e);
        }
    }

    @Override
    public void create(DetailJournee entity) {
        String sql = """
            INSERT INTO detail_journee
                (agenda_id, date_jour, heure_debut_travail, heure_fin_travail, etat_jour, commentaire)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (entity.getAgendaId() == null) {
                throw new IllegalArgumentException("agendaId obligatoire pour créer une DetailJournee");
            }
            ps.setLong(1, entity.getAgendaId());

            if (entity.getDateJour() != null) {
                ps.setDate(2, Date.valueOf(entity.getDateJour()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (entity.getHeureDebutTravaillee() != null) {
                ps.setTime(3, Time.valueOf(entity.getHeureDebutTravaillee()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            if (entity.getHeureFinTravaillee() != null) {
                ps.setTime(4, Time.valueOf(entity.getHeureFinTravaillee()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            if (entity.getEtatJour() != null) {
                ps.setString(5, entity.getEtatJour().name());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setString(6, entity.getCommentaire());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur create() DetailJournee", e);
        }
    }

    @Override
    public void update(DetailJournee entity) {
        if (entity.getId() == null) {
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

            if (entity.getAgendaId() == null) {
                throw new IllegalArgumentException("agendaId obligatoire pour mettre à jour une DetailJournee");
            }
            ps.setLong(1, entity.getAgendaId());

            if (entity.getDateJour() != null) {
                ps.setDate(2, Date.valueOf(entity.getDateJour()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            if (entity.getHeureDebutTravaillee() != null) {
                ps.setTime(3, Time.valueOf(entity.getHeureDebutTravaillee()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            if (entity.getHeureFinTravaillee() != null) {
                ps.setTime(4, Time.valueOf(entity.getHeureFinTravaillee()));
            } else {
                ps.setNull(4, Types.TIME);
            }

            if (entity.getEtatJour() != null) {
                ps.setString(5, entity.getEtatJour().name());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setString(6, entity.getCommentaire());
            ps.setLong(7, entity.getId());

            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur update() DetailJournee, id=" + entity.getId(), e);
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

    // ====================================================
    //  Méthodes spécifiques
    // ====================================================

    @Override
    public List<DetailJournee> findByAgendaId(Long agendaId) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ? ORDER BY date_jour";
        List<DetailJournee> list = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByAgendaId(), agendaId=" + agendaId, e);
        }
    }

    @Override
    public DetailJournee findByAgendaIdAndDateJour(Long agendaId, LocalDate dateJour) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ? AND date_jour = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(dateJour));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur findByAgendaIdAndDateJour(), agendaId=" + agendaId, e);
        }
    }
}
