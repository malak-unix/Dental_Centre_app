package ma.dentalTech.repository.modules.listeAttente.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.repository.modules.listeAttente.api.ListeAttenteRepository;
import ma.dentalTech.repository.common.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListeAttenteRepositoryJdbcImpl implements ListeAttenteRepository {

    @Override
    public void create(ListeAttente entity) {
        String sql = "INSERT INTO liste_attente " +
                "(nom, date_creation, date_modification, cree_par, modifie_par) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNomListe());

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(now)); // date_creation
            ps.setTimestamp(3, Timestamp.valueOf(now)); // date_modification

            ps.setString(4, entity.getCreePar());
            ps.setString(5, entity.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                    entity.setDateCreation(now);
                    entity.setDateDerniereModification(now);
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création de la liste d'attente", e);
        }
    }

    @Override
    public ListeAttente findById(Long id) {
        String sql = "SELECT * FROM liste_attente WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de la liste d'attente id=" + id, e);
        }
    }

    @Override
    public List<ListeAttente> findAll() {
        String sql = "SELECT * FROM liste_attente";

        List<ListeAttente> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du chargement de toutes les listes d'attente", e);
        }
    }

    @Override
    public void update(ListeAttente entity) {
        if (entity.getId() == null) {
            throw new RuntimeException("Impossible de mettre à jour une liste d'attente sans id");
        }

        String sql = "UPDATE liste_attente SET " +
                "nom = ?, " +
                "date_modification = ?, " +
                "modifie_par = ? " +
                "WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entity.getNomListe());

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(now)); // date_modification
            ps.setString(3, entity.getModifiePar());
            ps.setLong(4, entity.getId());

            ps.executeUpdate();
            entity.setDateDerniereModification(now);

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la liste d'attente id=" + entity.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM liste_attente WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression de la liste d'attente id=" + id, e);
        }
    }

    @Override
    public void delete(ListeAttente entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        deleteById(entity.getId());
    }

    @Override
    public ListeAttente findByNomListe(String nomListe) {
        String sql = "SELECT * FROM liste_attente WHERE nom = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nomListe);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de la liste d'attente par nom=" + nomListe, e);
        }
    }

    private ListeAttente map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nom = rs.getString("nom");

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModif = rs.getTimestamp("date_modification");

        LocalDateTime dateCreation = (tsCreation != null) ? tsCreation.toLocalDateTime() : null;
        LocalDateTime dateDerniereModif = (tsModif != null) ? tsModif.toLocalDateTime() : null;

        String creePar = rs.getString("cree_par");
        String modifiePar = rs.getString("modifie_par");

        return ListeAttente.builder()
                .id(id)
                .nomListe(nom)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateDerniereModif)
                .creePar(creePar)
                .modifiePar(modifiePar)
                .build();
    }
}
