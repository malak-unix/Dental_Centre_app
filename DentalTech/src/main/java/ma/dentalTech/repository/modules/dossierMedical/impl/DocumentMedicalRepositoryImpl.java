package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.DocumentMedical;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.DocumentMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentMedicalRepositoryImpl implements DocumentMedicalRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public List<DocumentMedical> findAll() {
        String sql = "SELECT * FROM document_medical ORDER BY id DESC";
        List<DocumentMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findAll()", e);
        }
        return out;
    }

    @Override
    public DocumentMedical findById(Long id) {
        String sql = "SELECT * FROM document_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapDocumentMedical(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findById(" + id + ")", e);
        }
    }

    @Override
    public void create(DocumentMedical d) {
        String sql = """
            INSERT INTO document_medical
            (dossier_id, consultation_id, type_document, titre, nom_fichier, chemin_fichier, taille_octets,
             date_document, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        if (d == null) throw new IllegalArgumentException("DocumentMedical null dans create()");
        if (d.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire (NOT NULL)");
        if (d.getCheminFichier() == null) throw new IllegalArgumentException("cheminFichier obligatoire (NOT NULL)");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, d.getDossierId());

            if (d.getConsultationId() != null) ps.setLong(2, d.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getTypeDocument() == null ? "AUTRE" : d.getTypeDocument().name());
            ps.setString(4, d.getTitre());
            ps.setString(5, d.getNomFichier());
            ps.setString(6, d.getCheminFichier());

            if (d.getTailleOctets() != null) ps.setLong(7, d.getTailleOctets());
            else ps.setNull(7, Types.BIGINT);

            if (d.getDateDocument() != null) ps.setTimestamp(8, Timestamp.valueOf(d.getDateDocument()));
            else ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

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

        if (d == null) throw new IllegalArgumentException("DocumentMedical null dans update()");
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");
        if (d.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire dans update()");
        if (d.getCheminFichier() == null) throw new IllegalArgumentException("cheminFichier obligatoire dans update()");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, d.getDossierId());

            if (d.getConsultationId() != null) ps.setLong(2, d.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            ps.setString(3, d.getTypeDocument() == null ? "AUTRE" : d.getTypeDocument().name());
            ps.setString(4, d.getTitre());
            ps.setString(5, d.getNomFichier());
            ps.setString(6, d.getCheminFichier());

            if (d.getTailleOctets() != null) ps.setLong(7, d.getTailleOctets());
            else ps.setNull(7, Types.BIGINT);

            if (d.getDateDocument() != null) ps.setTimestamp(8, Timestamp.valueOf(d.getDateDocument()));
            else ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

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

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findByDossierId(" + dossierId + ")", e);
        }
        return out;
    }

    @Override
    public List<DocumentMedical> findByConsultationId(Long consultationId) {
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

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findByConsultationId(" + consultationId + ")", e);
        }
        return out;
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

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String like = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDocumentMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.searchByTitreOrNom(" + keyword + ")", e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
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
        String sql = "SELECT COUNT(*) FROM document_medical";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

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

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: DocumentMedical.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }
        return out;
    }
}
