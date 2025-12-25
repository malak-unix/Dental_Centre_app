package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import java.sql.*;
import java.util.*;

public class MedecinRepositoryImpl implements MedecinRepository {

    private final Connection connection;

    public MedecinRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        // --- Infos de Utilisateur ---
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setLogin(rs.getString("login"));
        m.setActif(rs.getBoolean("actif"));

        // --- Infos de Staff ---
        m.setSalaire(rs.getDouble("salaire"));

        // CORRECTION DE L'ERREUR : Conversion java.sql.Date -> java.time.LocalDate
        java.sql.Date sqlDate = rs.getDate("date_recrutement");
        if (sqlDate != null) {
            m.setDateRecrutement(sqlDate.toLocalDate());
        }

        // --- Infos de Medecin ---
        m.setSpecialite(rs.getString("specialite"));

        return m;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, m.specialite " +
                "FROM medecin m " +
                "JOIN staff s ON m.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id " +
                "WHERE m.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToMedecin(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Medecin> findAll() {
        List<Medecin> list = new ArrayList<>();
        String sql = "SELECT u.*, s.salaire, s.date_recrutement, m.specialite " +
                "FROM medecin m " +
                "JOIN staff s ON m.id = s.id " +
                "JOIN utilisateur u ON s.id = u.id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToMedecin(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void deleteById(Long id) {
        // Le ON DELETE CASCADE dans ton SQL gère le reste
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM utilisateur WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(Medecin m) {
        if (m != null && m.getId() != null) deleteById(m.getId());
    }

    // --- Stubs obligatoires ---
    @Override public void create(Medecin m) {}
    @Override public void update(Medecin m) {}

    @Override
    public List<Medecin> findAllOrderByNom() {
        return List.of();
    }

    @Override public List<Medecin> findBySpecialite(String spec) { return new ArrayList<>(); }
}