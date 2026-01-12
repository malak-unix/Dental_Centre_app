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
            throw new RuntimeException("Erreur findById(role) id=" + id, e);
        }
        return null;
    }

    // Si ton interface expose findByType(LibelleRole)
    @Override
    public Optional<Role> findByType(LibelleRole type) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByType(role) type=" + type, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLibelle(role) libelle=" + libelle, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        String sql = "SELECT 1 FROM role WHERE libelle = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsByLibelle(role) libelle=" + libelle, e);
        }
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        String sql = """
                SELECT r.*
                FROM utilisateur u
                JOIN role r ON r.id = u.role_id
                WHERE u.id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Role> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(mapResultSetToRole(rs));
                }
                return roles;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findRolesByUtilisateurId utilisateurId=" + utilisateurId, e);
        }
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, Long roleId) {
        String sql = "UPDATE utilisateur SET role_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur assignRoleToUser roleId=" + roleId + " userId=" + utilisateurId, e);
        }
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, Long roleId) {
        String sql = "UPDATE utilisateur SET role_id = NULL WHERE id = ? AND role_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeRoleFromUser roleId=" + roleId + " userId=" + utilisateurId, e);
        }
    }

    /**
     * Retourne les privilèges sous forme de List<String> (parsing CSV).
     * DB: role.privileges = "A,B,C"
     */
    @Override
    public List<String> getPrivileges(Long roleId) {
        String sql = "SELECT privileges FROM role WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return List.of();
                String privs = rs.getString("privileges");
                if (privs == null || privs.isBlank()) return List.of();

                return Arrays.stream(privs.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toList();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur getPrivileges(role) roleId=" + roleId, e);
        }
    }

    /**
     * Ajoute un privilège dans la colonne CSV "privileges" si absent.
     */
    @Override
    public void addPrivilege(Long roleId, String privilege) {
        if (privilege == null || privilege.isBlank()) return;

        List<String> current = new ArrayList<>(getPrivileges(roleId));
        String p = privilege.trim();

        if (current.stream().anyMatch(x -> x.equalsIgnoreCase(p))) return;
        current.add(p);

        String updated = current.stream().collect(Collectors.joining(","));
        updatePrivileges(roleId, updated);
    }

    /**
     * Supprime un privilège dans la colonne CSV "privileges" si présent.
     */
    @Override
    public void removePrivilege(Long roleId, String privilege) {
        if (privilege == null || privilege.isBlank()) return;

        String p = privilege.trim();
        List<String> current = new ArrayList<>(getPrivileges(roleId));

        boolean changed = current.removeIf(x -> x.equalsIgnoreCase(p));
        if (!changed) return;

        String updated = current.stream().collect(Collectors.joining(","));
        updatePrivileges(roleId, updated);
    }

    private void updatePrivileges(Long roleId, String privilegesCsv) {
        String sql = "UPDATE role SET privileges = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, (privilegesCsv == null || privilegesCsv.isBlank()) ? null : privilegesCsv);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updatePrivileges(role) roleId=" + roleId, e);
        }
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));

        // ✅ DB -> Enum
        String libStr = rs.getString("libelle");
        if (libStr != null) {
            try {
                role.setLibelle(LibelleRole.valueOf(libStr));
            } catch (IllegalArgumentException ex) {
                role.setLibelle(null);
            }
        } else {
            role.setLibelle(null);
        }

        // ✅ DB privileges CSV string (ON GARDE STRING dans l'entité)
        role.setPrivileges(rs.getString("privileges"));

        return role;
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM role")) {
            while (rs.next()) roles.add(mapResultSetToRole(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(role)", e);
        }
        return roles;
    }

    // Stubs CRUD (si ton interface les exige)
    @Override public void create(Role role) { }
    @Override public void update(Role role) { }
    @Override public void delete(Role role) { }
    @Override public void deleteById(Long id) { }
}
