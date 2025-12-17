package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.medecin.Medecin;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.LibelleRole; // Pour chercher le role MEDECIN
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    // --- MAPPING (Triple Jointure : Utilisateur + Staff + Medecin) ---
    private Medecin map(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        // 1. UTILISATEUR (Champs de base)
        m.setId(rs.getLong("id")); // ID commun aux 3 tables
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setAdresse(rs.getString("adresse"));
        m.setTel(rs.getString("tel"));
        m.setCin(rs.getString("cin"));
        m.setLogin(rs.getString("login"));
        m.setMotDePass_hash(rs.getString("mot_de_passe"));

        // Gestion Sexe
        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try { m.setSexe(Sexe.valueOf(sexeStr)); } catch (Exception e) {}
        }

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) m.setDateNaissance(dateN.toLocalDate());

        // 2. STAFF (Salaire, Prime...)
        m.setSalaire(rs.getDouble("salaire"));
        m.setPrime(rs.getDouble("prime"));
        Date dateRecrut = rs.getDate("date_recrutement");
        if(dateRecrut != null) m.setDateRecrutement(dateRecrut.toLocalDate());
        m.setSoldeConge(rs.getInt("solde_conge"));

        // 3. MEDECIN (Spécialité)
        m.setSpecialite(rs.getString("specialite"));

        // Traçabilité (Table Medecin ou Utilisateur selon besoin, ici on prend Medecin)
        m.setCreePar(rs.getString("cree_par"));
        m.setModifiePar(rs.getString("modifie_par"));

        return m;
    }

    // Helper pour avoir l'ID du role MEDECIN
    private Long getRoleId(Connection conn) throws SQLException {
        String sql = "SELECT id FROM role WHERE libelle = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LibelleRole.MEDECIN.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    @Override
    public void create(Medecin m) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false); // <--- TRANSACTION OBLIGATOIRE

            // A. Récupérer ID du Role MEDECIN
            Long roleId = getRoleId(conn);
            if (roleId == null) throw new SQLException("Role MEDECIN introuvable en base");

            // B. INSERT TABLE 1 : UTILISATEUR
            String sqlUser = """
                INSERT INTO utilisateur 
                (nom, prenom, email, login, mot_de_passe, role_id, adresse, tel, cin, sexe, date_naissance, date_creation, cree_par, actif) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

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
                ps.setBoolean(14, m.isActif()); // true par défaut

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) generatedId = rs.getLong(1);
                }
            }

            if (generatedId == null) throw new SQLException("Echec insert Utilisateur (pas d'ID généré)");
            m.setId(generatedId); // On met à jour l'objet Java

            // C. INSERT TABLE 2 : STAFF
            String sqlStaff = """
                INSERT INTO staff (id, salaire, prime, date_recrutement, solde_conge, cree_par) 
                VALUES (?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, generatedId);
                ps.setDouble(2, m.getSalaire());
                ps.setDouble(3, m.getPrime());
                ps.setDate(4, m.getDateRecrutement() != null ? Date.valueOf(m.getDateRecrutement()) : null);
                ps.setInt(5, m.getSoldeConge());
                ps.setString(6, m.getCreePar());
                ps.executeUpdate();
            }

            // D. INSERT TABLE 3 : MEDECIN
            String sqlMedecin = "INSERT INTO medecin (id, specialite, cree_par) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlMedecin)) {
                ps.setLong(1, generatedId);
                ps.setString(2, m.getSpecialite());
                ps.setString(3, m.getCreePar());
                ps.executeUpdate();
            }

            conn.commit(); // <--- VALIDATION FINALE

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {} // Annuler si erreur
            throw new RuntimeException("Erreur création Médecin", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public List<Medecin> findAll() {
        // TRIPLE JOINTURE pour tout récupérer
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, m.specialite, m.cree_par as m_cree 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
            JOIN medecin m ON s.id = m.id
        """;
        List<Medecin> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Medecin", e);
        }
        return list;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, m.specialite 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
            JOIN medecin m ON s.id = m.id
            WHERE u.id = ?
        """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Medecin", e);
        }
        return null;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge, m.specialite 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
            JOIN medecin m ON s.id = m.id
            WHERE m.specialite LIKE ?
        """;
        List<Medecin> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + specialite + "%"); // Recherche approximative
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findBySpecialite", e);
        }
        return list;
    }

    @Override
    public void update(Medecin m) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE UTILISATEUR
            String sqlUser = "UPDATE utilisateur SET nom=?, prenom=?, email=?, tel=?, adresse=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlUser)){
                ps.setString(1, m.getNom());
                ps.setString(2, m.getPrenom());
                ps.setString(3, m.getEmail());
                ps.setString(4, m.getTel());
                ps.setString(5, m.getAdresse());
                ps.setLong(6, m.getId());
                ps.executeUpdate();
            }

            // 2. UPDATE STAFF
            String sqlStaff = "UPDATE staff SET salaire=?, prime=?, solde_conge=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlStaff)){
                ps.setDouble(1, m.getSalaire());
                ps.setDouble(2, m.getPrime());
                ps.setInt(3, m.getSoldeConge());
                ps.setLong(4, m.getId());
                ps.executeUpdate();
            }

            // 3. UPDATE MEDECIN
            String sqlMedecin = "UPDATE medecin SET specialite=?, modifie_par=?, date_modification=NOW() WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlMedecin)){
                ps.setString(1, m.getSpecialite());
                ps.setString(2, m.getModifiePar());
                ps.setLong(3, m.getId());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException("Erreur update Medecin", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public void deleteById(Long id) {
        // Le "ON DELETE CASCADE" dans votre SQL va supprimer Staff et Medecin automatiquement
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Medecin", e);
        }
    }

    @Override public void delete(Medecin m) { if(m != null) deleteById(m.getId()); }
}