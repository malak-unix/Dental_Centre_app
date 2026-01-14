package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    private static Timestamp toTs(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    /**
     * Ton entity Consultation utilise LocalDate (pas LocalDateTime).
     * Donc on convertit LocalDate -> LocalDateTime (midi) pour remplir le DATETIME en DB.
     */
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
        if (id == null) return null;

        String sql = "SELECT * FROM consultation WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapConsultation(rs) : null;
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

            LocalDateTime dt = co.getDate();
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

            LocalDateTime dt = co.getDate();
            if (dt == null) dt = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dt));

            StatutConsultation st = (co.getStatus() != null) ? co.getStatus() : StatutConsultation.PLANIFIE;
            ps.setString(3, st.name());

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
        if (id == null) return;

        String sql = "DELETE FROM consultation WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.deleteById(" + id + ")", e);
        }
    }

    @Override
    public List<Consultation> findByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();

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
        if (date == null) return List.of();

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
        if (start == null || end == null) return List.of();

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
        if (statut == null) return List.of();

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
        if (id == null) return false;

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
        if (limit <= 0) limit = 10;
        if (offset < 0) offset = 0;

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
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            int idx = i + 1;

            if (v instanceof Timestamp) ps.setTimestamp(idx, (Timestamp) v);
            else if (v instanceof Date) ps.setDate(idx, (Date) v);
            else ps.setObject(idx, v);
        }
    }

    @Override
    public List<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO req) {

        if (req == null) throw new IllegalArgumentException("ConsultationListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes consultations'");

        StringBuilder sql = new StringBuilder("""
        SELECT
          c.id AS consultation_id,
          c.dossier_id AS dossier_id,
          c.date_consultation AS date_consultation,
          c.statut AS statut,

          p.id AS patient_id,
          CONCAT(p.nom, ' ', p.prenom) AS patient_nom_complet,

          f.id AS facture_id,
          f.total_facture AS total_facture
        FROM consultation c
        JOIN dossier_medical d ON d.id = c.dossier_id
        JOIN patient p ON p.id = d.patient_id
        LEFT JOIN facture f ON f.consultation_id = c.id
        WHERE d.medecin_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(req.getMedecinId());

        // Filtre statut
        if (req.getStatut() != null) {
            sql.append(" AND c.statut = ? ");
            params.add(req.getStatut().name());
        }

        // Filtre patientKeyword (nom / prénom / nom complet)
        if (!isBlank(req.getPatientKeyword())) {
            sql.append("""
            AND (
                 LOWER(p.nom) LIKE ?
              OR LOWER(p.prenom) LIKE ?
              OR LOWER(CONCAT(p.nom,' ',p.prenom)) LIKE ?
            )
        """);
            String kw = "%" + req.getPatientKeyword().trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        // Filtre date exacte (journée entière)
        if (req.getDate() != null) {
            LocalDate dte = req.getDate();
            LocalDateTime start = dte.atStartOfDay();
            LocalDateTime end = dte.plusDays(1).atStartOfDay();

            sql.append(" AND c.date_consultation >= ? AND c.date_consultation < ? ");
            params.add(Timestamp.valueOf(start));
            params.add(Timestamp.valueOf(end));
        }

        sql.append(" ORDER BY c.date_consultation DESC, c.id DESC ");

        List<ConsultationListItemDTO> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConsultationListItemDTO dto = new ConsultationListItemDTO();

                    dto.setConsultationId(rs.getLong("consultation_id"));
                    dto.setDossierId(rs.getLong("dossier_id"));

                    Timestamp ts = rs.getTimestamp("date_consultation");
                    dto.setDateConsultation(ts != null ? ts.toLocalDateTime() : null);

                    dto.setStatut(StatutConsultation.valueOf(rs.getString("statut")));

                    dto.setPatientId(rs.getLong("patient_id"));
                    dto.setPatientNomComplet(rs.getString("patient_nom_complet"));

                    long fid = rs.getLong("facture_id");
                    dto.setFactureId(rs.wasNull() ? null : fid);

                    BigDecimal total = rs.getBigDecimal("total_facture");
                    dto.setTotalFacture(total);

                    out.add(dto);
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.searchForList()", e);
        }
    }

    @Override
    public long countForList(ConsultationListRequestDTO req) {

        if (req == null) throw new IllegalArgumentException("ConsultationListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes consultations'");

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM consultation c
        JOIN dossier_medical d ON d.id = c.dossier_id
        JOIN patient p ON p.id = d.patient_id
        WHERE d.medecin_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(req.getMedecinId());

        if (req.getStatut() != null) {
            sql.append(" AND c.statut = ? ");
            params.add(req.getStatut().name());
        }

        if (!isBlank(req.getPatientKeyword())) {
            sql.append("""
            AND (
                 LOWER(p.nom) LIKE ?
              OR LOWER(p.prenom) LIKE ?
              OR LOWER(CONCAT(p.nom,' ',p.prenom)) LIKE ?
            )
        """);
            String kw = "%" + req.getPatientKeyword().trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (req.getDate() != null) {
            LocalDate dte = req.getDate();
            LocalDateTime start = dte.atStartOfDay();
            LocalDateTime end = dte.plusDays(1).atStartOfDay();

            sql.append(" AND c.date_consultation >= ? AND c.date_consultation < ? ");
            params.add(Timestamp.valueOf(start));
            params.add(Timestamp.valueOf(end));
        }

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Consultation.countForList()", e);
        }
    }


    // ------------------------------------------------------------
    // Dashboard (Aya)
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
                return rs.next() ? rs.getInt("total") : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: countTermineesPourMedecin(medecinId=" + medecinId + ")", e);
        }
    }



    @Override
    public Integer countEnCoursPourMedecin(Long medecinId, LocalDateTime start, LocalDateTime end) {
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
                return rs.next() ? rs.getInt("total") : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: countEnCoursPourMedecin(medecinId=" + medecinId + ")", e);
        }
    }
}
