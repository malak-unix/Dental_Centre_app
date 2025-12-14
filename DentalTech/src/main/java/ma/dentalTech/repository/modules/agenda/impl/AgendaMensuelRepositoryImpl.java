package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // ============================
    //  CRUD
    // ============================

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM agenda_mensuel";
        List<AgendaMensuel> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapAgendaMensuel(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() AgendaMensuel", e);
        }
    }

    @Override
    public AgendaMensuel findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM agenda_mensuel WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapAgendaMensuel(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() AgendaMensuel, id=" + id, e);
        }
    }

    @Override
    public void create(AgendaMensuel agenda) {
        String sql = """
            INSERT INTO agenda_mensuel (medecin_id, mois, annee, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (agenda.getMedecinId() == null) {
                throw new IllegalArgumentException("medecinId obligatoire pour créer un AgendaMensuel");
            }
            if (agenda.getMois() == null) {
                throw new IllegalArgumentException("mois obligatoire pour créer un AgendaMensuel");
            }

            ps.setLong(1, agenda.getMedecinId());
            ps.setString(2, agenda.getMois().name());
            ps.setInt(3, agenda.getAnnee());
            ps.setString(4, agenda.getCreePar());
            ps.setString(5, agenda.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    agenda.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() AgendaMensuel", e);
        }
    }

    @Override
    public void update(AgendaMensuel agenda) {
        if (agenda.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un AgendaMensuel sans id");
        }

        String sql = """
            UPDATE agenda_mensuel
               SET medecin_id = ?,
                   mois       = ?,
                   annee      = ?,
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            if (agenda.getMedecinId() == null) {
                throw new IllegalArgumentException("medecinId obligatoire pour mettre à jour un AgendaMensuel");
            }
            if (agenda.getMois() == null) {
                throw new IllegalArgumentException("mois obligatoire pour mettre à jour un AgendaMensuel");
            }

            ps.setLong(1, agenda.getMedecinId());
            ps.setString(2, agenda.getMois().name());
            ps.setInt(3, agenda.getAnnee());
            ps.setString(4, agenda.getModifiePar());
            ps.setLong(5, agenda.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() AgendaMensuel, id=" + agenda.getId(), e);
        }
    }

    @Override
    public void delete(AgendaMensuel entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM agenda_mensuel WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() AgendaMensuel, id=" + id, e);
        }
    }

    // ============================
    //  Méthodes spécifiques
    // ============================

    @Override
    public AgendaMensuel findByMedecinAndMonth(Long medecinId, String mois, int annee) {
        String sql = "SELECT * FROM agenda_mensuel WHERE medecin_id = ? AND mois = ? AND annee = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);
            ps.setString(2, mois);
            ps.setInt(3, annee);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAgendaMensuel(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMedecinAndMonth(), medecinId=" + medecinId, e);
        }
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM agenda_mensuel WHERE medecin_id = ?";
        List<AgendaMensuel> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapAgendaMensuel(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByMedecin(), medecinId=" + medecinId, e);
        }
    }
}
