package ma.dentalTech.repository.modules.users.impl;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import java.sql.*;
import java.util.*;

public class RoleRepositoryImpl implements RoleRepository {

    private final Connection connection;

    public RoleRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToRole(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- CORRECTION : Cette méthode règle l'erreur "must implement findByType" ---
    @Override
    public Optional<Role> findByType(LibelleRole type) {
        String sql = "SELECT * FROM role WHERE libelle = ?"; // On cherche par le nom du rôle dans la base
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<String> getPrivileges(Long roleId) {
        return List.of();
    }

    @Override
    public void addPrivilege(Long roleId, String privilege) {

    }

    @Override
    public void removePrivilege(Long roleId, String privilege) {

    }

    @Override
    public boolean existsByLibelle(String libelle) {
        return false;
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        return List.of();
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, Long roleId) {

    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, Long roleId) {

    }

    // --- CORRECTION : Si ton interface demande findByLibelle ---
    @Override
    public Optional<Role> findByLibelle(String libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));

        // On remplit tes deux champs Java
        String libStr = rs.getString("libelle");
        role.setLibelle(libStr); // Le String

        try {
            role.setType(LibelleRole.valueOf(libStr)); // L'Enum
        } catch (IllegalArgumentException e) {
            role.setType(null);
        }

        // Conversion String (DB) -> List<String> (Java)
        String privs = rs.getString("privileges");
        if (privs != null) {
            role.setPrivileges(Arrays.asList(privs.split(",")));
        }

        return role;
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM role")) {
            while (rs.next()) roles.add(mapResultSetToRole(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return roles;
    }

    // Stubs obligatoires pour le CRUD
    @Override public void create(Role role) {}
    @Override public void update(Role role) {}
    @Override public void delete(Role role) {}
    @Override public void deleteById(Long id) {}
}