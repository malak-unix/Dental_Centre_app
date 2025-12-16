package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.interventionMedecin.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InterventionMedecinImpl implements InterventionMedecinRepository {

    // =========================================================================
    // Mapping ResultSet -> InterventionMedecin
    // =========================================================================
    private InterventionMedecin map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long consultationId = rs.getLong("consultation_id");
        if (rs.wasNull()) consultationId = null; // en théorie NOT NULL, mais on sécurise

        Long acteId = rs.getLong("acte_id");
        if (rs.wasNull()) acteId = null;

        BigDecimal prixBD = rs.getBigDecimal("prix_patient");
        Double prixPatient = (prixBD != null) ? prixBD.doubleValue() : null;

        Integer numDent = rs.getInt("num_dent");
        if (rs.wasNull()) numDent = null;

        Timestamp tCreate = rs.getTimestamp("date_creation");
        LocalDateTime dateCreation = (tCreate != null) ? tCreate.toLocalDateTime() : null;

        Timestamp tModif = rs.getTimestamp("date_modification");
        LocalDateTime dateModif = (tModif != null) ? tModif.toLocalDateTime() : null;

        return InterventionMedecin.builder()
                .id(id)
                .consultationId(consultationId)
                .acteId(acteId)
                .prixDePatient(prixPatient)
                .numDent(numDent)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =========================================================================
    // CRUD (CrudRepository)
    // =========================================================================

    @Override
    public void create(InterventionMedecin i) {
        String sql = """
                INSERT INTO intervention_medecin
                (consultation_id, acte_id, prix_patient, num_dent,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // consultation_id (NOT NULL en base)
            if (i.getConsultationId() == null) {
                throw new IllegalArgumentException("consultationId ne doit pas être null pour InterventionMedecin");
            }
            ps.setLong(1, i.getConsultationId());

            // acte_id (nullable)
            if (i.getActeId() != null) {
                ps.setLong(2, i.getActeId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            // prix_patient
            if (i.getPrixDePatient() != null) {
                ps.setBigDecimal(3, BigDecimal.valueOf(i.getPrixDePatient()));
            } else {
                ps.setBigDecimal(3, BigDecimal.ZERO);
            }

            // num_dent
            if (i.getNumDent() != null) {
                ps.setInt(4, i.getNumDent());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            // date_creation
            LocalDateTime dc = (i.getDateCreation() != null) ? i.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dc));

            // cree_par / modifie_par
            ps.setString(6, i.getCreePar());
            ps.setString(7, i.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    i.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public void update(InterventionMedecin i) {
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

            if (i.getConsultationId() == null) {
                throw new IllegalArgumentException("consultationId ne doit pas être null pour InterventionMedecin");
            }
            ps.setLong(1, i.getConsultationId());

            if (i.getActeId() != null) {
                ps.setLong(2, i.getActeId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            if (i.getPrixDePatient() != null) {
                ps.setBigDecimal(3, BigDecimal.valueOf(i.getPrixDePatient()));
            } else {
                ps.setBigDecimal(3, BigDecimal.ZERO);
            }

            if (i.getNumDent() != null) {
                ps.setInt(4, i.getNumDent());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            LocalDateTime dm = (i.getDateDerniereModification() != null)
                    ? i.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));

            ps.setString(6, i.getModifiePar());
            ps.setLong(7, i.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM intervention_medecin WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return null;
    }

    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM intervention_medecin ORDER BY id";
        List<InterventionMedecin> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }

    @Override
    public void delete(InterventionMedecin i) {
        if (i != null && i.getId() != null) {
            deleteById(i.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM intervention_medecin WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    // =========================================================================
    // Méthodes spécifiques (InterventionMedecinRepository)
    // =========================================================================

    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM intervention_medecin WHERE consultation_id = ? ORDER BY id";
        List<InterventionMedecin> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }

    @Override
    public void deleteByConsultationId(Long consultationId) {
        String sql = "DELETE FROM intervention_medecin WHERE consultation_id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }
}
