package ma.dentalTech.repository.modules.caisse.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryImpl implements FactureRepository {

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT * FROM facture";
        List<Facture> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapFacture(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() Facture", e);
        }
    }

    @Override
    public Facture findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM facture WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapFacture(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() Facture, id=" + id, e);
        }
    }

    @Override
    public void create(Facture f) {
        String sql = """
            INSERT INTO facture (consultation_id, date_facture, total_facture, total_paye, statut, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, f.getConsultationId(), Types.BIGINT);
            ps.setDate(2, f.getDateFacture() != null ? Date.valueOf(f.getDateFacture()) : null);
            ps.setDouble(3, f.getTotalFacture() == null ? 0.0 : f.getTotalFacture());
            ps.setDouble(4, f.getTotalPaye() == null ? 0.0 : f.getTotalPaye());
            ps.setString(5, f.getStatut() != null ? f.getStatut().name() : StatutFacture.NON_PAYEE.name());
            ps.setString(6, f.getCreePar());
            ps.setString(7, f.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) f.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Facture", e);
        }
    }

    @Override
    public void update(Facture f) {
        if (f.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE facture
               SET consultation_id = ?, date_facture = ?, total_facture = ?, total_paye = ?, statut = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setObject(1, f.getConsultationId(), Types.BIGINT);
            ps.setDate(2, f.getDateFacture() != null ? Date.valueOf(f.getDateFacture()) : null);
            ps.setDouble(3, f.getTotalFacture() == null ? 0.0 : f.getTotalFacture());
            ps.setDouble(4, f.getTotalPaye() == null ? 0.0 : f.getTotalPaye());
            ps.setString(5, f.getStatut() != null ? f.getStatut().name() : null);
            ps.setString(6, f.getModifiePar());
            ps.setLong(7, f.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Facture, id=" + f.getId(), e);
        }
    }

    @Override
    public void delete(Facture entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM facture WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() Facture, id=" + id, e);
        }
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        // facture.date_facture est DATE
        LocalDate d1 = start.toLocalDate();
        LocalDate d2 = end.toLocalDate();

        String sql = "SELECT * FROM facture WHERE date_facture BETWEEN ? AND ?";
        List<Facture> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(d1));
            ps.setDate(2, Date.valueOf(d2));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapFacture(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDateBetween() Facture", e);
        }
    }

    @Override
    public Double calculateTotalFactures(LocalDateTime start, LocalDateTime end) {
        LocalDate d1 = start.toLocalDate();
        LocalDate d2 = end.toLocalDate();

        String sql = "SELECT COALESCE(SUM(total_facture),0) AS total FROM facture WHERE date_facture BETWEEN ? AND ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(d1));
            ps.setDate(2, Date.valueOf(d2));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalFactures()", e);
        }
    }

    @Override
    public Double calculateTotalRegle(LocalDateTime start, LocalDateTime end) {
        LocalDate d1 = start.toLocalDate();
        LocalDate d2 = end.toLocalDate();

        String sql = """
            SELECT COALESCE(SUM(total_paye),0) AS total
            FROM facture
            WHERE date_facture BETWEEN ? AND ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(d1));
            ps.setDate(2, Date.valueOf(d2));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalRegle()", e);
        }
    }

    @Override
    public Double calculateTotalNonRegle(LocalDateTime start, LocalDateTime end) {
        LocalDate d1 = start.toLocalDate();
        LocalDate d2 = end.toLocalDate();

        String sql = """
            SELECT COALESCE(SUM(reste),0) AS total
            FROM facture
            WHERE date_facture BETWEEN ? AND ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(d1));
            ps.setDate(2, Date.valueOf(d2));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur calculateTotalNonRegle()", e);
        }
    }
}
