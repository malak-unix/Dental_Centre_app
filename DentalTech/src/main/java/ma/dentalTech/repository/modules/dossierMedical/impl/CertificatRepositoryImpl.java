package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListRequestDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepository {

    // =====================================================================================
    // Mapping ResultSet -> Certificat
    // =====================================================================================
    private Certificat map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long dossierId = rs.getLong("dossier_id");
        if (rs.wasNull()) dossierId = null;

        LocalDate dateDebut = null;
        Date d1 = rs.getDate("date_debut");
        if (d1 != null) dateDebut = d1.toLocalDate();

        LocalDate dateFin = null;
        Date d2 = rs.getDate("date_fin");
        if (d2 != null) dateFin = d2.toLocalDate();

        LocalDateTime dateCreation = null;
        Timestamp tCreate = rs.getTimestamp("date_creation");
        if (tCreate != null) dateCreation = tCreate.toLocalDateTime();

        LocalDateTime dateModif = null;
        Timestamp tModif = rs.getTimestamp("date_modification");
        if (tModif != null) dateModif = tModif.toLocalDateTime();

        return Certificat.builder()
                .id(id)
                .dossierId(dossierId)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .duree(rs.getInt("duree"))
                .noteMedecin(rs.getString("note_medecin"))
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =====================================================================================
    // CRUD
    // =====================================================================================

    @Override
    public void create(Certificat c) {
        if (c == null) throw new IllegalArgumentException("Certificat null dans create()");

        String sql = """
                INSERT INTO certificat
                (dossier_id, date_debut, date_fin, duree, note_medecin,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (c.getDossierId() != null) ps.setLong(1, c.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (c.getDateDebut() != null) ps.setDate(2, Date.valueOf(c.getDateDebut()));
            else ps.setNull(2, Types.DATE);

            if (c.getDateFin() != null) ps.setDate(3, Date.valueOf(c.getDateFin()));
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, c.getDuree());
            ps.setString(5, c.getNoteMedecin());

            LocalDateTime dc = (c.getDateCreation() != null) ? c.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dc));

            ps.setString(7, c.getCreePar());
            ps.setString(8, c.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.create()", e);
        }
    }

    @Override
    public void update(Certificat c) {
        if (c == null) throw new IllegalArgumentException("Certificat null dans update()");
        if (c.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");

        String sql = """
                UPDATE certificat
                   SET dossier_id = ?,
                       date_debut = ?,
                       date_fin = ?,
                       duree = ?,
                       note_medecin = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (c.getDossierId() != null) ps.setLong(1, c.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (c.getDateDebut() != null) ps.setDate(2, Date.valueOf(c.getDateDebut()));
            else ps.setNull(2, Types.DATE);

            if (c.getDateFin() != null) ps.setDate(3, Date.valueOf(c.getDateFin()));
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, c.getDuree());
            ps.setString(5, c.getNoteMedecin());

            LocalDateTime dm = (c.getDateDerniereModification() != null)
                    ? c.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dm));

            ps.setString(7, c.getModifiePar());
            ps.setLong(8, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.update(id=" + c.getId() + ")", e);
        }
    }

    @Override
    public Certificat findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM certificat WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findById(" + id + ")", e);
        }
    }

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM certificat ORDER BY date_debut DESC, id DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findAll()", e);
        }
    }

    @Override
    public void delete(Certificat c) {
        if (c != null && c.getId() != null) deleteById(c.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM certificat WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.deleteById(" + id + ")", e);
        }
    }

    // =====================================================================================
    // Méthodes spécifiques
    // =====================================================================================

    @Override
    public List<Certificat> findByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();

        String sql = "SELECT * FROM certificat WHERE dossier_id = ? ORDER BY date_debut DESC, id DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findByDossierId(" + dossierId + ")", e);
        }
    }

    @Override
    public List<Certificat> findByDateDebut(LocalDate dateDebut) {
        if (dateDebut == null) return List.of();

        String sql = "SELECT * FROM certificat WHERE date_debut = ? ORDER BY id DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dateDebut));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findByDateDebut(" + dateDebut + ")", e);
        }
    }

    @Override
    public List<Certificat> findByDateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return List.of();

        String sql = """
                SELECT * FROM certificat
                 WHERE date_debut BETWEEN ? AND ?
                 ORDER BY date_debut DESC, id DESC
                """;
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findByDateBetween(" + start + "," + end + ")", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM certificat WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM certificat";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong("total") : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.count()", e);
        }
    }

    @Override
    public List<Certificat> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM certificat
                 ORDER BY date_debut DESC, id DESC
                 LIMIT ? OFFSET ?
                """;
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }
    }

    @Override
    public List<Certificat> searchByNote(String keyword) {
        String sql = """
                SELECT * FROM certificat
                 WHERE note_medecin LIKE ?
                 ORDER BY date_debut DESC, id DESC
                """;

        List<Certificat> list = new ArrayList<>();
        String like = "%" + (keyword == null ? "" : keyword) + "%";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.searchByNote(" + keyword + ")", e);
        }
    }

    // =====================================================================================
    // Méthodes pour la liste avec nom du patient (JOIN)
    // =====================================================================================

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
            } else if (p instanceof Timestamp) {
                ps.setTimestamp(idx, (Timestamp) p);
            } else {
                ps.setObject(idx, p);
            }
        }
    }

    @Override
    public List<CertificatListItemDTO> searchForList(CertificatListRequestDTO req) {
        if (req == null) throw new IllegalArgumentException("CertificatListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes certificats'");

        StringBuilder sql = new StringBuilder("""
        SELECT
          cert.id AS certificat_id,
          cert.dossier_id AS dossier_id,
          cert.date_debut AS date_debut,
          cert.date_fin AS date_fin,
          cert.duree AS duree,
          cert.note_medecin AS note_medecin,
          p.id AS patient_id,
          CONCAT(p.nom, ' ', p.prenom) AS patient_nom_complet
        FROM certificat cert
        JOIN dossier_medical d ON d.id = cert.dossier_id
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

        // Filtre date début
        if (req.getDateDebutFrom() != null) {
            sql.append(" AND cert.date_debut >= ? ");
            params.add(req.getDateDebutFrom());
        }
        if (req.getDateDebutTo() != null) {
            sql.append(" AND cert.date_debut <= ? ");
            params.add(req.getDateDebutTo());
        }

        // Filtre date fin
        if (req.getDateFinFrom() != null) {
            sql.append(" AND cert.date_fin >= ? ");
            params.add(req.getDateFinFrom());
        }
        if (req.getDateFinTo() != null) {
            sql.append(" AND cert.date_fin <= ? ");
            params.add(req.getDateFinTo());
        }

        // Filtre noteKeyword
        if (!isBlank(req.getNoteKeyword())) {
            sql.append(" AND cert.note_medecin LIKE ? ");
            params.add("%" + req.getNoteKeyword().trim() + "%");
        }

        sql.append(" ORDER BY cert.date_debut DESC, cert.id DESC ");

        List<CertificatListItemDTO> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CertificatListItemDTO dto = new CertificatListItemDTO();

                    dto.setCertificatId(rs.getLong("certificat_id"));
                    dto.setDossierId(rs.getLong("dossier_id"));

                    Date d1 = rs.getDate("date_debut");
                    dto.setDateDebut(d1 != null ? d1.toLocalDate() : null);

                    Date d2 = rs.getDate("date_fin");
                    dto.setDateFin(d2 != null ? d2.toLocalDate() : null);

                    int duree = rs.getInt("duree");
                    dto.setDuree(rs.wasNull() ? null : duree);

                    dto.setNoteMedecin(rs.getString("note_medecin"));

                    dto.setPatientId(rs.getLong("patient_id"));
                    dto.setPatientNomComplet(rs.getString("patient_nom_complet"));

                    out.add(dto);
                }
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.searchForList()", e);
        }
    }

    @Override
    public long countForList(CertificatListRequestDTO req) {
        if (req == null) throw new IllegalArgumentException("CertificatListRequestDTO null");
        if (req.getMedecinId() == null) throw new IllegalArgumentException("medecinId obligatoire pour 'Mes certificats'");

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(*)
        FROM certificat cert
        JOIN dossier_medical d ON d.id = cert.dossier_id
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

        if (req.getDateDebutFrom() != null) {
            sql.append(" AND cert.date_debut >= ? ");
            params.add(req.getDateDebutFrom());
        }
        if (req.getDateDebutTo() != null) {
            sql.append(" AND cert.date_debut <= ? ");
            params.add(req.getDateDebutTo());
        }

        if (req.getDateFinFrom() != null) {
            sql.append(" AND cert.date_fin >= ? ");
            params.add(req.getDateFinFrom());
        }
        if (req.getDateFinTo() != null) {
            sql.append(" AND cert.date_fin <= ? ");
            params.add(req.getDateFinTo());
        }

        if (!isBlank(req.getNoteKeyword())) {
            sql.append(" AND cert.note_medecin LIKE ? ");
            params.add("%" + req.getNoteKeyword().trim() + "%");
        }

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Certificat.countForList()", e);
        }
    }
}
