package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.sql.Date; // Important pour distinguer de java.util.Date
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();

        // BaseEntity
        Long id = rs.getLong("id");
        if (!rs.wasNull()) u.setId(id);

        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) u.setDateCreation(dateCrea.toLocalDateTime());

        // CORRECTION ICI : setDateDerniereModification
        Timestamp dateModif = rs.getTimestamp("date_modification");
        if (dateModif != null) u.setDateDerniereModification(dateModif.toLocalDateTime());

        u.setCreePar(rs.getString("cree_par"));
        u.setModifiePar(rs.getString("modifie_par"));

        // Utilisateur
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setAdresse(rs.getString("adresse"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));
        u.setLogin(rs.getString("login"));
        u.setMotDePass_hash(rs.getString("mot_de_passe_hash"));

        u.setActif(rs.getBoolean("actif"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try {
                u.setSexe(Sexe.valueOf(sexeStr));
            } catch (IllegalArgumentException e) {
                u.setSexe(null);
            }
        }

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) u.setDateNaissance(dateN.toLocalDate());

        Date lastLogin = rs.getDate("last_login_date");
        if (lastLogin != null) u.setLastLoginDate(lastLogin.toLocalDate());

        return u;
    }
// =========================================================================
    // Méthodes manquantes de l'interface
    // =========================================================================

    @Override
    public Utilisateur findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche utilisateur par login=" + login, e);
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
                if (rs.next()) {
                    // Si le compte est > 0, l'email existe déjà
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur vérification existence email=" + email, e);
        }
        return false;
    }
    @Override
    public void create(Utilisateur u) {
        String sql = """
                INSERT INTO utilisateur
                (nom, prenom, email, adresse, cin, tel, sexe, login, mot_de_passe_hash,
                 last_login_date, date_naissance, actif, date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getCin());
            ps.setString(6, u.getTel());

            if (u.getSexe() != null) {
                ps.setString(7, u.getSexe().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.setString(8, u.getLogin());
            ps.setString(9, u.getMotDePass_hash());

            if (u.getLastLoginDate() != null) {
                ps.setDate(10, Date.valueOf(u.getLastLoginDate()));
            } else {
                ps.setNull(10, Types.DATE);
            }
            if (u.getDateNaissance() != null) {
                ps.setDate(11, Date.valueOf(u.getDateNaissance()));
            } else {
                ps.setNull(11, Types.DATE);
            }

            ps.setBoolean(12, u.isActif());

            ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(14, u.getCreePar());
            ps.setString(15, u.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création utilisateur", e);
        }
    }

    @Override
    public void update(Utilisateur u) {
        String sql = """
                UPDATE utilisateur
                   SET nom = ?, prenom = ?, email = ?, adresse = ?, cin = ?, tel = ?, sexe = ?,
                       login = ?, mot_de_passe_hash = ?, last_login_date = ?, date_naissance = ?, actif = ?,
                       date_modification = ?, modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getCin());
            ps.setString(6, u.getTel());

            if (u.getSexe() != null) {
                ps.setString(7, u.getSexe().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.setString(8, u.getLogin());
            ps.setString(9, u.getMotDePass_hash());

            if (u.getLastLoginDate() != null) {
                ps.setDate(10, Date.valueOf(u.getLastLoginDate()));
            } else {
                ps.setNull(10, Types.DATE);
            }
            if (u.getDateNaissance() != null) {
                ps.setDate(11, Date.valueOf(u.getDateNaissance()));
            } else {
                ps.setNull(11, Types.DATE);
            }

            ps.setBoolean(12, u.isActif());

            ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(14, u.getModifiePar());

            ps.setLong(15, u.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update utilisateur id=" + u.getId(), e);
        }
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche utilisateur id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM utilisateur ORDER BY nom";
        List<Utilisateur> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur liste utilisateurs", e);
        }
        return list;
    }

    @Override
    public void delete(Utilisateur u) {
        if (u != null && u.getId() != null) deleteById(u.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression utilisateur id=" + id, e);
        }
    }
}