package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryJdbcImpl implements ChargesRepository {

    private Charges mapResultSetToCharges(ResultSet rs) throws SQLException {
        return Charges.builder()
                .id(rs.getLong("id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getDouble("montant"))
                .date(rs.getTimestamp("date_charge").toLocalDateTime())
                // TODO: Inclure cabinet_id si nécessaire pour les relations
                .build();
    }

    // =========================================================================================
    // CRUD Implémentation
    // =========================================================================================
    @Override
    public List<Charges> findAll() {
        List<Charges> chargesList = new ArrayList<>();
        final String SQL = "SELECT * FROM charge ORDER BY date_charge DESC";
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                chargesList.add(mapResultSetToCharges(rs));
            }
        } catch (DaoException | SQLException e) {
            throw new RuntimeException(e);
        }
        return chargesList;
    }

    @Override
    public void create(Charges charge) {
        final String SQL = "INSERT INTO charge (cabinet_id, titre, description, montant, date_charge) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            // TODO: Remplacer 1L par l'ID du cabinet actif
            ps.setLong(1, 1L); // Exemple : cabinet_id
            ps.setString(2, charge.getTitre());
            ps.setString(3, charge.getDescription());
            ps.setDouble(4, charge.getMontant());
            ps.setTimestamp(5, Timestamp.valueOf(charge.getDate()));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    charge.setId(generatedKeys.getLong(1));
                }
            }
        } catch (DaoException | SQLException e) {
            throw new RuntimeException("Erreur de création de la charge : " + e.getMessage(), e);
        }
    }

    // =========================================================================================
    // Méthodes Spécifiques à ChargesRepository
    // =========================================================================================

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        // Implémentation pour filtrer par date
        //...
        return List.of();
    }

    @Override
    public Double calculateTotalExpenses(LocalDateTime start, LocalDateTime end) {
        final String SQL = "SELECT SUM(montant) FROM charge WHERE date_charge BETWEEN ? AND ?";
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

    @Override
    public Facture findById(Long id) { /* ... */ return null; }

    @Override
    public void update(Charges charge) { /* ... */ }

    @Override
    public void delete(Charges charge) { /* ... */ }

    @Override
    public void deleteById(Long id) { /* ... */ }
}