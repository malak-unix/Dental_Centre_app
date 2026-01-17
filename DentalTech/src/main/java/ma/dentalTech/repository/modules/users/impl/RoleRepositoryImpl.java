package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class RoleRepositoryImpl implements RoleRepository {

    @SuppressWarnings("unused")
    private final Connection connection;

    public RoleRepositoryImpl(Connection connection) {
        this.connection = connection; // not used
    }

    public RoleRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Role findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToRole(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Role) id=" + id, e);
        }
    }

    @Override
    public Optional<Role> findByType(LibelleRole type) {
        if (type == null) return Optional.empty();

        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToRole(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByType(Role) type=" + type, e);
        }
    }

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) return Optional.empty();

        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, libelle.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToRole(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLibelle(Role) libelle=" + libelle, e);
        }
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) return false;

        String sql = "SELECT 1 FROM role WHERE libelle = ? LIMIT 1";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, libelle.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsByLibelle(Role) libelle=" + libelle, e);
        }
    }

    @Override
    public List<String> getPrivileges(Long roleId) {
        if (roleId == null) return List.of();

        String sql = "SELECT privileges FROM role WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return List.of();

                String csv = rs.getString("privileges");
                if (csv == null || csv.isBlank()) return List.of();

                return Arrays.stream(csv.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .distinct()
                        .collect(Collectors.toList());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur getPrivileges(Role) roleId=" + roleId, e);
        }
    }

    @Override
    public void addPrivilege(Long roleId, String privilege) {
        if (roleId == null || privilege == null || privilege.isBlank()) return;

        List<String> current = new ArrayList<>(getPrivileges(roleId));
        String p = privilege.trim();

        if (current.stream().noneMatch(x -> x.equalsIgnoreCase(p))) {
            current.add(p);
        }

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
                : privs.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.joining(","));

        String sql = "UPDATE role SET privileges = ? WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (csv == null) ps.setNull(1, Types.VARCHAR);
            else ps.setString(1, csv);

            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur updatePrivilegesCsv(Role) roleId=" + roleId, e);
        }
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        if (utilisateurId == null) return List.of();

        String sql = """
                SELECT r.*
                FROM role r
                JOIN utilisateur u ON u.role_id = r.id
                WHERE u.id = ?
                """;

        List<Role> roles = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roles.add(mapResultSetToRole(rs));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findRolesByUtilisateurId(Role) userId=" + utilisateurId, e);
        }
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, Long roleId) {
        if (utilisateurId == null || roleId == null) return;

        String sql = "UPDATE utilisateur SET role_id = ? WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur assignRoleToUser userId=" + utilisateurId + " roleId=" + roleId, e);
        }
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, Long roleId) {
        if (utilisateurId == null || roleId == null) return;

        String sql = "UPDATE utilisateur SET role_id = NULL WHERE id = ? AND role_id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeRoleFromUser userId=" + utilisateurId + " roleId=" + roleId, e);
        }
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role";
        List<Role> roles = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) roles.add(mapResultSetToRole(rs));
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Role)", e);
        }
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));

        String libStr = rs.getString("libelle");
        role.setLibelle(libStr != null ? LibelleRole.valueOf(libStr) : null);

        role.setPrivileges(rs.getString("privileges"));
        return role;
    }

    @Override
    public void create(Role role) {
        if (role == null || role.getLibelle() == null) return;

        String sql = "INSERT INTO role (libelle, privileges, cree_par, modifie_par) VALUES (?, ?, ?, ?)";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, role.getLibelle().name());
            ps.setString(2, role.getPrivileges());
            ps.setString(3, role.getCreePar());
            ps.setString(4, role.getModifiePar());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) role.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur create(Role)", e);
        }
    }

    @Override
    public void update(Role role) {
        if (role == null || role.getId() == null) return;

        String sql = "UPDATE role SET libelle = ?, privileges = ?, modifie_par = ? WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, role.getLibelle() != null ? role.getLibelle().name() : null);
            ps.setString(2, role.getPrivileges());
            ps.setString(3, role.getModifiePar());
            ps.setLong(4, role.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update(Role) id=" + role.getId(), e);
        }
    }

    @Override
    public void delete(Role role) {
        if (role == null || role.getId() == null) return;
        deleteById(role.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Role) id=" + id, e);
        }
    }
}
