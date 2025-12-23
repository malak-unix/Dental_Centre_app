package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.sql.Timestamp;

public class ActeRepositoryImpl implements ActeRepository {

    // -------- CRUD --------
    @Override
    public List<Acte> findAll() {
        String sql = "SELECT * FROM acte ORDER BY categorie, libelle";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapActe(rs));

        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Acte findById(Long id) {
        String sql = "SELECT * FROM acte WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapActe(rs);
                return null;
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Acte a) {
        String sql = "INSERT INTO acte(libelle, categorie, prix_base, description) VALUES(?,?,?,?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());

            if (a.getPrixBase() != null) ps.setDouble(3, a.getPrixBase());
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, a.getDescription());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Acte a) {
        String sql = "UPDATE acte SET libelle=?, categorie=?, prix_base=?, description=? WHERE id=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());

            if (a.getPrixBase() != null) ps.setDouble(3, a.getPrixBase());
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, a.getDescription());

            ps.setLong(5, a.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Acte a) { if (a != null) deleteById(a.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM acte WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Extras --------
    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = "SELECT * FROM acte WHERE categorie = ? ORDER BY libelle";
        List<Acte> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Acte> searchByLibelle(String keyword) {
        String sql = "SELECT * FROM acte WHERE libelle LIKE ? ORDER BY categorie, libelle";
        List<Acte> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM acte WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM acte";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        String sql = "SELECT * FROM acte ORDER BY categorie, libelle LIMIT ? OFFSET ?";
        List<Acte> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapActe(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Integer countActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {

        String sql = """
        SELECT COUNT(im.id) AS cnt
        FROM intervention_medecin im
        JOIN consultation c ON c.id = im.consultation_id
        JOIN dossier_medical dm ON dm.id = c.dossier_id
        WHERE dm.medecin_id = ?
          AND c.date_consultation >= ?
          AND c.date_consultation <= ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Double sumMontantActesPourMedecinEtDate(Long medecinId, LocalDateTime start, LocalDateTime end) {

        // On somme le montant réellement facturable par intervention:
        // - priorité à intervention_medecin.prix_patient
        // - sinon fallback sur acte.prix_base
        String sql = """
        SELECT COALESCE(SUM(COALESCE(im.prix_patient, a.prix_base, 0)), 0) AS total
        FROM intervention_medecin im
        JOIN consultation c ON c.id = im.consultation_id
        JOIN dossier_medical dm ON dm.id = c.dossier_id
        LEFT JOIN acte a ON a.id = im.acte_id
        WHERE dm.medecin_id = ?
          AND c.date_consultation >= ?
          AND c.date_consultation <= ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
