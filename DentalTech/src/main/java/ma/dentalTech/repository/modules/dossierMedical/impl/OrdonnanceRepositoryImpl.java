package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // =========================================================================================
    // Méthode de mapping : ResultSet -> Ordonnance
    // =========================================================================================
    private Ordonnance map(ResultSet rs) throws SQLException {
        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");

        Long dossierId = rs.getLong("dossier_id");
        if (rs.wasNull()) {
            dossierId = null;
        }

        Long consultationId = rs.getLong("consultation_id");
        if (rs.wasNull()) {
            consultationId = null;
        }

        Date dateOrdo = rs.getDate("date_ordo");

        return Ordonnance.builder()
                .id(rs.getLong("id"))
                .dossierId(dossierId)
                .consultationId(consultationId)
                .date(dateOrdo != null ? dateOrdo.toLocalDate() : null)
                .dateCreation(tsCreation != null ? tsCreation.toLocalDateTime() : null)
                .dateDerniereModification(tsModification != null ? tsModification.toLocalDateTime() : null)
                .build();
    }

    // =========================================================================================
    // CRUD : méthodes de CrudRepository
    // =========================================================================================

    @Override
    public void create(Ordonnance ordonnance) {
        String sql = """
        INSERT INTO ordonnance(dossier_id, consultation_id, date_ordo, cree_par, modifie_par)
        VALUES (?, ?, ?, ?, ?)
        """;

        if (ordonnance == null) throw new IllegalArgumentException("Ordonnance null dans create()");

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // dossier_id
            if (ordonnance.getDossierId() != null) ps.setLong(1, ordonnance.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            // consultation_id
            if (ordonnance.getConsultationId() != null) ps.setLong(2, ordonnance.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            // date_ordo (NOT NULL)
            LocalDate d = ordonnance.getDate() != null ? ordonnance.getDate() : LocalDate.now();
            ps.setDate(3, Date.valueOf(d));

            ps.setString(4, ordonnance.getCreePar());
            ps.setString(5, ordonnance.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) ordonnance.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }


    @Override
    public void update(Ordonnance ordonnance) {
        if (ordonnance == null) throw new IllegalArgumentException("Ordonnance null dans update()");
        if (ordonnance.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");

        String sql = """
        UPDATE ordonnance
           SET dossier_id = ?,
               consultation_id = ?,
               date_ordo = ?,
               modifie_par = ?,
               date_modification = CURRENT_TIMESTAMP
         WHERE id = ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (ordonnance.getDossierId() != null) ps.setLong(1, ordonnance.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (ordonnance.getConsultationId() != null) ps.setLong(2, ordonnance.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            // date_ordo (NOT NULL)
            LocalDate d = ordonnance.getDate() != null ? ordonnance.getDate() : LocalDate.now();
            ps.setDate(3, Date.valueOf(d));

            ps.setString(4, ordonnance.getModifiePar());
            ps.setLong(5, ordonnance.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'ordonnance", e);
        }
    }


    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE id = ?";

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
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance ORDER BY id";
        List<Ordonnance> list = new ArrayList<>();

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
    public void delete(Ordonnance ordonnance) {
        if (ordonnance != null && ordonnance.getId() != null) {
            deleteById(ordonnance.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    // =========================================================================================
    // Méthodes spécifiques de OrdonnanceRepository
    // =========================================================================================

    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM ordonnance WHERE dossier_id = ? ORDER BY date_ordo, id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

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
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM ordonnance WHERE consultation_id = ? ORDER BY date_ordo, id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

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
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM ordonnance WHERE date_ordo = ? ORDER BY id";
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

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
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
                SELECT * FROM ordonnance
                 WHERE date_ordo BETWEEN ? AND ?
                 ORDER BY date_ordo, id
                """;
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

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
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM ordonnance";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
        } catch (SQLException  e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }

        return 0;
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM ordonnance
                 ORDER BY date_ordo DESC, id DESC
                 LIMIT ? OFFSET ?
                """;
        List<Ordonnance> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

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

    // =========================================================================================
    // Méthodes pour la liste avec nom du patient (JOIN)
    // =========================================================================================

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            int idx = i + 1;
            if (p == null) {
                ps.setNull(idx, Types.VARCHAR);
            } else if (p instanceof String) {
                ps.setString(idx, (String) p);
            } else if (p instanceof Long) {
                ps.setLong(idx, (Long) p);
            } else if (p instanceof Integer) {
                ps.setInt(idx, (Integer) p);
            } else if (p instanceof LocalDate) {
                ps.setDate(idx, Date.valueOf((LocalDate) p));
            } else if (p instanceof Date) {
                ps.setDate(idx, (Date) p);
            } else {
                ps.setObject(idx, p);
            }
        }
    }

    @Override
    public List<OrdonnanceListItemDTO> searchForList(OrdonnanceListRequestDTO req) {
        if (req == null) throw new IllegalArgumentException("OrdonnanceListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes ordonnances'");

        StringBuilder sql = new StringBuilder("""
        SELECT
          o.id AS ordonnance_id,
          o.dossier_id AS dossier_id,
          o.consultation_id AS consultation_id,
          o.date_ordo AS date_ordo,
          p.id AS patient_id,
          CONCAT(p.nom, ' ', p.prenom) AS patient_nom_complet
        FROM ordonnance o
        JOIN dossier_medical d ON d.id = o.dossier_id
        JOIN patient p ON p.id = d.patient_id
        WHERE d.medecin_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(req.getMedecinId());

        // Filtre patientKeyword
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

        // Filtre date
        if (req.getDateFrom() != null) {
            sql.append(" AND o.date_ordo >= ? ");
            params.add(req.getDateFrom());
        }
        if (req.getDateTo() != null) {
            sql.append(" AND o.date_ordo <= ? ");
            params.add(req.getDateTo());
        }

        sql.append(" ORDER BY o.date_ordo DESC, o.id DESC ");

        List<OrdonnanceListItemDTO> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdonnanceListItemDTO dto = new OrdonnanceListItemDTO();

                    dto.setOrdonnanceId(rs.getLong("ordonnance_id"));
                    dto.setDossierId(rs.getLong("dossier_id"));

                    long cid = rs.getLong("consultation_id");
                    dto.setConsultationId(rs.wasNull() ? null : cid);

                    Date d = rs.getDate("date_ordo");
                    dto.setDate(d != null ? d.toLocalDate() : null);

                    dto.setPatientId(rs.getLong("patient_id"));
                    dto.setPatientNomComplet(rs.getString("patient_nom_complet"));

                    out.add(dto);
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.searchForList()", e);
        }
    }

    @Override
    public long countForList(OrdonnanceListRequestDTO req) {
        if (req == null) throw new IllegalArgumentException("OrdonnanceListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes ordonnances'");

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM ordonnance o
        JOIN dossier_medical d ON d.id = o.dossier_id
        JOIN patient p ON p.id = d.patient_id
        WHERE d.medecin_id = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(req.getMedecinId());

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

        if (req.getDateFrom() != null) {
            sql.append(" AND o.date_ordo >= ? ");
            params.add(req.getDateFrom());
        }
        if (req.getDateTo() != null) {
            sql.append(" AND o.date_ordo <= ? ");
            params.add(req.getDateTo());
        }

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.countForList()", e);
        }
    }
}
