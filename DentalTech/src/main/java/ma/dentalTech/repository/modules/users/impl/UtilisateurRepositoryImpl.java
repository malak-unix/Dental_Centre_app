package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    protected Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setAdresse(rs.getString("adresse"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));
        u.setLogin(rs.getString("login"));
        u.setMotDePass_hash(rs.getString("mot_de_passe"));
        u.setActif(rs.getBoolean("actif"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try { u.setSexe(Sexe.valueOf(sexeStr)); } catch (Exception e) {}
        }

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) u.setDateNaissance(dateN.toLocalDate());

        Date lastLogin = rs.getDate("last_login");
        if (lastLogin != null) u.setLastLoginDate(lastLogin.toLocalDate());

        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) u.setDateCreation(dateCrea.toLocalDateTime());

        // --- MODIFICATION : On a supprimé la gestion de u.setRole(...) car le champ n'existe pas ---
        // Si vous avez besoin de l'ID du role, on supposera qu'il n'est pas mappé dans l'entité de base.

        return u;
    }

    @Override
    public Utilisateur findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLogin", e);
        }
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur existsByEmail", e);
        }
        return false;
    }

    @Override
    public void create(Utilisateur u) {
        String sql = """
            INSERT INTO utilisateur 
            (nom, prenom, email, login, mot_de_passe, adresse, tel, cin, sexe, actif, date_creation) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """; // J'ai retiré role_id de l'insert car on ne l'a pas dans l'objet u

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getLogin());
            ps.setString(5, u.getMotDePass_hash());
            ps.setString(6, u.getAdresse());
            ps.setString(7, u.getTel());
            ps.setString(8, u.getCin());
            ps.setString(9, u.getSexe() != null ? u.getSexe().name() : null);

            // --- MODIFICATION : Suppression de u.getRole() ---
            // Le role sera null par défaut (ou géré par la base)

            ps.setBoolean(10, u.isActif());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur create Utilisateur", e);
        }
    }

    // Le reste des méthodes standard...
    @Override
    public void update(Utilisateur u) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, tel=?, adresse=?, email=?, actif=? WHERE id=?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getTel());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getEmail());
            ps.setBoolean(6, u.isActif());
            ps.setLong(7, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void deleteById(Long id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try(Connection c = SessionFactory.getInstance().getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setLong(1, id); ps.executeUpdate();
        } catch(SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void delete(Utilisateur u) { if(u!=null) deleteById(u.getId()); }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur";
        List<Utilisateur> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}