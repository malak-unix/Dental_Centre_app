package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.admin.Admin;
import ma.dentalTech.entities.enums.LibelleRole; // Assurez-vous d'avoir cet Enum
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminRepositoryImpl implements AdminRepository {

    // Helper pour convertir ResultSet -> Admin
    private Admin map(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        // Mappage des champs hérités de Utilisateur
        a.setId(rs.getLong("id"));
        a.setNom(rs.getString("nom"));
        a.setPrenom(rs.getString("prenom"));
        a.setEmail(rs.getString("email"));
        a.setLogin(rs.getString("login"));
        a.setMotDePass_hash(rs.getString("mot_de_passe"));
        a.setActif(rs.getBoolean("actif"));
        a.setAdresse(rs.getString("adresse"));
        a.setTel(rs.getString("tel"));
        a.setCin(rs.getString("cin"));

        // Gestion Sexe
        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try { a.setSexe(Sexe.valueOf(sexeStr)); } catch (Exception e) {}
        }

        // Dates
        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) a.setDateNaissance(dateN.toLocalDate());

        // On ne set pas le Role ici car votre entité Admin/Utilisateur n'a pas le champ

        return a;
    }

    @Override
    public void create(Admin a) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();

            // 1. Récupérer l'ID du rôle ADMIN depuis la BDD
            Long roleId = null;
            // Adaptez 'ADMIN' si votre enum en base est différent
            String sqlRole = "SELECT id FROM role WHERE libelle = 'ADMIN'";

            try (PreparedStatement psRole = conn.prepareStatement(sqlRole);
                 ResultSet rsRole = psRole.executeQuery()) {
                if (rsRole.next()) {
                    roleId = rsRole.getLong("id");
                }
            }

            if (roleId == null) {
                throw new RuntimeException("Impossible de créer l'Admin : Le rôle 'ADMIN' n'existe pas dans la table 'role'.");
            }

            // 2. Insérer l'utilisateur en forçant le role_id trouvé
            String sqlInsert = """
                INSERT INTO utilisateur 
                (nom, prenom, email, login, mot_de_passe, role_id, actif, date_creation, cree_par, adresse, tel, cin, sexe) 
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, a.getNom());
                ps.setString(2, a.getPrenom());
                ps.setString(3, a.getEmail());
                ps.setString(4, a.getLogin());
                ps.setString(5, a.getMotDePass_hash());
                ps.setLong(6, roleId); // <--- C'est ici qu'on lie l'Admin à son rôle
                ps.setBoolean(7, a.isActif());
                ps.setString(8, a.getCreePar());
                ps.setString(9, a.getAdresse());
                ps.setString(10, a.getTel());
                ps.setString(11, a.getCin());
                ps.setString(12, a.getSexe() != null ? a.getSexe().name() : null);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) a.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'Admin", e);
        }
    }

    @Override
    public List<Admin> findAll() {
        // On sélectionne uniquement les utilisateurs qui ont le rôle ADMIN via une jointure
        String sql = """
            SELECT u.* FROM utilisateur u
            JOIN role r ON u.role_id = r.id
            WHERE r.libelle = 'ADMIN'
        """;
        List<Admin> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Admin", e);
        }
        return list;
    }

    @Override
    public Admin findById(Long id) {
        // On vérifie aussi le rôle lors de la recherche par ID
        String sql = """
            SELECT u.* FROM utilisateur u
            JOIN role r ON u.role_id = r.id
            WHERE u.id = ? AND r.libelle = 'ADMIN'
        """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Admin", e);
        }
        return null;
    }

    @Override
    public void update(Admin a) {
        // Mise à jour classique dans la table utilisateur
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=?, tel=?, adresse=?, cin=?, actif=? WHERE id=?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getEmail());
            ps.setString(4, a.getTel());
            ps.setString(5, a.getAdresse());
            ps.setString(6, a.getCin());
            ps.setBoolean(7, a.isActif());
            ps.setLong(8, a.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Admin", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // Suppression classique
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Admin", e);
        }
    }

    @Override
    public void delete(Admin a) {
        if (a != null) deleteById(a.getId());
    }
}