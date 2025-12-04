package ma.dentalTech.repository.modules.caisse.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.common.JdbcUtils; // Utilitaire de connexion
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryJdbcImpl implements FactureRepository {

    private Facture map(ResultSet rs) throws SQLException {
        Timestamp dateTimestamp = rs.getTimestamp("date_facture");

        return Facture.builder()
                .id(rs.getLong("id"))
                // TODO: Ajouter le patient/consultation ID pour la complétude de l'entité
                .totaleFacture(rs.getDouble("total_facture"))
                .totalePaye(rs.getDouble("total_paye"))
                .reste(rs.getDouble("reste"))
                .status(StatutFacture.valueOf(rs.getString("statut")))
                // Conversion sécurisée de Timestamp vers LocalDateTime
                .dateFacture(dateTimestamp != null ? dateTimestamp.toLocalDateTime() : null)
                .build();
    }

    // =========================================================================================
    // CRUD : Implémentation des méthodes de CrudRepository
    // =========================================================================================

    @Override
    public void create(Facture facture) {
        String sql = "INSERT INTO facture (consultation_id, date_facture, total_facture, total_paye, statut) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // NOTE: Vous devrez mettre à jour l'entité Facture pour obtenir consultation_id ou l'utiliser comme 'null' pour l'instant
            ps.setObject(1, null); // consultation_id (remplacer par facture.getConsultationId() si disponible)
            ps.setTimestamp(2, Timestamp.valueOf(facture.getDateFacture()));
            ps.setDouble(3, facture.getTotaleFacture());
            ps.setDouble(4, facture.getTotalePaye());
            ps.setString(5, facture.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    facture.setId(rs.getLong(1));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création de la facture", e);
        }
    }

    @Override
    public void update(Facture facture) {
        String sql = "UPDATE facture SET consultation_id = ?, date_facture = ?, total_facture = ?, total_paye = ?, statut = ? " +
                "WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, null); // consultation_id (à remplacer)
            ps.setTimestamp(2, Timestamp.valueOf(facture.getDateFacture()));
            ps.setDouble(3, facture.getTotaleFacture());
            ps.setDouble(4, facture.getTotalePaye());
            ps.setString(5, facture.getStatus().name());
            ps.setLong(6, facture.getId());
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la facture", e);
        }
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM facture WHERE id = ?";
        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de la facture", e);
        }
        return null;
    }

    @Override
    public List<Facture> findAll() {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture ORDER BY date_facture DESC";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du chargement des factures", e);
        }
        return list;
    }

    @Override
    public void delete(Facture facture) {
        if (facture != null) deleteById(facture.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM facture WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression de la facture", e);
        }
    }

    // =========================================================================================
    // Méthodes Spécifiques de FactureRepository
    // =========================================================================================

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE statut = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des factures par statut", e);
        }
        return list;
    }

    @Override
    public List<Facture> findByDateFactureBetween(LocalDate start, LocalDate end) {
        return List.of();
    }

    /**
     * Correction de la signature : Utilise LocalDateTime pour correspondre à l'interface API.
     */
    @Override
    public List<Facture> findByDateFactureBetween(LocalDateTime start, LocalDateTime end) {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE date_facture BETWEEN ? AND ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des factures par période", e);
        }
        return list;
    }

    @Override
    public Double calculateTotalRevenue(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(total_paye) AS total FROM facture " +
                "WHERE date_facture BETWEEN ? AND ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du calcul du chiffre d'affaires", e);
        }
        return 0.0;
    }

    @Override
    public Double calculateTotalUnpaid() {
        // La colonne 'reste' est calculée par la BD. On filtre ce qui n'est pas encore payé.
        String sql = "SELECT SUM(reste) AS total FROM facture WHERE statut != 'PAYEE'";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du calcul du total impayé", e);
        }
        return 0.0;
    }
}