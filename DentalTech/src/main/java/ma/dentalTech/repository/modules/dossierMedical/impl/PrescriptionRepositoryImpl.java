package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    // =========================================================================
    // Mapping ResultSet -> Prescription
    // =========================================================================
    private Prescription map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long ordonnanceId = rs.getLong("ordonnance_id");
        if (rs.wasNull()) ordonnanceId = null;

        Long medicamentId = rs.getLong("medicament_id");
        if (rs.wasNull()) medicamentId = null;

        Timestamp tCreate = rs.getTimestamp("date_creation");
        LocalDateTime dateCreation = (tCreate != null) ? tCreate.toLocalDateTime() : null;

        Timestamp tModif = rs.getTimestamp("date_modification");
        LocalDateTime dateModif = (tModif != null) ? tModif.toLocalDateTime() : null;

        return Prescription.builder()
                .id(id)
                .ordonnanceId(ordonnanceId)
                .medicamentId(medicamentId)
                .quantite(rs.getInt("quantite"))
                .frequence(rs.getString("frequence"))
                .dureeEnJours(rs.getInt("duree_en_jours"))
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =========================================================================
    // CRUD de base
    // =========================================================================

    @Override
    public void create(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null dans create()");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire (NOT NULL)");

        String sql = """
        INSERT INTO prescription
        (ordonnance_id, medicament_id, quantite, frequence, duree_en_jours, cree_par, modifie_par)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, p.getOrdonnanceId());

            if (p.getMedicamentId() != null) ps.setLong(2, p.getMedicamentId());
            else ps.setNull(2, Types.BIGINT);

            int q = p.getQuantite() <= 0 ? 1 : p.getQuantite();
            ps.setInt(3, q);

            ps.setString(4, p.getFrequence());

            int d = p.getDureeEnJours() < 0 ? 0 : p.getDureeEnJours();
            ps.setInt(5, d);

            ps.setString(6, p.getCreePar());
            ps.setString(7, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Prescription", e);
        }
    }


    @Override
    public void update(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription null dans update()");
        if (p.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (p.getOrdonnanceId() == null) throw new IllegalArgumentException("ordonnanceId obligatoire dans update()");

        String sql = """
        UPDATE prescription
           SET ordonnance_id = ?,
               medicament_id = ?,
               quantite = ?,
               frequence = ?,
               duree_en_jours = ?,
               modifie_par = ?,
               date_modification = CURRENT_TIMESTAMP
         WHERE id = ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, p.getOrdonnanceId());

            if (p.getMedicamentId() != null) ps.setLong(2, p.getMedicamentId());
            else ps.setNull(2, Types.BIGINT);

            int q = p.getQuantite() <= 0 ? 1 : p.getQuantite();
            ps.setInt(3, q);

            ps.setString(4, p.getFrequence());

            int d = p.getDureeEnJours() < 0 ? 0 : p.getDureeEnJours();
            ps.setInt(5, d);

            ps.setString(6, p.getModifiePar());
            ps.setLong(7, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Prescription, id=" + p.getId(), e);
        }
    }


    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return null;
    }

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM prescription ORDER BY id";
        List<Prescription> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public void delete(Prescription p) {
        if (p != null && p.getId() != null) {
            deleteById(p.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM prescription WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    // =========================================================================
    // Méthodes spécifiques
    // =========================================================================

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT * FROM prescription WHERE ordonnance_id = ? ORDER BY id";
        List<Prescription> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return list;
    }

    @Override
    public void deleteByOrdonnanceId(Long ordonnanceId) {
        String sql = "DELETE FROM prescription WHERE ordonnance_id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);
            ps.executeUpdate();

        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }
    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM prescription WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsById() Prescription, id=" + id, e);
        }
    }

    @Override
    public long countByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) return 0;

        String sql = "SELECT COUNT(*) AS total FROM prescription WHERE ordonnance_id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("total") : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countByOrdonnanceId() Prescription, ordonnanceId=" + ordonnanceId, e);
        }
    }

}
