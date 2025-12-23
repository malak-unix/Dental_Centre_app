package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepository {

    // =====================================================================================
    // CRUD
    // =====================================================================================

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM certificat ORDER BY id DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapCertificat(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findAll() Certificat", e);
        }
        return out;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM certificat WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCertificat(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findById(" + id + ") Certificat", e);
        }
    }

    @Override
    public void create(Certificat cert) {
        String sql = """
            INSERT INTO certificat (dossier_id, date_debut, date_fin, duree, note_medecin, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (cert.getDossierId() != null) ps.setLong(1, cert.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (cert.getDateDebut() != null) ps.setDate(2, Date.valueOf(cert.getDateDebut()));
            else ps.setNull(2, Types.DATE);

            if (cert.getDateFin() != null) ps.setDate(3, Date.valueOf(cert.getDateFin()));
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, cert.getDuree());
            ps.setString(5, cert.getNoteMedecin());

            ps.setString(6, cert.getCreePar());
            ps.setString(7, cert.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cert.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: create() Certificat", e);
        }
    }

    @Override
    public void update(Certificat cert) {
        String sql = """
            UPDATE certificat
               SET dossier_id = ?,
                   date_debut = ?,
                   date_fin = ?,
                   duree = ?,
                   note_medecin = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (cert.getDossierId() != null) ps.setLong(1, cert.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (cert.getDateDebut() != null) ps.setDate(2, Date.valueOf(cert.getDateDebut()));
            else ps.setNull(2, Types.DATE);

            if (cert.getDateFin() != null) ps.setDate(3, Date.valueOf(cert.getDateFin()));
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, cert.getDuree());
            ps.setString(5, cert.getNoteMedecin());
            ps.setString(6, cert.getModifiePar());
            ps.setLong(7, cert.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: update() Certificat id=" + cert.getId(), e);
        }
    }

    @Override
    public void delete(Certificat cert) {
        if (cert != null && cert.getId() != null) deleteById(cert.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM certificat WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: deleteById(" + id + ") Certificat", e);
        }
    }

    // =====================================================================================
    // Méthodes spécifiques (comme ton code mais plus clean)
    // =====================================================================================

    @Override
    public List<Certificat> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM certificat WHERE dossier_id = ? ORDER BY date_debut DESC, id DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByDossierId(" + dossierId + ") Certificat", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findByDateDebut(LocalDate dateDebut) {
        String sql = "SELECT * FROM certificat WHERE date_debut = ? ORDER BY id DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dateDebut));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByDateDebut(" + dateDebut + ") Certificat", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
            SELECT * FROM certificat
            WHERE date_debut BETWEEN ? AND ?
            ORDER BY date_debut DESC, id DESC
            """;

        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findByDateBetween(" + start + "," + end + ") Certificat", e);
        }
        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM certificat";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: count() Certificat", e);
        }
    }

    @Override
    public List<Certificat> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM certificat
            ORDER BY id DESC
            LIMIT ? OFFSET ?
            """;

        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: findPage(limit=" + limit + ", offset=" + offset + ") Certificat", e);
        }
        return out;
    }
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM certificat WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: existsById(" + id + ") Certificat", e);
        }
    }
    @Override
    public List<Certificat> searchByNote(String keyword) {
        String sql = "SELECT * FROM certificat WHERE note_medecin LIKE ? ORDER BY date_debut DESC, id DESC";
        List<Certificat> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: searchByNote(keyword=" + keyword + ") Certificat", e);
        }
        return out;
    }


}
