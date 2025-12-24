package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<DossierMedical> findAll() {
        String sql = "SELECT * FROM dossier_medical ORDER BY id DESC";
        List<DossierMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findAll()", e);
        }
    }

    @Override
    public DossierMedical findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM dossier_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapDossierMedical(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(DossierMedical d) {
        if (d == null) throw new IllegalArgumentException("DossierMedical null dans create()");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire (NOT NULL) dans dossier_medical");

        String sql = """
            INSERT INTO dossier_medical (patient_id, medecin_id, notes, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, d.getPatientId());

            if (d.getMedecinId() != null) ps.setLong(2, d.getMedecinId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getNotes());
            ps.setString(4, d.getCreePar());
            ps.setString(5, d.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.create()", e);
        }
    }

    @Override
    public void update(DossierMedical d) {
        if (d == null) throw new IllegalArgumentException("DossierMedical null dans update()");
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire dans update()");

        String sql = """
            UPDATE dossier_medical
               SET patient_id = ?,
                   medecin_id = ?,
                   notes = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, d.getPatientId());

            if (d.getMedecinId() != null) ps.setLong(2, d.getMedecinId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getNotes());
            ps.setString(4, d.getModifiePar());
            ps.setLong(5, d.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.update(id=" + d.getId() + ")", e);
        }
    }

    @Override
    public void delete(DossierMedical d) {
        if (d != null && d.getId() != null) deleteById(d.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM dossier_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Extras (interface)
    // ------------------------------------------------------------
    @Override
    public Optional<DossierMedical> findByPatientId(Long patientId) {
        if (patientId == null) return Optional.empty();

        String sql = "SELECT * FROM dossier_medical WHERE patient_id = ? ORDER BY id DESC LIMIT 1";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(RowMappers.mapDossierMedical(rs))
                        : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findByPatientId(" + patientId + ")", e);
        }
    }

    @Override
    public List<DossierMedical> findByMedecinId(Long medecinId) {
        if (medecinId == null) return List.of();

        String sql = "SELECT * FROM dossier_medical WHERE medecin_id = ? ORDER BY id DESC";
        List<DossierMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findByMedecinId(" + medecinId + ")", e);
        }
    }

    @Override
    public List<DossierMedical> searchByNotes(String keyword) {
        String sql = """
            SELECT * FROM dossier_medical
             WHERE notes LIKE ?
             ORDER BY id DESC
            """;
        List<DossierMedical> out = new ArrayList<>();
        String like = "%" + (keyword == null ? "" : keyword) + "%";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.searchByNotes(" + keyword + ")", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM dossier_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM dossier_medical";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong("total") : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.count()", e);
        }
    }

    @Override
    public List<DossierMedical> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM dossier_medical
             ORDER BY id DESC
             LIMIT ? OFFSET ?
            """;
        List<DossierMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Dashboard - Aya
    // ------------------------------------------------------------
    @Override
    public Integer countActifs() {
        // Actif = dossier ayant au moins une consultation PLANIFIE
        String sql = """
            SELECT COUNT(DISTINCT d.id) AS total
              FROM dossier_medical d
              JOIN consultation c ON c.dossier_id = d.id
             WHERE c.statut = 'PLANIFIE'
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("total") : 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.countActifs()", e);
        }
    }
}
