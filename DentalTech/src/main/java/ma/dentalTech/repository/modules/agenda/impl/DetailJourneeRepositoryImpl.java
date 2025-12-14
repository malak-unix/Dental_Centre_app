package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DetailJourneeRepositoryImpl implements DetailJourneeRepository {

    @Override
    public List<DetailJournee> findAll() {
        String sql = "SELECT * FROM detail_journee";
        List<DetailJournee> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapDetailJournee(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() DetailJournee", e);
        }
    }

    @Override
    public DetailJournee findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM detail_journee WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapDetailJournee(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() DetailJournee, id=" + id, e);
        }
    }

    @Override
    public void create(DetailJournee d) {
        String sql = """
            INSERT INTO detail_journee
            (agenda_id, date_jour, heure_debut_travail, heure_fin_travail, etat_jour, commentaire, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (d.getAgendaId() == null) throw new IllegalArgumentException("agendaId obligatoire");

            ps.setLong(1, d.getAgendaId());
            ps.setDate(2, d.getDateJour() != null ? Date.valueOf(d.getDateJour()) : null);
            ps.setTime(3, d.getHeureDebutTravaillee() != null ? Time.valueOf(d.getHeureDebutTravaillee()) : null);
            ps.setTime(4, d.getHeureFinTravaillee() != null ? Time.valueOf(d.getHeureFinTravaillee()) : null);
            ps.setString(5, d.getEtatJour() != null ? d.getEtatJour().name() : null);
            ps.setString(6, d.getCommentaire());
            ps.setString(7, d.getCreePar());
            ps.setString(8, d.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() DetailJournee", e);
        }
    }

    @Override
    public void update(DetailJournee d) {
        if (d.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE detail_journee
               SET agenda_id = ?, date_jour = ?, heure_debut_travail = ?, heure_fin_travail = ?,
                   etat_jour = ?, commentaire = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, d.getAgendaId());
            ps.setDate(2, d.getDateJour() != null ? Date.valueOf(d.getDateJour()) : null);
            ps.setTime(3, d.getHeureDebutTravaillee() != null ? Time.valueOf(d.getHeureDebutTravaillee()) : null);
            ps.setTime(4, d.getHeureFinTravaillee() != null ? Time.valueOf(d.getHeureFinTravaillee()) : null);
            ps.setString(5, d.getEtatJour() != null ? d.getEtatJour().name() : null);
            ps.setString(6, d.getCommentaire());
            ps.setString(7, d.getModifiePar());
            ps.setLong(8, d.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() DetailJournee, id=" + d.getId(), e);
        }
    }

    @Override
    public void delete(DetailJournee entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM detail_journee WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() DetailJournee, id=" + id, e);
        }
    }

    @Override
    public List<DetailJournee> findByAgendaId(Long agendaId) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ?";
        List<DetailJournee> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapDetailJournee(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByAgendaId()", e);
        }
    }

    @Override
    public DetailJournee findByAgendaIdAndDateJour(Long agendaId, LocalDate dateJour) {
        String sql = "SELECT * FROM detail_journee WHERE agenda_id = ? AND date_jour = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(dateJour));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapDetailJournee(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByAgendaIdAndDateJour()", e);
        }
    }
}
