package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import java.sql.*;
import java.util.*;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    private final Connection connection;

    public UtilisateurRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

     @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retourne null si non trouvé, conformément à l'interface sans Optional
    }

    @Override
    public void create(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, login, mot_de_passe, actif) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getLogin());
            ps.setString(5, u.getMotDePasse());
            ps.setBoolean(6, u.isActif());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                u.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Utilisateur u) {
        if (u != null && u.getId() != null) {
            deleteById(u.getId());
        }
    }

    @Override
    public void update(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, actif=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setBoolean(4, u.isActif());
            ps.setLong(5, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> users = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM utilisateur")) {
            while (rs.next()) {
                users.add(mapResultSetToUtilisateur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
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

    // --- MÉTHODES SPÉCIFIQUES (Utiliser Optional seulement si UtilisateurRepository le demande) ---
    @Override public Optional<Utilisateur> findByLogin(String login) {
        Utilisateur u = null;
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) u = mapResultSetToUtilisateur(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.ofNullable(u);
    }
//fait par AYA
    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void updatePassword(Long userId, String newEncodedPassword) {
        if (userId == null || newEncodedPassword == null) return;

        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newEncodedPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        String sql = "SELECT 1 FROM utilisateur WHERE email = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsByLogin(String login) {
        if (login == null || login.isBlank()) return false;

        String sql = "SELECT 1 FROM utilisateur WHERE login = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Utilisateur> searchByNom(String keyword) {
        List<Utilisateur> list = new ArrayList<>();
        if (keyword == null) keyword = "";
        String k = "%" + keyword.trim() + "%";

        String sql = "SELECT * FROM utilisateur WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Utilisateur> findPage(int limit, int offset) {
        List<Utilisateur> list = new ArrayList<>();
        int l = Math.max(1, limit);
        int o = Math.max(0, offset);

        String sql = "SELECT * FROM utilisateur ORDER BY id DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, l);
            ps.setInt(2, o);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String lib = rs.getString("libelle");
                    if (lib != null) roles.add(lib);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public void addRoleToUser(Long utilisateurId, Long roleId) {
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
}