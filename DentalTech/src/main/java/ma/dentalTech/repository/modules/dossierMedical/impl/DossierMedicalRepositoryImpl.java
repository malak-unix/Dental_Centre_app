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

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findAll()", e);
        }

        return out;
    }

    @Override
    public DossierMedical findById(Long id) {
        String sql = "SELECT * FROM dossier_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapDossierMedical(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(DossierMedical d) {
        String sql = """
            INSERT INTO dossier_medical (patient_id, medecin_id, notes, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?)
            """;

        if (d == null) throw new IllegalArgumentException("DossierMedical null dans create()");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire (NOT NULL) dans dossier_medical");

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
        String sql = """
            UPDATE dossier_medical
               SET patient_id = ?,
                   medecin_id = ?,
                   notes = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        if (d == null) throw new IllegalArgumentException("DossierMedical null dans update()");
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (d.getPatientId() == null) throw new IllegalArgumentException("patientId obligatoire dans update()");

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
    // Extras
    // ------------------------------------------------------------
    @Override
    public Optional<DossierMedical> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM dossier_medical WHERE patient_id = ? ORDER BY id DESC LIMIT 1";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapDossierMedical(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findByPatientId(" + patientId + ")", e);
        }
    }

    @Override
    public List<DossierMedical> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM dossier_medical WHERE medecin_id = ? ORDER BY id DESC";
        List<DossierMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findByMedecinId(" + medecinId + ")", e);
        }

        return out;
    }

    @Override
    public List<DossierMedical> searchByNotes(String keyword) {
        String sql = """
            SELECT * FROM dossier_medical
             WHERE notes LIKE ?
             ORDER BY id DESC
            """;
        List<DossierMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String like = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.searchByNotes(" + keyword + ")", e);
        }

        return out;
    }

    @Override
    public boolean existsById(Long id) {
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
        String sql = "SELECT COUNT(*) FROM dossier_medical";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

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

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }

        return out;
    }

    // ------------------------------------------------------------
    // Dashboard - Aya
    // ------------------------------------------------------------
    @Override
    public Integer countActifs() {
        /*
         * Définition "actif" (proposée, logique dashboard) :
         * Dossier qui a AU MOINS une consultation PLANIFIE (à venir / en cours).
         *
         * Si vous préférez "actif = a au moins une consultation tout court",
         * je te donne la requête alternative juste après.
         */
        String sql = """
            SELECT COUNT(DISTINCT d.id) AS total
              FROM dossier_medical d
              JOIN consultation c ON c.dossier_id = d.id
             WHERE c.statut = 'PLANIFIE'
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("total");
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DossierMedical.countActifs()", e);
        }
    }

    // Alternative si vous voulez "actif = dossier qui a au moins 1 consultation (n'importe quel statut)"
    // SQL:
    // SELECT COUNT(DISTINCT d.id) AS total
    //   FROM dossier_medical d
    //   JOIN consultation c ON c.dossier_id = d.id;
}
