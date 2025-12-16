package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.role.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepository {

    private Role map(ResultSet rs) throws SQLException {
        Role r = new Role();

        // BaseEntity
        Long id = rs.getLong("id");
        if (!rs.wasNull()) r.setId(id);

        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) r.setDateCreation(dateCrea.toLocalDateTime());

        // CORRECTION ICI : setDateDerniereModification
        Timestamp dateModif = rs.getTimestamp("date_modification");
        if (dateModif != null) r.setDateDerniereModification(dateModif.toLocalDateTime());

        r.setCreePar(rs.getString("cree_par"));
        r.setModifiePar(rs.getString("modifie_par"));

        // Role
        String libelleStr = rs.getString("libelle");
        if (libelleStr != null) {
            try {
                r.setLibelle(LibelleRole.valueOf(libelleStr));
            } catch (IllegalArgumentException e) {
                r.setLibelle(null);
            }
        }

        // Note: La liste 'privilege' n'est pas chargée ici (nécessite une table de jointure)

        return r;
    }

    @Override
    public void create(Role r) {
        String sql = """
                INSERT INTO role
                (libelle, date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (r.getLibelle() != null) {
                ps.setString(1, r.getLibelle().name());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }

            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, r.getCreePar());
            ps.setString(4, r.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du rôle", e);
        }
    }

    @Override
    public void update(Role r) {
        String sql = """
                UPDATE role
                   SET libelle = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (r.getLibelle() != null) {
                ps.setString(1, r.getLibelle().name());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }

            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, r.getModifiePar());

            ps.setLong(4, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du rôle id=" + r.getId(), e);
        }
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche rôle id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role ORDER BY id";
        List<Role> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération liste rôles", e);
        }
        return list;
    }

    @Override
    public void delete(Role r) {
        if (r != null && r.getId() != null) deleteById(r.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression rôle id=" + id, e);
        }
    }
}