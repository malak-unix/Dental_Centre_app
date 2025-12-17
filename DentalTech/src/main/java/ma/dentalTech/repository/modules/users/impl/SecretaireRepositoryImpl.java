package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.secretaire.Secretaire;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // Helper pour récupérer l'ID du role 'SECRETAIRE'
    private Long getRoleId(Connection conn, String libelle) throws SQLException {
        String sql = "SELECT id FROM role WHERE libelle = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null; // ou throw Exception
    }

    // Mappage avec Jointure (Utilisateur + Staff + Secretaire)
    private Secretaire map(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

        // --- Table UTILISATEUR ---
        s.setId(rs.getLong("u_id")); // Alias utilisé dans la requête SQL
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setTel(rs.getString("tel"));
        s.setLogin(rs.getString("login"));
        s.setMotDePass_hash(rs.getString("mot_de_passe")); // Attention nom colonne DB
        s.setActif(rs.getBoolean("actif"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) s.setSexe(Sexe.valueOf(sexeStr));

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) s.setDateNaissance(dateN.toLocalDate());

        // --- Table STAFF ---
        s.setSalaire(rs.getDouble("salaire"));
        s.setPrime(rs.getDouble("prime"));
        // s.setSoldeConge(rs.getInt("solde_conge")); // Si vous avez ce champ dans l'entité

        // --- Table SECRETAIRE ---
        s.setNumCNSS(rs.getString("num_cnss"));
        // s.setCommission(rs.getDouble("commission")); // Si vous avez ce champ

        return s;
    }

    @Override
    public void create(Secretaire s) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            // 1. Démarrer une transaction (ESSENTIEL car 3 inserts)
            conn.setAutoCommit(false);

            // 1.1 Récupérer l'ID du rôle
            Long roleId = getRoleId(conn, "SECRETAIRE");

            // --- A. INSERT UTILISATEUR ---
            String sqlUser = """
                INSERT INTO utilisateur 
                (nom, prenom, email, adresse, cin, tel, sexe, login, mot_de_passe, role_id, actif, date_creation) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
            """;

            Long userId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getAdresse());
                ps.setString(5, s.getCin());
                ps.setString(6, s.getTel());
                ps.setString(7, s.getSexe() != null ? s.getSexe().name() : "AUTRE");
                ps.setString(8, s.getLogin());
                ps.setString(9, s.getMotDePass_hash());
                if(roleId != null) ps.setLong(10, roleId); else ps.setNull(10, Types.BIGINT);
                ps.setBoolean(11, s.isActif());

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) userId = rs.getLong(1);
                }
            }

            if (userId == null) throw new SQLException("Echec création Utilisateur, pas d'ID.");
            s.setId(userId);

            // --- B. INSERT STAFF ---
            String sqlStaff = "INSERT INTO staff (id, salaire, prime) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, userId);
                ps.setDouble(2, s.getSalaire()); // Assurez-vous que l'entité a getSalaire()
                ps.setDouble(3, s.getPrime());
                ps.executeUpdate();
            }

            // --- C. INSERT SECRETAIRE ---
            String sqlSec = "INSERT INTO secretaire (id, num_cnss) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSec)) {
                ps.setLong(1, userId);
                ps.setString(2, s.getNumCNSS());
                ps.executeUpdate();
            }

            // 2. Valider la transaction
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erreur transactionnelle création Secretaire", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public Secretaire findById(Long id) {
        // JOINTURE OBLIGATOIRE
        String sql = """
            SELECT u.id as u_id, u.*, st.*, sec.* FROM utilisateur u
            JOIN staff st ON u.id = st.id
            JOIN secretaire sec ON st.id = sec.id
            WHERE u.id = ?
        """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Secretaire", e);
        }
        return null;
    }

    @Override
    public Secretaire findByNumCNSS(String numCNSS) {
        String sql = """
            SELECT u.id as u_id, u.*, st.*, sec.* FROM utilisateur u
            JOIN staff st ON u.id = st.id
            JOIN secretaire sec ON st.id = sec.id
            WHERE sec.num_cnss = ?
        """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numCNSS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNumCNSS", e);
        }
        return null;
    }

    @Override
    public List<Secretaire> findAll() {
        String sql = """
            SELECT u.id as u_id, u.*, st.*, sec.* FROM utilisateur u
            JOIN staff st ON u.id = st.id
            JOIN secretaire sec ON st.id = sec.id
        """;
        List<Secretaire> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Secretaire", e);
        }
        return list;
    }

    @Override
    public void update(Secretaire s) {
        // Update transactionnel sur 3 tables
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Update Utilisateur
            String sqlUser = "UPDATE utilisateur SET nom=?, prenom=?, email=?, tel=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlUser)){
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getTel());
                ps.setLong(5, s.getId());
                ps.executeUpdate();
            }

            // 2. Update Staff
            String sqlStaff = "UPDATE staff SET salaire=?, prime=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlStaff)){
                ps.setDouble(1, s.getSalaire());
                ps.setDouble(2, s.getPrime());
                ps.setLong(3, s.getId());
                ps.executeUpdate();
            }

            // 3. Update Secretaire
            String sqlSec = "UPDATE secretaire SET num_cnss=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlSec)){
                ps.setString(1, s.getNumCNSS());
                ps.setLong(2, s.getId());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException("Erreur update Secretaire", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public void deleteById(Long id) {
        // Grâce au ON DELETE CASCADE dans votre script SQL,
        // supprimer l'utilisateur supprime automatiquement le staff et le secretaire !
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Secretaire", e);
        }
    }

    // Implémentez delete(Secretaire s) en appelant deleteById...
    @Override public void delete(Secretaire s) { deleteById(s.getId()); }
}