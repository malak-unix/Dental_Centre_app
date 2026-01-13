package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Role> findByType(LibelleRole type) {
        if (type == null) return Optional.empty();
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) return Optional.empty();
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libelle.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) return false;
        String sql = "SELECT 1 FROM role WHERE libelle = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libelle.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> getPrivileges(Long roleId) {
        if (roleId == null) return List.of();
        String sql = "SELECT privileges FROM role WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String csv = rs.getString("privileges");
                    if (csv == null || csv.isBlank()) return List.of();
                    return Arrays.stream(csv.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .distinct()
                            .collect(Collectors.toList());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void addPrivilege(Long roleId, String privilege) {
        if (roleId == null || privilege == null || privilege.isBlank()) return;

        List<String> current = new ArrayList<>(getPrivileges(roleId));
        String p = privilege.trim();
        if (!current.contains(p)) current.add(p);

        updatePrivilegesCsv(roleId, current);
    }

    @Override
    public void removePrivilege(Long roleId, String privilege) {
        if (roleId == null || privilege == null || privilege.isBlank()) return;

        List<String> current = new ArrayList<>(getPrivileges(roleId));
        current.removeIf(x -> x.equalsIgnoreCase(privilege.trim()));

        updatePrivilegesCsv(roleId, current);
    }

    private void updatePrivilegesCsv(Long roleId, List<String> privs) {
        String csv = (privs == null || privs.isEmpty())
                ? null
                : privs.stream().map(String::trim).filter(s -> !s.isBlank()).distinct().collect(Collectors.joining(","));

        String sql = "UPDATE role SET privileges = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (csv == null) ps.setNull(1, Types.VARCHAR);
            else ps.setString(1, csv);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        if (utilisateurId == null) return List.of();

        List<Role> roles = new ArrayList<>();
        String sql = """
                SELECT r.*
                FROM role r
                JOIN utilisateur u ON u.role_id = r.id
                WHERE u.id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roles.add(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, Long roleId) {
        if (utilisateurId == null || roleId == null) return;
        String sql = "UPDATE utilisateur SET role_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, Long roleId) {
        if (utilisateurId == null || roleId == null) return;
        String sql = "UPDATE utilisateur SET role_id = NULL WHERE id = ? AND role_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));

        String libStr = rs.getString("libelle");
        if (libStr != null) {
            role.setLibelle(LibelleRole.valueOf(libStr));
        } else {
            role.setLibelle(null);
        }

        role.setPrivileges(rs.getString("privileges")); // CSV brut
        return role;
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM role")) {
            while (rs.next()) roles.add(mapResultSetToRole(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    // CRUD stubs (si tu veux les compléter plus tard)
    @Override public void create(Role role) {}
    @Override public void update(Role role) {}
    @Override public void delete(Role role) {}
    @Override public void deleteById(Long id) {}
}
