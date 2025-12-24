package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.secretaire.Secretaire;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // --- MAPPING ---
    private Secretaire map(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();
        // 1. UTILISATEUR
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setTel(rs.getString("tel"));
        s.setCin(rs.getString("cin"));
        s.setLogin(rs.getString("login"));
        s.setMotDePass_hash(rs.getString("mot_de_passe"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) { try { s.setSexe(Sexe.valueOf(sexeStr)); } catch (Exception e) {} }
        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) s.setDateNaissance(dateN.toLocalDate());

        // 2. STAFF
        s.setSalaire(rs.getDouble("salaire"));
        try { s.setPrime(rs.getDouble("prime")); } catch (Exception e) {}

        s.setCreePar(rs.getString("cree_par"));
        return s;
    }

    // --- CREATE ---
    @Override
    public void create(Secretaire s) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            Long roleId = null;
            try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM role WHERE libelle = ?")){
                ps.setString(1, LibelleRole.SECRETAIRE.name());
                ResultSet rs = ps.executeQuery();
                if(rs.next()) roleId = rs.getLong("id");
            }
            if(roleId == null) throw new SQLException("Role SECRETAIRE introuvable");

            String sqlUser = "INSERT INTO utilisateur (nom, prenom, email, login, mot_de_passe, role_id, adresse, tel, cin, sexe, date_naissance, date_creation, cree_par, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Long generatedId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getLogin());
                ps.setString(5, s.getMotDePass_hash());
                ps.setLong(6, roleId);
                ps.setString(7, s.getAdresse());
                ps.setString(8, s.getTel());
                ps.setString(9, s.getCin());
                ps.setString(10, s.getSexe() != null ? s.getSexe().name() : null);
                ps.setDate(11, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
                ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(13, s.getCreePar());
                ps.setBoolean(14, s.isActif());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) generatedId = rs.getLong(1);
            }
            s.setId(generatedId);

            String sqlStaff = "INSERT INTO staff (id, salaire, prime, cree_par) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, generatedId);
                ps.setObject(2, s.getSalaire());
                ps.setObject(3, s.getPrime());
                ps.setString(4, s.getCreePar());
                ps.executeUpdate();
            }

            // Pour Secretaire, on insère juste l'ID car pas de champs spécifiques
            String sqlSec = "INSERT INTO secretaire (id, cree_par) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSec)) {
                ps.setLong(1, generatedId);
                ps.setString(2, s.getCreePar());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public List<Secretaire> findAll() {
        String sql = "SELECT u.*, s.salaire, s.prime, sec.cree_par as sec_cree FROM utilisateur u JOIN staff s ON u.id = s.id JOIN secretaire sec ON s.id = sec.id";
        List<Secretaire> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // --- MÉTHODES VIDES POUR SATISFAIRE L'INTERFACE ---

    @Override
    public Secretaire findById(Long id) { return null; }

    @Override
    public void update(Secretaire s) {}

    @Override
    public void deleteById(Long id) {}

    @Override
    public void delete(Secretaire s) {}

    // C'EST CETTE MÉTHODE QUI MANQUAIT :
    @Override
    public Secretaire findByNumCNSS(String numCNSS) {
        // Le champ n'existe pas, on retourne null pour que ça compile
        return null;
    }
}