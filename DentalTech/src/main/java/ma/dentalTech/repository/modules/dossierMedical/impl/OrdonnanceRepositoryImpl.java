package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // ------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------
    @Override
    public void create(Ordonnance o) {
        if (o == null) throw new IllegalArgumentException("Ordonnance null dans create()");

        String sql = """
            INSERT INTO ordonnance
            (dossier_id, consultation_id, date_ordo, date_creation, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // dossier_id (nullable en SQL)
            if (o.getDossierId() != null) ps.setLong(1, o.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            // consultation_id (nullable en SQL)
            if (o.getConsultationId() != null) ps.setLong(2, o.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            // date_ordo (NOT NULL en SQL) -> si null, on met aujourd'hui
            LocalDate d = (o.getDate() != null) ? o.getDate() : LocalDate.now();
            ps.setDate(3, Date.valueOf(d));
            o.setDate(d);

            LocalDateTime dc = (o.getDateCreation() != null) ? o.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dc));
            o.setDateCreation(dc);

            ps.setString(5, o.getCreePar());
            ps.setString(6, o.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.create()", e);
        }
    }

    @Override
    public void update(Ordonnance o) {
        if (o == null) throw new IllegalArgumentException("Ordonnance null dans update()");
        if (o.getId() == null) throw new IllegalArgumentException("id obligatoire dans update()");

        String sql = """
            UPDATE ordonnance
               SET dossier_id = ?,
                   consultation_id = ?,
                   date_ordo = ?,
                   date_modification = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (o.getDossierId() != null) ps.setLong(1, o.getDossierId());
            else ps.setNull(1, Types.BIGINT);

            if (o.getConsultationId() != null) ps.setLong(2, o.getConsultationId());
            else ps.setNull(2, Types.BIGINT);

            LocalDate d = (o.getDate() != null) ? o.getDate() : LocalDate.now();
            ps.setDate(3, Date.valueOf(d));
            o.setDate(d);

            LocalDateTime dm = (o.getDateDerniereModification() != null)
                    ? o.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dm));
            o.setDateDerniereModification(dm);

            ps.setString(5, o.getModifiePar());
            ps.setLong(6, o.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.update(id=" + o.getId() + ")", e);
        }
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapOrdonnance(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findById(" + id + ")", e);
        }
    }

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance ORDER BY date_ordo DESC, id DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findAll()", e);
        }

        return out;
    }

    @Override
    public void delete(Ordonnance o) {
        if (o != null && o.getId() != null) deleteById(o.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.deleteById(" + id + ")", e);
        }
    }

    // ------------------------------------------------------------
    // Méthodes spécifiques (interface)
    // ------------------------------------------------------------
    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM ordonnance WHERE dossier_id = ? ORDER BY date_ordo DESC, id DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findByDossierId(" + dossierId + ")", e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM ordonnance WHERE consultation_id = ? ORDER BY date_ordo DESC, id DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findByConsultationId(" + consultationId + ")", e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM ordonnance WHERE date_ordo = ? ORDER BY id DESC";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findByDate(" + date + ")", e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
            SELECT * FROM ordonnance
             WHERE date_ordo BETWEEN ? AND ?
             ORDER BY date_ordo DESC, id DESC
            """;
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findByDateBetween(" + start + "," + end + ")", e);
        }

        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM ordonnance";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.count()", e);
        }
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM ordonnance
             ORDER BY date_ordo DESC, id DESC
             LIMIT ? OFFSET ?
            """;
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.findPage(limit=" + limit + ", offset=" + offset + ")", e);
        }

        return out;
    }

    @Override
    public Ordonnance findLastByDossierId(Long dossierId) {
        return null;
    }

    @Override
    public Ordonnance findLastByConsultationId(Long consultationId) {
        return null;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL: Ordonnance.existsById(" + id + ")", e);
        }
    }
}
