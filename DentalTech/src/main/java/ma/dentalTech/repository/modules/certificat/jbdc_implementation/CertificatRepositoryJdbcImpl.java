package ma.dentalTech.repository.modules.certificat.jbdc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.certificat.Certificat;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.certificat.api.CertificatRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryJdbcImpl implements CertificatRepository {

    // =====================================================================================
    // Mapping ResultSet -> Certificat
    // =====================================================================================
    private Certificat map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

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
    // CRUD de base
    // =====================================================================================

    @Override
    public void create(Certificat c) {
        String sql = """
                INSERT INTO certificat
                (date_debut, date_fin, duree, note_medecin,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (c.getDateDebut() != null) ps.setDate(1, Date.valueOf(c.getDateDebut()));
            else ps.setNull(1, Types.DATE);

            if (c.getDateFin() != null) ps.setDate(2, Date.valueOf(c.getDateFin()));
            else ps.setNull(2, Types.DATE);

            ps.setInt(3, c.getDuree());
            ps.setString(4, c.getNoteMedecin());

            LocalDateTime dc = (c.getDateCreation() != null) ? c.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dc));

            ps.setString(6, c.getCreePar());
            ps.setString(7, c.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }
    }

    @Override
    public void update(Certificat c) {
        String sql = """
                UPDATE certificat
                   SET date_debut = ?,
                       date_fin = ?,
                       duree = ?,
                       note_medecin = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (c.getDateDebut() != null) ps.setDate(1, Date.valueOf(c.getDateDebut()));
            else ps.setNull(1, Types.DATE);

            if (c.getDateFin() != null) ps.setDate(2, Date.valueOf(c.getDateFin()));
            else ps.setNull(2, Types.DATE);

            ps.setInt(3, c.getDuree());
            ps.setString(4, c.getNoteMedecin());

            LocalDateTime dm = (c.getDateDerniereModification() != null)
                    ? c.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));

            ps.setString(6, c.getModifiePar());
            ps.setLong(7, c.getId());

            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du certificat", e);
        }
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM certificat WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du certificat par ID", e);
        }
        return null;
    }

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM certificat ORDER BY id";
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les certificats", e);
        }
        return list;
    }

    @Override
    public void delete(Certificat c) {
        if (c != null && c.getId() != null) {
            deleteById(c.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM certificat WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression du certificat", e);
        }
    }

    // =====================================================================================
    // Méthodes spécifiques
    // =====================================================================================

    @Override
    public List<Certificat> findByDateDebut(LocalDate dateDebut) {
        String sql = "SELECT * FROM certificat WHERE date_debut = ? ORDER BY id";
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dateDebut));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par dateDebut", e);
        }

        return list;
    }

    @Override
    public List<Certificat> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
                SELECT * FROM certificat
                 WHERE date_debut BETWEEN ? AND ?
                 ORDER BY date_debut, id
                """;
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par période", e);
        }

        return list;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM certificat";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong("total");
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du comptage des certificats", e);
        }

        return 0;
    }

    @Override
    public List<Certificat> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM certificat
                 ORDER BY date_debut DESC, id DESC
                 LIMIT ? OFFSET ?
                """;
        List<Certificat> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de la page de certificats", e);
        }

        return list;
    }
}
