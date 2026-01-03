package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.DocumentMedical;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.DocumentMedicalRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentMedicalRepositoryImpl implements DocumentMedicalRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<DocumentMedical> findAll() {
        String sql = "SELECT * FROM document_medical ORDER BY date_document DESC, id DESC";
        List<DocumentMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findAll()", e);
        }
    }

    @Override
    public DocumentMedical findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM document_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapDocumentMedical(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(DocumentMedical d) {
        if (d == null) throw new IllegalArgumentException("DocumentMedical null dans create()");
        if (d.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire (NOT NULL)");
        if (d.getCheminFichier() == null || d.getCheminFichier().isBlank())
            throw new IllegalArgumentException("cheminFichier obligatoire (NOT NULL)");

        String sql = """
            INSERT INTO document_medical
            (dossier_id, consultation_id, type_document, titre, nom_fichier, chemin_fichier, taille_octets,
             date_document, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, d.getDossierId());

            if (d.getConsultationId() != null) ps.setLong(2, d.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getTypeDocument() == null ? "AUTRE" : d.getTypeDocument().name());
            ps.setString(4, d.getTitre());
            ps.setString(5, d.getNomFichier());
            ps.setString(6, d.getCheminFichier());

            long taille = (d.getTailleOctets() == null ? 0L : d.getTailleOctets());
            ps.setLong(7, taille);

            LocalDateTime dd = (d.getDateDocument() == null ? LocalDateTime.now() : d.getDateDocument());
            ps.setTimestamp(8, Timestamp.valueOf(dd));

            ps.setString(9, d.getCreePar());
            ps.setString(10, d.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.create()", e);
        }
    }

    @Override
    public void update(DocumentMedical d) {
        if (d == null) throw new IllegalArgumentException("DocumentMedical null dans update()");
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (d.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire (NOT NULL)");
        if (d.getCheminFichier() == null || d.getCheminFichier().isBlank())
            throw new IllegalArgumentException("cheminFichier obligatoire (NOT NULL)");

        String sql = """
            UPDATE document_medical
               SET dossier_id = ?,
                   consultation_id = ?,
                   type_document = ?,
                   titre = ?,
                   nom_fichier = ?,
                   chemin_fichier = ?,
                   taille_octets = ?,
                   date_document = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, d.getDossierId());

            if (d.getConsultationId() != null) ps.setLong(2, d.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getTypeDocument() == null ? "AUTRE" : d.getTypeDocument().name());
            ps.setString(4, d.getTitre());
            ps.setString(5, d.getNomFichier());
            ps.setString(6, d.getCheminFichier());

            long taille = (d.getTailleOctets() == null ? 0L : d.getTailleOctets());
            ps.setLong(7, taille);

            LocalDateTime dd = (d.getDateDocument() == null ? LocalDateTime.now() : d.getDateDocument());
            ps.setTimestamp(8, Timestamp.valueOf(dd));

            ps.setString(9, d.getModifiePar());
            ps.setLong(10, d.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.update(id=" + d.getId() + ")", e);
        }
    }

    @Override
    public void delete(DocumentMedical d) {
        if (d != null && d.getId() != null) deleteById(d.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM document_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Extras
    // ------------------------------------------------------------
    @Override
    public List<DocumentMedical> findByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();

        String sql = """
            SELECT * FROM document_medical
             WHERE dossier_id = ?
             ORDER BY date_document DESC, id DESC
            """;
        List<DocumentMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findByDossierId(" + dossierId + ")", e);
        }
    }

    @Override
    public List<DocumentMedical> findByPatientId(Long patientId) {
        return List.of();
    }

    @Override
    public List<DocumentMedical> findByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();

        String sql = """
            SELECT * FROM document_medical
             WHERE consultation_id = ?
             ORDER BY date_document DESC, id DESC
            """;
        List<DocumentMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findByConsultationId(" + consultationId + ")", e);
        }
    }

    @Override
    public List<DocumentMedical> searchByTitreOrNom(String keyword) {
        String sql = """
            SELECT * FROM document_medical
             WHERE titre LIKE ?
                OR nom_fichier LIKE ?
             ORDER BY date_document DESC, id DESC
            """;
        List<DocumentMedical> out = new ArrayList<>();
        String like = "%" + (keyword == null ? "" : keyword) + "%";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.searchByTitreOrNom(" + keyword + ")", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM document_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.existsById(" + id + ")", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM document_medical";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong("total") : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.count()", e);
        }
    }

    @Override
    public List<DocumentMedical> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM document_medical
             ORDER BY date_document DESC, id DESC
             LIMIT ? OFFSET ?
            """;
        List<DocumentMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            }

            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }
    }
}
