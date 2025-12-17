package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM intervention_medecin ORDER BY id DESC";
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: InterventionMedecin.findAll()", e);
        }

        return out;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM intervention_medecin WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapInterventionMedecin(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: InterventionMedecin.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(InterventionMedecin i) {
        String sql = """
            INSERT INTO intervention_medecin
            (consultation_id, acte_id, prix_patient, num_dent, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        if (i == null) throw new IllegalArgumentException("InterventionMedecin null dans create()");
        if (i.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire (NOT NULL)");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, i.getConsultationId());

            if (i.getActeId() != null) ps.setLong(2, i.getActeId());
            else ps.setNull(2, Types.BIGINT);

            ps.setBigDecimal(3, i.getPrixDePatient() != null
                    ? java.math.BigDecimal.valueOf(i.getPrixDePatient())
                    : java.math.BigDecimal.ZERO);

            if (i.getNumDent() != null) ps.setInt(4, i.getNumDent());
            else ps.setNull(4, Types.INTEGER);

            ps.setString(5, i.getCreePar());
            ps.setString(6, i.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) i.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: InterventionMedecin.create()", e);
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
                   modifie_par = ?
             WHERE id = ?
            """;

        if (i == null) throw new IllegalArgumentException("InterventionMedecin null dans update()");
        if (i.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (i.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire (NOT NULL)");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, i.getConsultationId());

            if (i.getActeId() != null) ps.setLong(2, i.getActeId());
            else ps.setNull(2, Types.BIGINT);

            ps.setBigDecimal(3, i.getPrixDePatient() != null
                    ? java.math.BigDecimal.valueOf(i.getPrixDePatient())
                    : java.math.BigDecimal.ZERO);

            if (i.getNumDent() != null) ps.setInt(4, i.getNumDent());
            else ps.setNull(4, Types.INTEGER);

            ps.setString(5, i.getModifiePar());
            ps.setLong(6, i.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: InterventionMedecin.update(id=" + i.getId() + ")", e);
        }
    }

    @Override
    public void delete(InterventionMedecin i) {
        if (i != null && i.getId() != null) deleteById(i.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM intervention_medecin WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: InterventionMedecin.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Méthodes existantes
    // ------------------------------------------------------------
    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM intervention_medecin WHERE consultation_id = ? ORDER BY id ASC";
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByConsultationId(" + consultationId + ")", e);
        }

        return out;
    }

    @Override
    public void deleteByConsultationId(Long consultationId) {
        String sql = "DELETE FROM intervention_medecin WHERE consultation_id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: deleteByConsultationId(" + consultationId + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Nouvelles méthodes (optimisation / manipulation)
    // ------------------------------------------------------------
    @Override
    public List<InterventionMedecin> findByActeId(Long acteId) {
        String sql = "SELECT * FROM intervention_medecin WHERE acte_id = ? ORDER BY id DESC";
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, acteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByActeId(" + acteId + ")", e);
        }

        return out;
    }

    @Override
    public List<InterventionMedecin> findByDossierId(Long dossierId) {
        String sql = """
            SELECT im.*
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
             WHERE c.dossier_id = ?
             ORDER BY c.date_consultation DESC, im.id DESC
            """;
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByDossierId(" + dossierId + ")", e);
        }

        return out;
    }

    @Override
    public List<InterventionMedecin> findByPatientId(Long patientId) {
        String sql = """
            SELECT im.*
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.patient_id = ?
             ORDER BY c.date_consultation DESC, im.id DESC
            """;
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByPatientId(" + patientId + ")", e);
        }

        return out;
    }

    @Override
    public List<InterventionMedecin> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT im.*
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
             WHERE c.date_consultation BETWEEN ? AND ?
             ORDER BY c.date_consultation DESC, im.id DESC
            """;
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByDateBetween(" + start + "," + end + ")", e);
        }

        return out;
    }

    @Override
    public List<InterventionMedecin> findByActeIdAndDateBetween(Long acteId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT im.*
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
             WHERE im.acte_id = ?
               AND c.date_consultation BETWEEN ? AND ?
             ORDER BY c.date_consultation DESC, im.id DESC
            """;
        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, acteId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByActeIdAndDateBetween(acteId=" + acteId + ")", e);
        }

        return out;
    }

    @Override
    public Integer countPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT COUNT(im.id) AS total
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.medecin_id = ?
               AND c.date_consultation BETWEEN ? AND ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: countPourMedecinEtDate(medecinId=" + medecinId + ")", e);
        }
    }

    @Override
    public Double sumMontantPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT COALESCE(SUM(im.prix_patient), 0) AS total
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.medecin_id = ?
               AND c.date_consultation BETWEEN ? AND ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: sumMontantPourMedecinEtDate(medecinId=" + medecinId + ")", e);
        }
    }

    @Override
    public Double sumMontantPourConsultation(Long consultationId) {
        String sql = """
            SELECT COALESCE(SUM(prix_patient), 0) AS total
              FROM intervention_medecin
             WHERE consultation_id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: sumMontantPourConsultation(" + consultationId + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Utilitaires classiques
    // ------------------------------------------------------------
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM intervention_medecin WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM intervention_medecin";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: count()", e);
        }
    }

    @Override
    public List<InterventionMedecin> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM intervention_medecin
             ORDER BY id DESC
             LIMIT ? OFFSET ?
            """;

        List<InterventionMedecin> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }

        return out;
    }
}
