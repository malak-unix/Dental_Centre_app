package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.util.*;

public class AdminRepositoryImpl implements AdminRepository {

    @SuppressWarnings("unused")
    private final Connection connection;

    public AdminRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public AdminRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Admin findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT u.*
            FROM utilisateur u
            JOIN role r ON r.id = u.role_id
            WHERE u.id = ? AND r.libelle = 'ADMIN'
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Admin) id=" + id, e);
        }
    }

    @Override
    public List<Admin> findAll() {
        String sql = """
            SELECT u.*
            FROM utilisateur u
            JOIN role r ON r.id = u.role_id
            WHERE r.libelle = 'ADMIN'
            ORDER BY u.nom, u.prenom
        """;

        List<Admin> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Admin)", e);
        }
    }

    @Override public void create(Admin admin) {}
    @Override public void update(Admin admin) {}

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Admin) id=" + id, e);
        }
    }

    @Override
    public void delete(Admin admin) {
        if (admin == null || admin.getId() == null) return;
        deleteById(admin.getId());
    }

    @Override
    public List<Admin> findAllOrderByNom() {
        return findAll();
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();

        String sql = """
            SELECT u.*
            FROM utilisateur u
            JOIN role r ON r.id = u.role_id
            WHERE u.email = ? AND r.libelle = 'ADMIN'
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail(Admin)", e);
        }
    }

    private Admin map(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));
        a.setPrenom(rs.getString("prenom"));
        a.setEmail(rs.getString("email"));
        a.setLogin(rs.getString("login"));
        a.setMotDePasse(rs.getString("mot_de_passe"));
        a.setActif(rs.getBoolean("actif"));
        return a;
    }
}
