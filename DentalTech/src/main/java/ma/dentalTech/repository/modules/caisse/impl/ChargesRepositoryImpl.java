package ma.dentalTech.repository.modules.caisse.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryImpl implements ChargesRepository {

    @Override
    public List<Charges> findAll() {
        String sql = "SELECT * FROM charge";
        List<Charges> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapCharge(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() Charges", e);
        }
    }

    @Override
    public Charges findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM charge WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapCharge(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() Charges, id=" + id, e);
        }
    }

    @Override
    public void create(Charges c) {
        String sql = """
            INSERT INTO charge (cabinet_id, titre, description, montant, date_charge, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, c.getCabinetId());
            ps.setString(2, c.getTitre());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getMontant() == null ? 0.0 : c.getMontant());
            ps.setTimestamp(5, c.getDateCharge() != null ? Timestamp.valueOf(c.getDateCharge()) : null);
            ps.setString(6, c.getCreePar());
            ps.setString(7, c.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Charges", e);
        }
    }

    @Override
    public void update(Charges c) {
        if (c.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE charge
               SET cabinet_id = ?, titre = ?, description = ?, montant = ?, date_charge = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, c.getCabinetId());
            ps.setString(2, c.getTitre());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getMontant() == null ? 0.0 : c.getMontant());
            ps.setTimestamp(5, c.getDateCharge() != null ? Timestamp.valueOf(c.getDateCharge()) : null);
            ps.setString(6, c.getModifiePar());
            ps.setLong(7, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Charges, id=" + c.getId(), e);
        }
    }

    @Override
    public void delete(Charges entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM charge WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() Charges, id=" + id, e);
        }
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM charge WHERE date_charge BETWEEN ? AND ?";
        List<Charges> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapCharge(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDateBetween() Charges", e);
        }
    }

    @Override
    public Double calculateTotalCharges(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COALESCE(SUM(montant),0) AS total FROM charge WHERE date_charge BETWEEN ? AND ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalCharges()", e);
        }
    }
}
