package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM medicament ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapMedicament(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findAll()", e);
        }

        return out;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedicament(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null dans create()");
        if (m.getNom() == null || m.getNom().isBlank())
            throw new IllegalArgumentException("nom obligatoire pour Medicament");

        String sql = """
            INSERT INTO medicament
            (nom, laboratoire, type_medicament, forme, remboursable,
             prix_unitaire, description, date_creation, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());

            if (m.getForme() != null) ps.setString(4, m.getForme().name());
            else ps.setNull(4, Types.VARCHAR);

            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) ps.setBigDecimal(6, java.math.BigDecimal.valueOf(m.getPrixUnitaire()));
            else ps.setNull(6, Types.DECIMAL);

            ps.setString(7, m.getDescription());

            LocalDateTime dc = (m.getDateCreation() != null) ? m.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dc));
            m.setDateCreation(dc);

            ps.setString(9, m.getCreePar());
            ps.setString(10, m.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.create()", e);
        }
    }

    @Override
    public void update(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null dans update()");
        if (m.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");

        String sql = """
            UPDATE medicament
               SET nom = ?,
                   laboratoire = ?,
                   type_medicament = ?,
                   forme = ?,
                   remboursable = ?,
                   prix_unitaire = ?,
                   description = ?,
                   date_modification = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());

            if (m.getForme() != null) ps.setString(4, m.getForme().name());
            else ps.setNull(4, Types.VARCHAR);

            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) ps.setBigDecimal(6, java.math.BigDecimal.valueOf(m.getPrixUnitaire()));
            else ps.setNull(6, Types.DECIMAL);

            ps.setString(7, m.getDescription());

            LocalDateTime dm = (m.getDateDerniereModification() != null)
                    ? m.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dm));
            m.setDateDerniereModification(dm);

            ps.setString(9, m.getModifiePar());
            ps.setLong(10, m.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.update(id=" + m.getId() + ")", e);
        }
    }

    @Override
    public void delete(Medicament m) {
        if (m != null && m.getId() != null) deleteById(m.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Méthodes spécifiques
    // ------------------------------------------------------------
    @Override
    public List<Medicament> searchByNom(String keyword) {
        String sql = "SELECT * FROM medicament WHERE nom LIKE ? ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.searchByNom(" + keyword + ")", e);
        }

        return out;
    }

    @Override
    public Optional<Medicament> findByNomExact(String nom) {
        String sql = "SELECT * FROM medicament WHERE nom = ? LIMIT 1";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapMedicament(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByNomExact(" + nom + ")", e);
        }
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        String sql = "SELECT * FROM medicament WHERE remboursable = ? ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, remboursable);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByRemboursable(" + remboursable + ")", e);
        }

        return out;
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        String sql = "SELECT * FROM medicament WHERE laboratoire = ? ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, laboratoire);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByLaboratoire(" + laboratoire + ")", e);
        }

        return out;
    }

    @Override
    public List<Medicament> findByType(String typeMedicament) {
        String sql = "SELECT * FROM medicament WHERE type_medicament = ? ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, typeMedicament);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByType(" + typeMedicament + ")", e);
        }

        return out;
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament forme) {
        String sql = "SELECT * FROM medicament WHERE forme = ? ORDER BY nom ASC, id ASC";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, forme.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByForme(" + forme + ")", e);
        }

        return out;
    }

    @Override
    public List<Medicament> findByPrixBetween(Double min, Double max) {
        // null-safe (si un des 2 est null)
        Double lo = (min == null) ? 0.0 : min;
        Double hi = (max == null) ? Double.MAX_VALUE : max;

        String sql = """
            SELECT * FROM medicament
             WHERE prix_unitaire BETWEEN ? AND ?
             ORDER BY prix_unitaire ASC, nom ASC
            """;

        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(lo));
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(hi));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findByPrixBetween(" + min + "," + max + ")", e);
        }

        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM medicament";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.count()", e);
        }
    }

    @Override
    public List<Medicament> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM medicament
             ORDER BY nom ASC, id ASC
             LIMIT ? OFFSET ?
            """;
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Medicament.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }

        return out;
    }
}
