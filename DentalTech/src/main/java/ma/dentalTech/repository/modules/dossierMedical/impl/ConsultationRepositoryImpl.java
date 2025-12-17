package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------
    private static Timestamp toTs(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    /** Ton entity stocke LocalDate, mais la DB a DATETIME → on met midi par défaut */
    private static LocalDateTime toDateTime(LocalDate d) {
        return d == null ? null : d.atTime(12, 0);
    }

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM consultation ORDER BY date_consultation DESC, id DESC";
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapConsultation(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findAll()", e);
        }
        return out;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM consultation WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapConsultation(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(Consultation co) {
        String sql = """
            INSERT INTO consultation
            (dossier_id, date_consultation, statut, observation_medecin, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        if (co == null) throw new IllegalArgumentException("Consultation null dans create()");
        if (co.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire (NOT NULL) dans consultation");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, co.getDossierId());

            LocalDateTime dt = toDateTime(co.getDate());
            if (dt == null) dt = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dt));

            StatutConsultation st = (co.getStatus() != null) ? co.getStatus() : StatutConsultation.PLANIFIE;
            ps.setString(3, st.name());

            ps.setString(4, co.getObservationMedecin());
            ps.setString(5, co.getCreePar());
            ps.setString(6, co.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) co.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.create()", e);
        }
    }

    @Override
    public void update(Consultation co) {
        String sql = """
            UPDATE consultation
               SET dossier_id = ?,
                   date_consultation = ?,
                   statut = ?,
                   observation_medecin = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        if (co == null) throw new IllegalArgumentException("Consultation null dans update()");
        if (co.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (co.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire dans update()");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, co.getDossierId());

            LocalDateTime dt = toDateTime(co.getDate());
            if (dt == null) dt = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dt));

            if (co.getStatus() != null) ps.setString(3, co.getStatus().name());
            else ps.setString(3, StatutConsultation.PLANIFIE.name());

            ps.setString(4, co.getObservationMedecin());
            ps.setString(5, co.getModifiePar());
            ps.setLong(6, co.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.update(id=" + co.getId() + ")", e);
        }
    }

    @Override
    public void delete(Consultation co) {
        if (co != null && co.getId() != null) deleteById(co.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM consultation WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Extras
    // ------------------------------------------------------------
    @Override
    public List<Consultation> findByDossierId(Long dossierId) {
        String sql = """
            SELECT * FROM consultation
             WHERE dossier_id = ?
             ORDER BY date_consultation DESC, id DESC
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findByDossierId(" + dossierId + ")", e);
        }
        return out;
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        // version simple (comme ton code)
        String sql = """
            SELECT * FROM consultation
             WHERE DATE(date_consultation) = ?
             ORDER BY date_consultation DESC, id DESC
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findByDate(" + date + ")", e);
        }
        return out;
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
            SELECT * FROM consultation
             WHERE DATE(date_consultation) BETWEEN ? AND ?
             ORDER BY date_consultation DESC, id DESC
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findByDateBetween(" + start + "," + end + ")", e);
        }
        return out;
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        String sql = """
            SELECT * FROM consultation
             WHERE statut = ?
             ORDER BY date_consultation DESC, id DESC
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findByStatut(" + statut + ")", e);
        }
        return out;
    }

    @Override
    public List<Consultation> searchByObservation(String keyword) {
        String sql = """
            SELECT * FROM consultation
             WHERE observation_medecin LIKE ?
             ORDER BY date_consultation DESC, id DESC
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + (keyword == null ? "" : keyword) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.searchByObservation(" + keyword + ")", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM consultation WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM consultation";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.count()", e);
        }
    }

    @Override
    public List<Consultation> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM consultation
             ORDER BY date_consultation DESC, id DESC
             LIMIT ? OFFSET ?
            """;
        List<Consultation> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }
        return out;
    }

    // ------------------------------------------------------------
    // Dashboard (Aya) - vraies requêtes SQL
    // ------------------------------------------------------------
    @Override
    public Integer countTermineesPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
        String sql = """
            SELECT COUNT(*) AS total
              FROM consultation c
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.medecin_id = ?
               AND c.statut = 'TERMINE'
               AND c.date_consultation BETWEEN ? AND ?
            """;

        if (medecinId == null || start == null || end == null) return 0;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, toTs(start));
            ps.setTimestamp(3, toTs(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: countTermineesPourMedecin(medecinId=" + medecinId + ")", e);
        }
    }

    @Override
    public Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
        // On interprète "EnCours" = PLANIFIE (à venir / en cours de traitement)
        String sql = """
            SELECT COUNT(*) AS total
              FROM consultation c
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.medecin_id = ?
               AND c.statut = 'PLANIFIE'
               AND c.date_consultation BETWEEN ? AND ?
            """;

        if (medecinId == null || start == null || end == null) return 0;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, toTs(start));
            ps.setTimestamp(3, toTs(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: countEnCoursPourMedecin(medecinId=" + medecinId + ")", e);
        }
    }
}
