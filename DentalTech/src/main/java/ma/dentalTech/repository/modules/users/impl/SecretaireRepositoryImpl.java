package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;
import java.sql.*;
import java.util.*;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    private final Connection connection;

    public SecretaireRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    private Secretaire mapResultSetToSecretaire(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

        // --- Données héritées ---
        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setLogin(rs.getString("login"));
        s.setActif(rs.getBoolean("actif"));
        s.setSalaire(rs.getDouble("salaire"));

        java.sql.Date sqlDate = rs.getDate("date_recrutement");
        if (sqlDate != null) {
            s.setDateRecrutement(sqlDate.toLocalDate()); // Conversion java.sql.Date -> LocalDate
        }

        // --- Données spécifiques (CNSS en majuscules comme dans l'entité) ---
        s.setNumCNSS(rs.getString("num_cnss"));
        s.setCommission(rs.getDouble("commission"));

        return s;
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, sec.num_cnss, sec.commission " +
                "FROM secretaire sec " +
                "JOIN staff s ON sec.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id " +
                "WHERE sec.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToSecretaire(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- CORRECTION : Doit être findByNumCNSS pour correspondre à l'interface ---
    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, sec.num_cnss, sec.commission " +
                "FROM secretaire sec " +
                "JOIN staff s ON sec.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id " +
                "WHERE sec.num_cnss = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numCNSS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToSecretaire(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        List<Secretaire> list = new ArrayList<>();
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, sec.num_cnss, sec.commission " +
                "FROM secretaire sec " +
                "JOIN staff s ON sec.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id " +
                "WHERE sec.commission >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, minCommission);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToSecretaire(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Secretaire> findAllOrderByNom() {
        List<Secretaire> list = new ArrayList<>();
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, sec.num_cnss, sec.commission " +
                "FROM secretaire sec " +
                "JOIN staff s ON sec.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id " +
                "ORDER BY u.nom ASC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToSecretaire(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Secretaire> findAll() { return findAllOrderByNom(); }
    @Override public void create(Secretaire s) {}
    @Override public void update(Secretaire s) {}
    @Override public void deleteById(Long id) {}
    @Override public void delete(Secretaire s) {}
}