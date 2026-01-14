package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.util.*;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    // ✅ Gardé pour compatibilité avec ApplicationContext/RepoFactory
    @SuppressWarnings("unused")
    private final Connection connection;

    public UtilisateurRepositoryImpl(Connection connection) {
        this.connection = connection; // pas utilisé
    }

    public UtilisateurRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Utilisateur findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Utilisateur) id=" + id, e);
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur";
        List<Utilisateur> users = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) users.add(map(rs));
            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Utilisateur)", e);
        }
    }

    @Override
    public void create(Utilisateur u) {
        if (u == null) return;

        String sql = "INSERT INTO utilisateur (nom, prenom, email, login, mot_de_passe, actif) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getLogin());
            ps.setString(5, u.getMotDePasse());
            ps.setBoolean(6, u.isActif());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create(Utilisateur)", e);
        }
    }

    @Override
    public void update(Utilisateur u) {
        if (u == null || u.getId() == null) return;

        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, actif=? WHERE id=?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setBoolean(4, u.isActif());
            ps.setLong(5, u.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update(Utilisateur) id=" + u.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Utilisateur) id=" + id, e);
        }
    }

    @Override
    public void delete(Utilisateur u) {
        if (u != null && u.getId() != null) deleteById(u.getId());
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        if (login == null || login.isBlank()) return Optional.empty();

        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLogin(Utilisateur)", e);
        }
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail(Utilisateur)", e);
        }
    }

    @Override
    public void updatePassword(Long userId, String newEncodedPassword) {
        if (userId == null || newEncodedPassword == null) return;

        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, newEncodedPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updatePassword(Utilisateur)", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        String sql = "SELECT 1 FROM utilisateur WHERE email = ? LIMIT 1";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsByEmail(Utilisateur)", e);
        }
    }

    @Override
    public boolean existsByLogin(String login) {
        if (login == null || login.isBlank()) return false;

        String sql = "SELECT 1 FROM utilisateur WHERE login = ? LIMIT 1";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsByLogin(Utilisateur)", e);
        }
    }

    @Override
    public List<Utilisateur> searchByNom(String keyword) {
        String k = (keyword == null) ? "" : keyword.trim();
        String like = "%" + k + "%";

        String sql = "SELECT * FROM utilisateur WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        List<Utilisateur> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur searchByNom(Utilisateur)", e);
        }
    }

    @Override
    public List<Utilisateur> findPage(int limit, int offset) {
        int l = Math.max(1, limit);
        int o = Math.max(0, offset);

        String sql = "SELECT * FROM utilisateur ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Utilisateur> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, l);
            ps.setInt(2, o);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findPage(Utilisateur)", e);
        }
    }

    @Override
    public List<String> getRoleLibellesOfUser(Long utilisateurId) {
        if (utilisateurId == null) return new ArrayList<>();

        String sql = """
            SELECT r.libelle
            FROM utilisateur u
            JOIN role r ON r.id = u.role_id
            WHERE u.id = ?
            """;

        List<String> roles = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String lib = rs.getString("libelle");
                    if (lib != null) roles.add(lib);
                }
            }
            return roles;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur getRoleLibellesOfUser(Utilisateur)", e);
        }
    }

    @Override
    public void addRoleToUser(Long utilisateurId, Long roleId) {
        if (utilisateurId == null || roleId == null) return;

        String sql = "UPDATE utilisateur SET role_id = ? WHERE id = ?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur addRoleToUser(Utilisateur)", e);
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
            throw new RuntimeException("Erreur removeRoleFromUser(Utilisateur)", e);
        }
    }

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        u.setActif(rs.getBoolean("actif"));
        return u;
    }
}
