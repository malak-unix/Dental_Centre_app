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

    @Override public Optional<Utilisateur> findByEmail(String email) { return Optional.empty(); }

    @Override
    public void updatePassword(Long userId, String newEncodedPassword) {

    }

    @Override public boolean existsByEmail(String email) { return false; }
    @Override public boolean existsByLogin(String login) { return false; }
    @Override public List<Utilisateur> searchByNom(String keyword) { return new ArrayList<>(); }
    @Override public List<Utilisateur> findPage(int limit, int offset) { return new ArrayList<>(); }
    @Override public List<String> getRoleLibellesOfUser(Long utilisateurId) { return new ArrayList<>(); }
    @Override public void addRoleToUser(Long utilisateurId, Long roleId) {}
    @Override public void removeRoleFromUser(Long utilisateurId, Long roleId) {}
}