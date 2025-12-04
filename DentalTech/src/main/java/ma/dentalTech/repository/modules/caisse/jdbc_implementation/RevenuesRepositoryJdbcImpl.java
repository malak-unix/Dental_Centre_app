package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class RevenuesRepositoryJdbcImpl implements RevenuesRepository {

    private Revenues mapResultSetToRevenues(ResultSet rs) throws SQLException {
        return Revenues.builder()
                .id(rs.getLong("id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getDouble("montant"))
                .date(rs.getTimestamp("date_revenu").toLocalDateTime())
                // TODO: Inclure cabinet_id
                .build();
    }

    // =========================================================================================
    // CRUD Implémentation (Minimal)
    // =========================================================================================

    @Override
    public List<Revenues> findAll() { /* ... */ return List.of(); }

    @Override
    public Facture findById(Long id) { /* ... */ return null; }

    @Override
    public void create(Revenues revenu) {
        final String SQL = "INSERT INTO revenu (cabinet_id, titre, description, montant, date_revenu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            // TODO: Remplacer 1L par l'ID du cabinet actif
            ps.setLong(1, 1L); // Exemple : cabinet_id
            ps.setString(2, revenu.getTitre());
            ps.setString(3, revenu.getDescription());
            ps.setDouble(4, revenu.getMontant());
            ps.setTimestamp(5, Timestamp.valueOf(revenu.getDate()));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    revenu.setId(generatedKeys.getLong(1));
                }
            }
        } catch (DaoException | SQLException e) {
            throw new RuntimeException("Erreur de création du revenu : " + e.getMessage(), e);
        }
    }

    // =========================================================================================
    // Méthodes Spécifiques à RevenuesRepository
    // =========================================================================================

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        // Implémentation pour filtrer par date
        //...
        return List.of();
    }

    @Override
    public Double calculateTotalOtherRevenue(LocalDateTime start, LocalDateTime end) {
        final String SQL = "SELECT SUM(montant) FROM revenu WHERE date_revenu BETWEEN ? AND ?";
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (DaoException | SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    @Override public void update(Revenues revenu) { /* ... */ }
    @Override public void delete(Revenues revenu) { /* ... */ }
    @Override public void deleteById(Long id) { /* ... */ }
}