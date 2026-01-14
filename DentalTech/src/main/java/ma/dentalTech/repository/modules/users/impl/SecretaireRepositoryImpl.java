package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.util.*;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    @SuppressWarnings("unused")
    private final Connection connection; // compat injection

    public SecretaireRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    public SecretaireRepositoryImpl() {
        this.connection = null;
    }

    @Override
    public void updateSecretaireFields(Secretaire s) {
        if (s == null || s.getId() == null) return;

        // ✅ table = secretaire (pas secretaires)
        String sql = "UPDATE secretaire SET num_cnss = ?, commission = ? WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getNumCNSS());
            if (s.getCommission() == null) ps.setNull(2, Types.DECIMAL);
            else ps.setBigDecimal(2, new java.math.BigDecimal(s.getCommission()));

            ps.setLong(3, s.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateSecretaireFields id=" + s.getId(), e);
        }
    }

    @Override
    public List<Secretaire> findAll() {
        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id,
                   sc.num_cnss, sc.commission
            FROM secretaire sc
            JOIN staff st ON sc.id = st.id
            JOIN utilisateur u ON st.id = u.id
            ORDER BY u.nom, u.prenom
        """;

        List<Secretaire> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll(Secretaire)", e);
        }
    }

    @Override
    public Secretaire findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id,
                   sc.num_cnss, sc.commission
            FROM secretaire sc
            JOIN staff st ON sc.id = st.id
            JOIN utilisateur u ON st.id = u.id
            WHERE sc.id = ?
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById(Secretaire) id=" + id, e);
        }
    }

    @Override
    public void create(Secretaire entity) {
        // Optionnel : si vous créez utilisateur + staff + secretaire ailleurs, laissez vide.
    }

    @Override
    public void update(Secretaire entity) {
        // Optionnel
    }

    @Override
    public void delete(Secretaire entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        // ✅ cascade: utilisateur -> staff -> secretaire
        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById(Secretaire) id=" + id, e);
        }
    }

    @Override
    public List<Secretaire> findAllOrderByNom() {
        return findAll();
    }

    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        if (numCNSS == null || numCNSS.isBlank()) return Optional.empty();

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id,
                   sc.num_cnss, sc.commission
            FROM secretaire sc
            JOIN staff st ON sc.id = st.id
            JOIN utilisateur u ON st.id = u.id
            WHERE sc.num_cnss = ?
        """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, numCNSS.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNumCNSS(Secretaire)", e);
        }
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        if (minCommission == null) minCommission = 0.0;

        String sql = """
            SELECT u.*, st.salaire, st.prime, st.date_recrutement, st.solde_conge, st.cabinet_id,
                   sc.num_cnss, sc.commission
            FROM secretaire sc
            JOIN staff st ON sc.id = st.id
            JOIN utilisateur u ON st.id = u.id
            WHERE sc.commission >= ?
            ORDER BY u.nom, u.prenom
        """;

        List<Secretaire> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setBigDecimal(1, new java.math.BigDecimal(minCommission));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCommissionMin(Secretaire)", e);
        }
    }

    private Secretaire map(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setPrenom(rs.getString("prenom"));
        s.setEmail(rs.getString("email"));
        s.setLogin(rs.getString("login"));
        s.setActif(rs.getBoolean("actif"));

        // Staff
        s.setSalaire(rs.getDouble("salaire"));
        java.sql.Date d = rs.getDate("date_recrutement");
        if (d != null) s.setDateRecrutement(d.toLocalDate());

        // Secretaire
        s.setNumCNSS(rs.getString("num_cnss"));
        s.setCommission(rs.getBigDecimal("commission") != null ? rs.getBigDecimal("commission").doubleValue() : null);

        return s;
    }
}
