package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.staff.Staff;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

    // --- MAPPING ---
    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        // 1. Utilisateur
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setAdresse(rs.getString("adresse"));
        s.setTel(rs.getString("tel"));
        s.setCin(rs.getString("cin"));
        s.setLogin(rs.getString("login"));
        s.setMotDePass_hash(rs.getString("mot_de_passe"));

        Date dateN = rs.getDate("date_naissance");
        if (dateN != null) s.setDateNaissance(dateN.toLocalDate());

        // 2. Staff
        s.setSalaire(rs.getDouble("salaire"));
        try { s.setPrime(rs.getDouble("prime")); } catch (Exception e) {}

        s.setCreePar(rs.getString("cree_par"));
        return s;
    }

    // --- FIND ALL ---
    @Override
    public List<Staff> findAll() {
        String sql = "SELECT u.*, s.salaire, s.prime FROM utilisateur u JOIN staff s ON u.id = s.id";
        List<Staff> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // --- CREATE ---
    @Override
    public void create(Staff s) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            Long roleId = null;
            try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM role WHERE libelle = ?")){
                ps.setString(1, "STAFF");
                ResultSet rs = ps.executeQuery();
                if(rs.next()) roleId = rs.getLong("id");
            }

            String sqlUser = "INSERT INTO utilisateur (nom, prenom, email, login, mot_de_passe, role_id, adresse, tel, cin, date_creation, cree_par, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Long generatedId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getLogin());
                ps.setString(5, s.getMotDePass_hash());
                ps.setObject(6, roleId);
                ps.setString(7, s.getAdresse());
                ps.setString(8, s.getTel());
                ps.setString(9, s.getCin());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(11, s.getCreePar());
                ps.setBoolean(12, s.isActif());
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
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // --- UPDATE ---
    @Override
    public void update(Staff s) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            String sqlUser = "UPDATE utilisateur SET nom=?, prenom=?, email=?, tel=?, adresse=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlUser)){
                ps.setString(1, s.getNom());
                ps.setString(2, s.getPrenom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getTel());
                ps.setString(5, s.getAdresse());
                ps.setLong(6, s.getId());
                ps.executeUpdate();
            }

            String sqlStaff = "UPDATE staff SET salaire=?, prime=? WHERE id=?";
            try(PreparedStatement ps = conn.prepareStatement(sqlStaff)){
                ps.setObject(1, s.getSalaire());
                ps.setObject(2, s.getPrime());
                ps.setLong(3, s.getId());
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
    public Staff findById(Long id) { return null; }

    @Override
    public void deleteById(Long id) {}

    // --- MÉTHODES MANQUANTES AJOUTÉES ---

    @Override
    public List<Staff> findBySalaireInferieurA(Double maxSalaire) {
        return new ArrayList<>();
    }

    // 👇 C'est celle-ci que le compilateur réclamait
    @Override
    public void delete(Staff s) {
        if (s != null && s.getId() != null) {
            deleteById(s.getId());
        }
    }
}