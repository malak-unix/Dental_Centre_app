package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.util.*;

public class MedecinRepositoryImpl implements MedecinRepository {

    @SuppressWarnings("unused")
    private final Connection connection;

    public MedecinRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public MedecinRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public Medecin findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge,
                   m.specialite
            FROM medecin m
            JOIN staff st ON m.id = st.id
            JOIN utilisateur u ON st.id = u.id
            WHERE m.id = ?
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Medecin) id=" + id, e);
        }
    }

    @Override
    public List<Medecin> findAll() {
        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge,
                   m.specialite
            FROM medecin m
            JOIN staff st ON m.id = st.id
            JOIN utilisateur u ON st.id = u.id
            ORDER BY u.nom, u.prenom
        """;

        List<Medecin> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Medecin)", e);
        }
    }

    @Override public void create(Medecin m) {}
    @Override public void update(Medecin m) {}

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        // cascade
        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Medecin) id=" + id, e);
        }
    }

    @Override
    public void delete(Medecin m) {
        if (m == null || m.getId() == null) return;
        deleteById(m.getId());
    }

    @Override
    public List<Medecin> findAllOrderByNom() {
        return findAll();
    }

    @Override
    public List<Medecin> findBySpecialite(String specialiteLike) {
        String key = (specialiteLike == null) ? "" : specialiteLike.trim();

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge,
                   m.specialite
            FROM medecin m
            JOIN staff st ON m.id = st.id
            JOIN utilisateur u ON st.id = u.id
            WHERE m.specialite LIKE ?
            ORDER BY u.nom, u.prenom
        """;

        List<Medecin> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + key + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findBySpecialite(Medecin)", e);
        }
    }

    @Override
    public void updateMedecinFields(Medecin medecin) {
        if (medecin == null || medecin.getId() == null) return;

        String sql = "UPDATE medecin SET specialite = ? WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, medecin.getSpecialite());
            ps.setLong(2, medecin.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateMedecinFields id=" + medecin.getId(), e);
        }
    }

    private Medecin map(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setLogin(rs.getString("login"));
        m.setActif(rs.getBoolean("actif"));

        m.setSalaire(rs.getBigDecimal("salaire") != null ? rs.getBigDecimal("salaire").doubleValue() : 0.0);

        java.sql.Date d = rs.getDate("date_recrutement");
        if (d != null) m.setDateRecrutement(d.toLocalDate());

        m.setSpecialite(rs.getString("specialite"));

        return m;
    }
}
