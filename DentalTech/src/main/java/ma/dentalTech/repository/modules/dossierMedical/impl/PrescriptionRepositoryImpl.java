package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public void create(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null dans create()");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire (create)");

        String sql = """
            INSERT INTO prescription
            (ordonnance_id, medicament_id, quantite, frequence, duree_en_jours,
             date_creation, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, p.getOrdonnanceId());

            if (p.getMedicamentId() != null) ps.setLong(2, p.getMedicamentId());
            else ps.setNull(2, Types.BIGINT);

            int qte = (p.getQuantite() <= 0) ? 1 : p.getQuantite();
            ps.setInt(3, qte);
            p.setQuantite(qte);

            ps.setString(4, p.getFrequence());

            int duree = Math.max(0, p.getDureeEnJours());
            ps.setInt(5, duree);
            p.setDureeEnJours(duree);

            LocalDateTime dc = (p.getDateCreation() != null) ? p.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dc));
            p.setDateCreation(dc);

            ps.setString(7, p.getCreePar());
            ps.setString(8, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.create()", e);
        }
    }

    @Override
    public void update(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null dans update()");
        if (p.getId() == null) throw new IllegalArgumentException("id obligatoire (update)");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire (update)");

        String sql = """
            UPDATE prescription
               SET ordonnance_id = ?,
                   medicament_id = ?,
                   quantite = ?,
                   frequence = ?,
                   duree_en_jours = ?,
                   date_modification = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, p.getOrdonnanceId());

            if (p.getMedicamentId() != null) ps.setLong(2, p.getMedicamentId());
            else ps.setNull(2, Types.BIGINT);

            int qte = (p.getQuantite() <= 0) ? 1 : p.getQuantite();
            ps.setInt(3, qte);
            p.setQuantite(qte);

            ps.setString(4, p.getFrequence());

            int duree = Math.max(0, p.getDureeEnJours());
            ps.setInt(5, duree);
            p.setDureeEnJours(duree);

            LocalDateTime dm = (p.getDateDerniereModification() != null)
                    ? p.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dm));
            p.setDateDerniereModification(dm);

            ps.setString(7, p.getModifiePar());
            ps.setLong(8, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.update(id=" + p.getId() + ")", e);
        }
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPrescription(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.findById(" + id + ")", e);
        }
    }

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM prescription ORDER BY id DESC";
        List<Prescription> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapPrescription(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.findAll()", e);
        }

        return out;
    }

    @Override
    public void delete(Prescription p) {
        if (p != null && p.getId() != null) deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Méthodes spécifiques
    // ------------------------------------------------------------
    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT * FROM prescription WHERE ordonnance_id = ? ORDER BY id ASC";
        List<Prescription> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.findByOrdonnanceId(" + ordonnanceId + ")", e);
        }

        return out;
    }

    @Override
    public void deleteByOrdonnanceId(Long ordonnanceId) {
        String sql = "DELETE FROM prescription WHERE ordonnance_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.deleteByOrdonnanceId(" + ordonnanceId + ")", e);
        }
    }

    // ----------------- Extras (si tu les ajoutes à l'interface) -----------------

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        String sql = "SELECT * FROM prescription WHERE medicament_id = ? ORDER BY id DESC";
        List<Prescription> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medicamentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.findByMedicamentId(" + medicamentId + ")", e);
        }

        return out;
    }

    @Override
    public long countByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT COUNT(*) FROM prescription WHERE ordonnance_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.countByOrdonnanceId(" + ordonnanceId + ")", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Prescription.existsById(" + id + ")", e);
        }
    }
}
