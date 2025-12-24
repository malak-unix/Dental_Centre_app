package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.role.Role;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {

    // Méthode utilitaire pour convertir ResultSet -> Role
    private Role map(ResultSet rs) throws SQLException {
        Role role = new Role();

        // 1. Mapping ID
        role.setId(rs.getLong("id"));

        // 2. Mapping Enum LibelleRole
        String libelleStr = rs.getString("libelle");
        if (libelleStr != null) {
            try {
                role.setLibelle(LibelleRole.valueOf(libelleStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Role inconnu en base : " + libelleStr);
            }
        }

        // 3. Mapping Privileges (String BDD -> List<String> Java)
        String privStr = rs.getString("privileges");
        List<String> privList = new ArrayList<>();

        if (privStr != null && !privStr.isEmpty()) {
            String[] parts = privStr.split(",");
            for (String p : parts) {
                privList.add(p.trim());
            }
        }
        role.setPrivilege(privList);

        // 4. Mapping BaseEntity (Sans actif)
        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) role.setDateCreation(dateCrea.toLocalDateTime());

        Timestamp dateModif = rs.getTimestamp("date_modification");
        if (dateModif != null) role.setDateDerniereModification(dateModif.toLocalDateTime());

        role.setCreePar(rs.getString("cree_par"));
        role.setModifiePar(rs.getString("modifie_par"));

        return role;
    }

    @Override
    public Optional<Role> findByLibelle(LibelleRole libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libelle.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLibelle", e);
        }
        return Optional.empty();
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
            throw new RuntimeException("Erreur findById Role", e);
        }
        return null;
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role";
        List<Role> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Role", e);
        }
        return list;
    }

    @Override
    public void create(Role role) {
        // CORRECTION : Suppression de la colonne 'actif'
        String sql = """
            INSERT INTO role (libelle, privileges, date_creation, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Enum -> String
            ps.setString(1, role.getLibelle() != null ? role.getLibelle().name() : null);

            // 2. List<String> -> String
            String privStr = "";
            if (role.getPrivilege() != null && !role.getPrivilege().isEmpty()) {
                privStr = String.join(",", role.getPrivilege());
            }
            ps.setString(2, privStr);

            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, role.getCreePar());
            ps.setString(5, role.getModifiePar());

            // On a supprimé le ps.setBoolean(6, role.isActif()) car la table Role n'a pas de colonne actif

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) role.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create Role", e);
        }
    }

    @Override
    public void update(Role role) {
        String sql = """
            UPDATE role 
            SET libelle = ?, privileges = ?, date_modification = ?, modifie_par = ?
            WHERE id = ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.getLibelle().name());

            String privStr = "";
            if (role.getPrivilege() != null && !role.getPrivilege().isEmpty()) {
                privStr = String.join(",", role.getPrivilege());
            }
            ps.setString(2, privStr);

            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, role.getModifiePar());
            ps.setLong(5, role.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Role", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Role", e);
        }
    }

    @Override
    public void delete(Role role) {
        if (role != null && role.getId() != null) {
            deleteById(role.getId());
        }
    }
}