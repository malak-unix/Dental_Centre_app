package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.medecin.Medecin;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    // --- MAPPING (Lecture des données) ---
    private Medecin map(ResultSet rs) throws SQLException {
        // On utilise le constructeur vide ou le builder, ici setters classiques pour être sûr
        Medecin m = new Medecin();

        // 1. UTILISATEUR (Champs communs)
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setAdresse(rs.getString("adresse"));
        m.setTel(rs.getString("tel"));
        m.setCin(rs.getString("cin"));
        m.setLogin(rs.getString("login"));
        m.setMotDePass_hash(rs.getString("mot_de_passe"));

        // Sexe
        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try { m.setSexe(Sexe.valueOf(sexeStr)); } catch (Exception e) {}
        }

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) m.setDateNaissance(dateN.toLocalDate());

        // 2. STAFF (Salaire uniquement, PAS de congés ni date recrutement)
        m.setSalaire(rs.getDouble("salaire"));
        // Si 'prime' existe dans Staff, garde cette ligne. Sinon, supprime-la.
        try { m.setPrime(rs.getDouble("prime")); } catch (Exception e) { /* Ignore si pas de colonne */ }

        // 3. MEDECIN (Tes champs spécifiques)
        m.setSpecialite(rs.getString("specialite"));
        m.setPourcentage(rs.getDouble("pourcentage"));

        m.setCreePar(rs.getString("cree_par"));
        m.setModifiePar(rs.getString("modifie_par"));

        return m;
    }

    // --- INSERTION (CREATE) ---
    @Override
    public void create(Medecin m) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false); // Transaction

            // 1. Récupérer ID Role MEDECIN
            Long roleId = null;
            try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM role WHERE libelle = ?")){
                ps.setString(1, LibelleRole.MEDECIN.name());
                ResultSet rs = ps.executeQuery();
                if(rs.next()) roleId = rs.getLong("id");
            }
            if (roleId == null) throw new SQLException("Role MEDECIN introuvable");

            // 2. Insert UTILISATEUR
            String sqlUser = "INSERT INTO utilisateur (nom, prenom, email, login, mot_de_passe, role_id, adresse, tel, cin, sexe, date_naissance, date_creation, cree_par, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Long generatedId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, m.getNom());
                ps.setString(2, m.getPrenom());
                ps.setString(3, m.getEmail());
                ps.setString(4, m.getLogin());
                ps.setString(5, m.getMotDePass_hash());
                ps.setLong(6, roleId);
                ps.setString(7, m.getAdresse());
                ps.setString(8, m.getTel());
                ps.setString(9, m.getCin());
                ps.setString(10, m.getSexe() != null ? m.getSexe().name() : null);
                ps.setDate(11, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);
                ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(13, m.getCreePar());
                ps.setBoolean(14, m.isActif());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) generatedId = rs.getLong(1);
            }
            m.setId(generatedId);

            // 3. Insert STAFF (Juste Salaire et Prime)
            // J'ai enlevé solde_conge et date_recrutement
            String sqlStaff = "INSERT INTO staff (id, salaire, prime, cree_par) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, generatedId);
                ps.setObject(2, m.getSalaire());
                ps.setObject(3, m.getPrime()); // Si erreur ici, supprime cette ligne
                ps.setString(4, m.getCreePar());
                ps.executeUpdate();
            }

            // 4. Insert MEDECIN (Specialite et Pourcentage)
            String sqlMedecin = "INSERT INTO medecin (id, specialite, pourcentage, cree_par) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlMedecin)) {
                ps.setLong(1, generatedId);
                ps.setString(2, m.getSpecialite());
                ps.setObject(3, m.getPourcentage());
                ps.setString(4, m.getCreePar());
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

    // --- LECTURE (FIND ALL) ---
    @Override
    public List<Medecin> findAll() {
        String sql = "SELECT u.*, s.salaire, s.prime, m.specialite, m.pourcentage, m.cree_par as m_cree " +
                "FROM utilisateur u " +
                "JOIN staff s ON u.id = s.id " +
                "JOIN medecin m ON s.id = m.id";
        List<Medecin> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // --- AUTRES MÉTHODES ---
    @Override
    public Medecin findById(Long id) {
        String sql = "SELECT u.*, s.salaire, s.prime, m.specialite, m.pourcentage FROM utilisateur u JOIN staff s ON u.id = s.id JOIN medecin m ON s.id = m.id WHERE u.id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM utilisateur WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // Ajoute les autres méthodes (findBySpecialite, update...) si ton interface les demande,
    // mais copie d'abord ça pour corriger l'erreur bloquante.
    @Override public List<Medecin> findBySpecialite(String s) { return new ArrayList<>(); } // Vide pour l'instant
    @Override public void update(Medecin m) {} // Vide pour l'instant
    @Override public void delete(Medecin m) {}
}