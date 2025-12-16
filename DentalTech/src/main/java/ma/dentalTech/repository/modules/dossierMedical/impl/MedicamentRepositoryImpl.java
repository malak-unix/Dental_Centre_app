package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    // =========================================================================
    // Mapping ResultSet -> Medicament
    // =========================================================================
    private Medicament map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Timestamp tCreate = rs.getTimestamp("date_creation");
        LocalDateTime dateCreation = (tCreate != null) ? tCreate.toLocalDateTime() : null;

        Timestamp tModif = rs.getTimestamp("date_modification");
        LocalDateTime dateModif = (tModif != null) ? tModif.toLocalDateTime() : null;

        String formeStr = rs.getString("forme");
        FormeMedicament forme = null;
        if (formeStr != null) {
            forme = FormeMedicament.valueOf(formeStr);
        }

        BigDecimal prixBD = rs.getBigDecimal("prix_unitaire");
        Double prix = (prixBD != null) ? prixBD.doubleValue() : null;

        return Medicament.builder()
                .id(id)
                .nom(rs.getString("nom"))
                .laboratoire(rs.getString("laboratoire"))
                .type(rs.getString("type_medicament"))
                .forme(forme)
                .remboursable(rs.getBoolean("remboursable"))
                .prixUnitaire(prix)
                .description(rs.getString("description"))
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =========================================================================
    // CRUD de base
    // =========================================================================

    @Override
    public void create(Medicament m) {
        String sql = """
                INSERT INTO medicament
                (nom, laboratoire, type_medicament, forme, remboursable,
                 prix_unitaire, description, date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());

            if (m.getForme() != null) {
                ps.setString(4, m.getForme().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) {
                ps.setBigDecimal(6, BigDecimal.valueOf(m.getPrixUnitaire()));
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            ps.setString(7, m.getDescription());

            LocalDateTime dc = (m.getDateCreation() != null) ? m.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dc));

            ps.setString(9, m.getCreePar());
            ps.setString(10, m.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public void update(Medicament m) {
        String sql = """
                UPDATE medicament
                   SET nom = ?,
                       laboratoire = ?,
                       type_medicament = ?,
                       forme = ?,
                       remboursable = ?,
                       prix_unitaire = ?,
                       description = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());

            if (m.getForme() != null) {
                ps.setString(4, m.getForme().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) {
                ps.setBigDecimal(6, BigDecimal.valueOf(m.getPrixUnitaire()));
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            ps.setString(7, m.getDescription());

            LocalDateTime dm = (m.getDateDerniereModification() != null)
                    ? m.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dm));

            ps.setString(9, m.getModifiePar());
            ps.setLong(10, m.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return null;
    }

    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM medicament ORDER BY nom, id";
        List<Medicament> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }

    @Override
    public void delete(Medicament m) {
        if (m != null && m.getId() != null) {
            deleteById(m.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    // =========================================================================
    // Méthodes spécifiques de MedicamentRepository
    // =========================================================================

    @Override
    public List<Medicament> searchByNom(String keyword) {
        String sql = "SELECT * FROM medicament WHERE nom LIKE ? ORDER BY nom, id";
        List<Medicament> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        String sql = "SELECT * FROM medicament WHERE remboursable = ? ORDER BY nom, id";
        List<Medicament> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, remboursable);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM medicament";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return 0;
    }

    @Override
    public List<Medicament> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM medicament
                 ORDER BY nom ASC, id ASC
                 LIMIT ? OFFSET ?
                """;
        List<Medicament> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }


        return list;
    }
}
