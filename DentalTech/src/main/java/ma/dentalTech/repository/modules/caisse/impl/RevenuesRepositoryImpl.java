package ma.dentalTech.repository.modules.caisse.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    @Override
    public List<Revenues> findAll() {
        String sql = "SELECT * FROM revenu";
        List<Revenues> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapRevenu(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() Revenues", e);
        }
    }

    @Override
    public Revenues findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM revenu WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapRevenu(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() Revenues, id=" + id, e);
        }
    }

    public void create(Revenues r) {
        String sql = """
            INSERT INTO revenu (cabinet_id, titre, description, montant, date_revenu, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, r.getCabinetId());
            ps.setString(2, r.getTitre());
            ps.setString(3, r.getDescription());
            ps.setBigDecimal(4, r.getMontant() == null ? BigDecimal.ZERO : r.getMontant());
            ps.setTimestamp(5, r.getDateRevenu() != null ? Timestamp.valueOf(r.getDateRevenu()) : null);
            ps.setString(6, r.getCreePar());
            ps.setString(7, r.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Revenues", e);
        }
    }
    @Override
    public void update(Revenues r) {
        if (r.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE revenu
               SET cabinet_id = ?, titre = ?, description = ?, montant = ?, date_revenu = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, r.getCabinetId());
            ps.setString(2, r.getTitre());
            ps.setString(3, r.getDescription());
            ps.setBigDecimal(4, r.getMontant() == null ? BigDecimal.ZERO : r.getMontant());
            ps.setTimestamp(5, r.getDateRevenu() != null ? Timestamp.valueOf(r.getDateRevenu()) : null);
            ps.setString(6, r.getModifiePar());
            ps.setLong(7, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Revenues, id=" + r.getId(), e);
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

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() Revenues, id=" + id, e);
        }
    }

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM revenu WHERE date_revenu BETWEEN ? AND ?";
        List<Revenues> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapRevenu(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDateBetween() Revenues", e);
        }
    }
    @Override
    public Double calculateTotalRevenus(LocalDateTime start, LocalDateTime end) {
        String sql = """
        SELECT COALESCE(SUM(montant), 0)
        FROM revenu
        WHERE date_creation BETWEEN ? AND ?
    """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalRevenus", e);
        }
    }

    @Override
    public Double calculateTotalOtherRevenue(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COALESCE(SUM(montant),0) AS total FROM revenu WHERE date_revenu BETWEEN ? AND ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalOtherRevenue()", e);
        }
    }
}
