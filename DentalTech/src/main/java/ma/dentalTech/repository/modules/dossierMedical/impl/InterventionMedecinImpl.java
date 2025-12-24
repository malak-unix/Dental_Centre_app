package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM intervention_medecin ORDER BY id";
        List<InterventionMedecin> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapInterventionMedecin(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() InterventionMedecin", e);
        }
    }

    @Override
    public InterventionMedecin findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM intervention_medecin WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapInterventionMedecin(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() InterventionMedecin, id=" + id, e);
        }
    }

    @Override
    public void create(InterventionMedecin i) {
        if (i == null) throw new IllegalArgumentException("InterventionMedecin null");
        if (i.getConsultationId() == null)
            throw new IllegalArgumentException("consultationId obligatoire (NOT NULL)");

        String sql = """
            INSERT INTO intervention_medecin
            (consultation_id, acte_id, prix_patient, num_dent, date_creation, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, i.getConsultationId());

            if (i.getActeId() != null) ps.setLong(2, i.getActeId());
            else ps.setNull(2, Types.BIGINT);

            // prix_patient (DECIMAL) -> BigDecimal
            BigDecimal prix = (i.getPrixDePatient() != null) ? BigDecimal.valueOf(i.getPrixDePatient()) : BigDecimal.ZERO;
            ps.setBigDecimal(3, prix);

            if (i.getNumDent() != null) ps.setInt(4, i.getNumDent());
            else ps.setNull(4, Types.INTEGER);

            LocalDateTime dc = (i.getDateCreation() != null) ? i.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dc));

            ps.setString(6, i.getCreePar());
            ps.setString(7, i.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) i.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() InterventionMedecin", e);
        }
    }

    @Override
    public void update(InterventionMedecin i) {
        if (i == null) throw new IllegalArgumentException("InterventionMedecin null");
        if (i.getId() == null) throw new IllegalArgumentException("id obligatoire");
        if (i.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire (NOT NULL)");

        String sql = """
            UPDATE intervention_medecin
               SET consultation_id = ?,
                   acte_id = ?,
                   prix_patient = ?,
                   num_dent = ?,
                   date_modification = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, i.getConsultationId());

            if (i.getActeId() != null) ps.setLong(2, i.getActeId());
            else ps.setNull(2, Types.BIGINT);

            BigDecimal prix = (i.getPrixDePatient() != null) ? BigDecimal.valueOf(i.getPrixDePatient()) : BigDecimal.ZERO;
            ps.setBigDecimal(3, prix);

            if (i.getNumDent() != null) ps.setInt(4, i.getNumDent());
            else ps.setNull(4, Types.INTEGER);

            LocalDateTime dm = (i.getDateDerniereModification() != null)
                    ? i.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));

            ps.setString(6, i.getModifiePar());
            ps.setLong(7, i.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() InterventionMedecin, id=" + i.getId(), e);
        }
    }

    @Override
    public void delete(InterventionMedecin i) {
        if (i == null || i.getId() == null) return;
        deleteById(i.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM intervention_medecin WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() InterventionMedecin, id=" + id, e);
        }
    }

    // =========================================================================
    // Méthodes spécifiques (InterventionMedecinRepository)
    // =========================================================================

    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();

        String sql = "SELECT * FROM intervention_medecin WHERE consultation_id = ? ORDER BY id";
        List<InterventionMedecin> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapInterventionMedecin(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByConsultationId() InterventionMedecin, consultationId=" + consultationId, e);
        }
    }

    @Override
    public void deleteByConsultationId(Long consultationId) {
        if (consultationId == null) return;

        String sql = "DELETE FROM intervention_medecin WHERE consultation_id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteByConsultationId() InterventionMedecin, consultationId=" + consultationId, e);
        }
    }
}
