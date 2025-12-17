package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

    // --- MAPPING (Jointure : Utilisateur + Staff) ---
    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        // 1. UTILISATEUR
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setTel(rs.getString("tel"));
        s.setAdresse(rs.getString("adresse"));
        s.setCin(rs.getString("cin"));
        s.setLogin(rs.getString("login"));
        s.setMotDePass_hash(rs.getString("mot_de_passe"));
        s.setActif(rs.getBoolean("actif"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            try { s.setSexe(Sexe.valueOf(sexeStr)); } catch(Exception e){}
        }

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) s.setDateNaissance(dateN.toLocalDate());

        // 2. STAFF
        s.setSalaire(rs.getDouble("salaire"));
        s.setPrime(rs.getDouble("prime"));
        s.setSoldeConge(rs.getInt("solde_conge"));

        Date dateRecrut = rs.getDate("date_recrutement");
        if (dateRecrut != null) s.setDateRecrutement(dateRecrut.toLocalDate());

        // 3. Traçabilité
        Timestamp dateCrea = rs.getTimestamp("date_creation");
        if (dateCrea != null) s.setDateCreation(dateCrea.toLocalDateTime());
        s.setCreePar(rs.getString("cree_par"));

        return s;
    }

    @Override
    public List<Staff> findAll() {
        // On récupère tout le monde qui est dans la table STAFF (Médecins + Secrétaires + Autres)
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
        """;
        List<Staff> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Staff", e);
        }
        return list;
    }

    @Override
    public Staff findById(Long id) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
            WHERE u.id = ?
        """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Staff", e);
        }
        return null;
    }

    @Override
    public void create(Staff s) {
        // Attention : En général, on crée soit un Medecin, soit une Secretaire.
        // Si on crée un "Staff" générique (ex: technicien de surface), il faut un rôle par défaut.

        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Utilisateur (On suppose un role par défaut ou NULL si la base l'autorise)
            // Note: Idéalement, il faudrait récupérer un role_id ici.
            String sqlUser = """
                INSERT INTO utilisateur 
                (nom, prenom, email, login, mot_de_passe, adresse, tel, cin, date_creation, actif) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)
            """;

            Long generatedId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getLogin());
                ps.setString(5, s.getMotDePass_hash());
                ps.setString(6, s.getAdresse());
                ps.setString(7, s.getTel());
                ps.setString(8, s.getCin());
                ps.setBoolean(9, s.isActif());

                ps.executeUpdate();
                try(ResultSet rs = ps.getGeneratedKeys()){ if(rs.next()) generatedId = rs.getLong(1); }
            }

            if(generatedId == null) throw new SQLException("Erreur ID User");
            s.setId(generatedId);

            // 2. Insert Staff
            String sqlStaff = "INSERT INTO staff (id, salaire, prime, date_recrutement, solde_conge) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, generatedId);
                ps.setDouble(2, s.getSalaire());
                ps.setDouble(3, s.getPrime());
                ps.setDate(4, s.getDateRecrutement() != null ? Date.valueOf(s.getDateRecrutement()) : null);
                ps.setInt(5, s.getSoldeConge());
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if(conn!=null) try{conn.rollback();}catch(Exception ex){}
            throw new RuntimeException("Erreur create Staff", e);
        } finally {
            if(conn!=null) try{conn.setAutoCommit(true); conn.close();}catch(Exception ex){}
        }
    }

    @Override
    public void update(Staff s) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Update User
            String sqlUser = "UPDATE utilisateur SET nom=?, prenom=?, tel=?, adresse=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlUser)){
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getTel());
                ps.setString(4, s.getAdresse());
                ps.setLong(5, s.getId());
                ps.executeUpdate();
            }

            // Update Staff
            String sqlStaff = "UPDATE staff SET salaire=?, prime=?, solde_conge=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlStaff)){
                ps.setDouble(1, s.getSalaire());
                ps.setDouble(2, s.getPrime());
                ps.setInt(3, s.getSoldeConge());
                ps.setLong(4, s.getId());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if(conn!=null) try{conn.rollback();}catch(Exception ex){}
            throw new RuntimeException("Erreur update Staff", e);
        } finally {
            if(conn!=null) try{conn.setAutoCommit(true); conn.close();}catch(Exception ex){}
        }
    }

    @Override
    public void deleteById(Long id) {
        // Cascade delete géré par la base
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Staff", e);
        }
    }

    @Override public void delete(Staff s) { if(s!=null) deleteById(s.getId()); }

    @Override
    public List<Staff> findBySalaireInferieurA(Double montant) {
        String sql = """
            SELECT u.*, s.salaire, s.prime, s.date_recrutement, s.solde_conge 
            FROM utilisateur u
            JOIN staff s ON u.id = s.id
            WHERE s.salaire < ?
        """;
        List<Staff> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, montant);
            try(ResultSet rs = ps.executeQuery()){ while(rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}