package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class StaffRepositoryImpl implements StaffRepository {

    @SuppressWarnings("unused")
    private final Connection connection;

    public StaffRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public StaffRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Staff findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id
            FROM staff st
            JOIN utilisateur u ON st.id = u.id
            WHERE st.id = ?
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Staff) id=" + id, e);
        }
    }

    @Override
    public List<Staff> findAll() {
        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id
            FROM staff st
            JOIN utilisateur u ON st.id = u.id
            ORDER BY u.nom, u.prenom
        """;

        List<Staff> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Staff)", e);
        }
    }

    @Override public void create(Staff s) {}
    @Override public void update(Staff s) {}

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Staff) id=" + id, e);
        }
    }

    @Override
    public void delete(Staff s) {
        if (s == null || s.getId() == null) return;
        deleteById(s.getId());
    }

    @Override
    public List<Staff> findAllOrderByNom() {
        return findAll();
    }

    @Override
    public List<Staff> findBySalaireBetween(Double min, Double max) {
        if (min == null) min = 0.0;
        if (max == null) max = Double.MAX_VALUE;

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id
            FROM staff st
            JOIN utilisateur u ON st.id = u.id
            WHERE st.salaire BETWEEN ? AND ?
            ORDER BY u.nom, u.prenom
        """;

        List<Staff> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBigDecimal(1, new java.math.BigDecimal(min));
            ps.setBigDecimal(2, new java.math.BigDecimal(max));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findBySalaireBetween(Staff)", e);
        }
    }

    @Override
    public List<Staff> findByDateRecrutementAfter(LocalDate date) {
        if (date == null) return List.of();

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id
            FROM staff st
            JOIN utilisateur u ON st.id = u.id
            WHERE st.date_recrutement > ?
            ORDER BY u.nom, u.prenom
        """;

        List<Staff> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDateRecrutementAfter(Staff)", e);
        }
    }

    @Override
    public void updateStaffFields(Staff staff) {
        if (staff == null || staff.getId() == null) return;

        String sql = """
            UPDATE staff
               SET salaire = ?,
                   prime = ?,
                   date_recrutement = ?,
                   solde_conge = ?
             WHERE id = ?
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBigDecimal(1, new java.math.BigDecimal(staff.getSalaire()));
            ps.setBigDecimal(2, new java.math.BigDecimal(staff.getPrime() != null ? staff.getPrime() : 0.0));

            if (staff.getDateRecrutement() != null) ps.setDate(3, java.sql.Date.valueOf(staff.getDateRecrutement()));
            else ps.setNull(3, Types.DATE);

            ps.setInt(4, staff.getSoldeConge());
            ps.setLong(5, staff.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateStaffFields(Staff) id=" + staff.getId(), e);
        }
    }

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setLogin(rs.getString("login"));
        s.setActif(rs.getBoolean("actif"));

        s.setSalaire(rs.getBigDecimal("salaire") != null ? rs.getBigDecimal("salaire").doubleValue() : 0.0);
        s.setPrime(rs.getBigDecimal("prime") != null ? rs.getBigDecimal("prime").doubleValue() : 0.0);
        s.setSoldeConge(rs.getInt("solde_conge"));

        java.sql.Date d = rs.getDate("date_recrutement");
        if (d != null) s.setDateRecrutement(d.toLocalDate());

        return s;
    }
}
