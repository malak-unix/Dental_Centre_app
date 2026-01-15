package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActeRepositoryImpl implements ActeRepository {

    private static Timestamp toTs(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public List<Acte> findAll() {
        String sql = "SELECT * FROM acte ORDER BY libelle ASC, id ASC";
        List<Acte> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapActe(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() Acte", e);
        }
    }

    @Override
    public Acte findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM acte WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapActe(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() Acte, id=" + id, e);
        }
    }

    @Override
    public void create(Acte a) {
        if (a == null) throw new IllegalArgumentException("Acte null");

        String sql = """
            INSERT INTO acte (libelle, categorie, prix_base, description, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());
            ps.setDouble(3, a.getPrixBase() == null ? 0.0 : a.getPrixBase());
            ps.setString(4, a.getDescription());
            ps.setString(5, a.getCreePar());
            ps.setString(6, a.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Acte", e);
        }
    }

    @Override
    public void update(Acte a) {
        if (a == null) throw new IllegalArgumentException("Acte null");
        if (a.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE acte
               SET libelle = ?,
                   categorie = ?,
                   prix_base = ?,
                   description = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());
            ps.setDouble(3, a.getPrixBase() == null ? 0.0 : a.getPrixBase());
            ps.setString(4, a.getDescription());
            ps.setString(5, a.getModifiePar());
            ps.setLong(6, a.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Acte, id=" + a.getId(), e);
        }
    }

    @Override
    public void delete(Acte a) {
        if (a == null || a.getId() == null) return;
        deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM acte WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() Acte, id=" + id, e);
        }
    }

    // =========================================================================
    // Méthodes spécifiques
    // =========================================================================

    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = "SELECT * FROM acte WHERE categorie = ? ORDER BY libelle ASC, id ASC";
        List<Acte> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapActe(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCategorie() Acte, categorie=" + categorie, e);
        }
    }

    @Override
    public List<Acte> searchByLibelle(String keyword) {
        String sql = "SELECT * FROM acte WHERE libelle LIKE ? ORDER BY libelle ASC, id ASC";
        List<Acte> list = new ArrayList<>();
        String k = (keyword == null) ? "" : keyword;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + k + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapActe(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur searchByLibelle() Acte, keyword=" + keyword, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM acte WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsById() Acte, id=" + id, e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM acte";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong("total") : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur count() Acte", e);
        }
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM acte
             ORDER BY libelle ASC, id ASC
             LIMIT ? OFFSET ?
            """;
        List<Acte> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapActe(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findPage() Acte, limit=" + limit + ", offset=" + offset, e);
        }
    }

    // =========================================================================
    // Dashboard (stats médecin sur période)
    // =========================================================================

    @Override
    public Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0;

        String sql = """
            SELECT COUNT(*) AS total
              FROM intervention_medecin im
              JOIN consultation c ON c.id = im.consultation_id
              JOIN dossier_medical d ON d.id = c.dossier_id
             WHERE d.medecin_id = ?
               AND c.date_consultation BETWEEN ? AND ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, toTs(start));
            ps.setTimestamp(3, toTs(end));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countActesPourMedecinEtDate(medecinId=" + medecinId + ")", e);
        }
    }

    @Override
    public long countActesDuJourPourMedecin(Long medecinId) {
        if (medecinId == null) return 0;

    /*
      Hypothèse la plus cohérente avec ton schéma:
      - acte est lié à consultation (acte.consultation_id)
      - consultation est liée à dossier_medical (consultation.dossier_id)
      - dossier_medical est lié à patient (dossier_medical.patient_id)
      - patient -> rdv (rdv.patient_id)
      - rdv -> detail_journee -> agenda_mensuel (agenda_mensuel.medecin_id)
      - "du jour" = DATE(consultation.date_consultation) = CURDATE()
    */

        String sql = """
        SELECT COUNT(*)
        FROM acte a
        JOIN consultation c ON c.id = a.consultation_id
        JOIN dossier_medical dm ON dm.id = c.dossier_id
        JOIN rdv r ON r.patient_id = dm.patient_id
        JOIN detail_journee dj ON dj.id = r.detail_journee_id
        JOIN agenda_mensuel am ON am.id = dj.agenda_id
        WHERE DATE(c.date_consultation) = CURDATE()
          AND am.medecin_id = ?
    """;

        try (java.sql.Connection cn = ma.dentalTech.configuration.SessionFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur countActesDuJourPourMedecin(Acte)", e);
        }
    }


    @Override
    public Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {
        if (medecinId == null || start == null || end == null) return 0.0;

        // Option 1: somme des prix_patient saisis sur les interventions (plus réaliste)
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
            ps.setTimestamp(2, toTs(start));
            ps.setTimestamp(3, toTs(end));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur sumMontantActesPourMedecinEtDate(medecinId=" + medecinId + ")", e);
        }
    }
    @Override
    public long countAll() {
        // si ta table s'appelle "acte"
        String sql = "SELECT COUNT(*) FROM acte";

        try (java.sql.Connection cn = ma.dentalTech.configuration.SessionFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur countAll(Acte)", e);
        }
    }


    @Override
    public java.math.BigDecimal totalRecetteDuJourPourMedecin(Long medecinId) {
        if (medecinId == null) return java.math.BigDecimal.ZERO;

        // Recette = somme des paiements du jour des factures liées aux consultations
        // Consultation -> Dossier -> Patient, et le médecin est porté par agenda_mensuel (via rdv/detail_journee)
        // Donc on remonte: facture -> consultation -> (dossier) -> (patient) -> rdv -> detail_journee -> agenda_mensuel.medecin_id
        String sql = """
        SELECT COALESCE(SUM(f.total_paye),0)
        FROM facture f
        JOIN consultation c ON c.id = f.consultation_id
        JOIN dossier_medical dm ON dm.id = c.dossier_id
        JOIN rdv r ON r.patient_id = dm.patient_id
        JOIN detail_journee dj ON dj.id = r.detail_journee_id
        JOIN agenda_mensuel am ON am.id = dj.agenda_id
        WHERE f.date_facture = CURDATE()
          AND am.medecin_id = ?
    """;

        try (java.sql.Connection cn = ma.dentalTech.configuration.SessionFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return java.math.BigDecimal.ZERO;
                java.math.BigDecimal v = rs.getBigDecimal(1);
                return v == null ? java.math.BigDecimal.ZERO : v;
            }

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur totalRecetteDuJourPourMedecin(Acte)", e);
        }
    }

}
