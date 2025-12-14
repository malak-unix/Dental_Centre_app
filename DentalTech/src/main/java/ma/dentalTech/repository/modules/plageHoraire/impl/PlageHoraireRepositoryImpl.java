package ma.dentalTech.repository.modules.plageHoraire.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;
import ma.dentalTech.repository.common.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlageHoraireRepositoryImpl implements PlageHoraireRepository {

    @Override
    public void create(PlageHoraire entity) {
        String sql = "INSERT INTO plage_horaire " +
                "(detail_journee_id, heure_debut, heure_fin, disponible, date_creation, date_modification, cree_par, modifie_par) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getDetailJourneeId());

            if (entity.getHeureDebut() != null) {
                ps.setTime(2, Time.valueOf(entity.getHeureDebut()));
            } else {
                ps.setNull(2, Types.TIME);
            }

            if (entity.getHeureFin() != null) {
                ps.setTime(3, Time.valueOf(entity.getHeureFin()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            ps.setBoolean(4, entity.getDisponible() != null ? entity.getDisponible() : Boolean.TRUE);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(now)); // date_creation
            ps.setTimestamp(6, Timestamp.valueOf(now)); // date_modification

            ps.setString(7, entity.getCreePar());
            ps.setString(8, entity.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                    entity.setDateCreation(now);
                    entity.setDateDerniereModification(now);
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création de la plage horaire", e);
        }
    }

    @Override
    public PlageHoraire findById(Long id) {
        String sql = "SELECT * FROM plage_horaire WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de la plage horaire id=" + id, e);
        }
    }

    @Override
    public List<PlageHoraire> findAll() {
        String sql = "SELECT * FROM plage_horaire";

        List<PlageHoraire> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du chargement de toutes les plages horaires", e);
        }
    }

    @Override
    public void update(PlageHoraire entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("Impossible de mettre à jour une plage horaire sans id");
        }

        String sql = "UPDATE plage_horaire SET " +
                "detail_journee_id = ?, " +
                "heure_debut = ?, " +
                "heure_fin = ?, " +
                "disponible = ?, " +
                "date_modification = ?, " +
                "modifie_par = ? " +
                "WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, entity.getDetailJourneeId());

            if (entity.getHeureDebut() != null) {
                ps.setTime(2, Time.valueOf(entity.getHeureDebut()));
            } else {
                ps.setNull(2, Types.TIME);
            }

            if (entity.getHeureFin() != null) {
                ps.setTime(3, Time.valueOf(entity.getHeureFin()));
            } else {
                ps.setNull(3, Types.TIME);
            }

            ps.setBoolean(4, entity.getDisponible() != null ? entity.getDisponible() : Boolean.TRUE);

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(now)); // date_modification
            ps.setString(6, entity.getModifiePar());
            ps.setLong(7, entity.getId());

            ps.executeUpdate();
            entity.setDateDerniereModification(now);

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la plage horaire id=" + entity.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM plage_horaire WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression de la plage horaire id=" + id, e);
        }
    }

    @Override
    public void delete(PlageHoraire entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        deleteById(entity.getId());
    }

    @Override
    public List<PlageHoraire> findByDetailJourneeId(Long detailJourneeId) {
        String sql = "SELECT * FROM plage_horaire WHERE detail_journee_id = ?";
        List<PlageHoraire> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, detailJourneeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du chargement des plages pour detail_journee_id=" + detailJourneeId, e);
        }
    }

    @Override
    public List<PlageHoraire> findDisponiblesByDetailJournee(Long detailJourneeId) {
        String sql = "SELECT * FROM plage_horaire WHERE detail_journee_id = ? AND disponible = TRUE";
        List<PlageHoraire> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, detailJourneeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du chargement des plages disponibles pour detail_journee_id=" + detailJourneeId, e);
        }
    }

    private PlageHoraire map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long detailJourneeId = rs.getLong("detail_journee_id");

        Time tDebut = rs.getTime("heure_debut");
        Time tFin = rs.getTime("heure_fin");

        LocalTime heureDebut = (tDebut != null) ? tDebut.toLocalTime() : null;
        LocalTime heureFin = (tFin != null) ? tFin.toLocalTime() : null;

        Boolean disponible = rs.getBoolean("disponible");
        if (rs.wasNull()) disponible = null;

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModif = rs.getTimestamp("date_modification");

        LocalDateTime dateCreation = (tsCreation != null) ? tsCreation.toLocalDateTime() : null;
        LocalDateTime dateDerniereModif = (tsModif != null) ? tsModif.toLocalDateTime() : null;

        String creePar = rs.getString("cree_par");
        String modifiePar = rs.getString("modifie_par");

        return PlageHoraire.builder()
                .id(id)
                .detailJourneeId(detailJourneeId)
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .disponible(disponible)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateDerniereModif)
                .creePar(creePar)
                .modifiePar(modifiePar)
                .build();
    }
}
