package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.PlageHoraireRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlageHoraireRepositoryImpl implements PlageHoraireRepository {

    @Override
    public List<PlageHoraire> findAll() {
        String sql = "SELECT * FROM plage_horaire";
        List<PlageHoraire> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapPlageHoraire(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() PlageHoraire", e);
        }
    }

    @Override
    public PlageHoraire findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM plage_horaire WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapPlageHoraire(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() PlageHoraire, id=" + id, e);
        }
    }

    @Override
    public void create(PlageHoraire p) {
        String sql = """
            INSERT INTO plage_horaire (detail_journee_id, heure_debut, heure_fin, disponible, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, p.getDetailJourneeId());
            ps.setTime(2, Time.valueOf(p.getHeureDebut()));
            ps.setTime(3, Time.valueOf(p.getHeureFin()));
            ps.setBoolean(4, Boolean.TRUE.equals(p.getDisponible()));
            ps.setString(5, p.getCreePar());
            ps.setString(6, p.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() PlageHoraire", e);
        }
    }

    @Override
    public void update(PlageHoraire p) {
        if (p.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE plage_horaire
               SET detail_journee_id = ?, heure_debut = ?, heure_fin = ?, disponible = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, p.getDetailJourneeId());
            ps.setTime(2, Time.valueOf(p.getHeureDebut()));
            ps.setTime(3, Time.valueOf(p.getHeureFin()));
            ps.setBoolean(4, Boolean.TRUE.equals(p.getDisponible()));
            ps.setString(5, p.getModifiePar());
            ps.setLong(6, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() PlageHoraire, id=" + p.getId(), e);
        }
    }

    @Override
    public void delete(PlageHoraire entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM plage_horaire WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() PlageHoraire, id=" + id, e);
        }
    }

    @Override
    public List<PlageHoraire> findByDetailJourneeId(Long detailJourneeId) {
        String sql = "SELECT * FROM plage_horaire WHERE detail_journee_id = ?";
        return selectList(sql, detailJourneeId);
    }

    @Override
    public List<PlageHoraire> findDisponiblesByDetailJournee(Long detailJourneeId) {
        String sql = "SELECT * FROM plage_horaire WHERE detail_journee_id = ? AND disponible = 1";
        return selectList(sql, detailJourneeId);
    }

    private List<PlageHoraire> selectList(String sql, Long id) {
        List<PlageHoraire> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPlageHoraire(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur selectList() PlageHoraire", e);
        }
    }
}
