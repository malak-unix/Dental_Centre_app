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

    // --- Méthode spécifique de SecretaireRepository ---
    @Override
    public void updateSecretaireFields(Secretaire s) {
        String sql = "UPDATE secretaires SET num_cnss = ?, commission = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getNumCNSS());
            ps.setDouble(2, s.getCommission() != null ? s.getCommission() : 0.0);
            ps.setLong(3, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL updateSecretaireFields", e);
        }
    }

    // --- MÉTHODES DU CRUDREPOSITORY (Respect strict des signatures) ---

    @Override
    public List<Secretaire> findAll() {
        return new ArrayList<>();
    }

    @Override
    public Secretaire findById(Long id) { // Retourne T (Secretaire), pas Optional
        return null;
    }

    @Override
    public void create(Secretaire entity) { // Retourne void
    }

    @Override
    public void update(Secretaire entity) { // Retourne void
    }

    @Override
    public void delete(Secretaire entity) { // Méthode manquante ajoutée
    }

    @Override
    public void deleteById(Long id) { // Méthode manquante ajoutée
    }

    // --- MÉTHODES DE L'INTERFACE SECRETAIREREPOSITORY ---

    @Override
    public List<Secretaire> findAllOrderByNom() {
        return new ArrayList<>();
    }

    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        return Optional.empty();
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        return new ArrayList<>();
    }
}