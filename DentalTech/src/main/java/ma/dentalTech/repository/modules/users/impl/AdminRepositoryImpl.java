package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.repository.modules.users.api.AdminRepository;
import java.sql.*;
import java.util.*;

public class AdminRepositoryImpl implements AdminRepository {

    private final Connection connection;

    public AdminRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();

        // --- Données héritées de Utilisateur ---
        admin.setId(rs.getLong("id"));
        admin.setNom(rs.getString("nom"));
        admin.setPrenom(rs.getString("prenom"));
        admin.setEmail(rs.getString("email"));
        admin.setLogin(rs.getString("login"));
        admin.setMotDePasse(rs.getString("mot_de_passe"));
        admin.setActif(rs.getBoolean("actif"));

        return admin;
    }

    @Override
    public Admin findById(Long id) {
        // Simple requête sur la table utilisateur car Admin n'a pas de table de jointure
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToAdmin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> admins = new ArrayList<>();
        // Note: Dans une logique métier, on pourrait filtrer par rôle ici
        String sql = "SELECT * FROM utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
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
    public void delete(Admin admin) {
        if (admin != null && admin.getId() != null) {
            deleteById(admin.getId());
        }
    }

    // --- Stubs pour l'API ---
    @Override public void create(Admin admin) { /* Logique INSERT utilisateur */ }
    @Override public void update(Admin admin) { /* Logique UPDATE utilisateur */ }

    @Override
    public List<Admin> findAllOrderByNom() {
        return List.of();
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return Optional.empty();
    }
}