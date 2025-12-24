package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.modules.users.api.StaffRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class StaffRepositoryImpl implements StaffRepository {
    private final Connection connection;

    public StaffRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setLogin(rs.getString("login"));
        s.setActif(rs.getBoolean("actif"));
        s.setSalaire(rs.getDouble("salaire"));

        // Utilisez le nom complet du package pour éviter l'ambiguïté
        java.sql.Date sqlDate = rs.getDate("date_recrutement");
        if (sqlDate != null) {
            // Maintenant toLocalDate() fonctionnera car l'objet est clairement un java.sql.Date
            s.setDateRecrutement(sqlDate.toLocalDate());
        }
        return s;
    }

    @Override
    public Staff findById(Long id) {
        String sql = "SELECT u.*, s.salaire, s.date_recrutement FROM staff s " +
                "JOIN utilisateur u ON s.id = u.id WHERE s.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToStaff(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Staff> findAll() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT u.*, s.salaire, s.date_recrutement FROM staff s " +
                "JOIN utilisateur u ON s.id = u.id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToStaff(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public void create(Staff s) {}
    @Override public void update(Staff s) {}
    @Override public void deleteById(Long id) {}
    @Override public void delete(Staff s) {}

    @Override
    public List<Staff> findAllOrderByNom() {
        return List.of();
    }

    @Override
    public List<Staff> findBySalaireBetween(Double min, Double max) {
        return List.of();
    }

    @Override
    public List<Staff> findByDateRecrutementAfter(LocalDate date) {
        return List.of();
    }
}