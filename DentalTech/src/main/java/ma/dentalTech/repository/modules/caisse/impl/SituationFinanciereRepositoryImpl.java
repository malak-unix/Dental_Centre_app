package ma.dentalTech.repository.modules.caisse.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = "SELECT * FROM situation_financiere";
        List<SituationFinanciere> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapSituationFinanciere(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() SituationFinanciere", e);
        }
    }

    @Override
    public SituationFinanciere findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM situation_financiere WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() SituationFinanciere, id=" + id, e);
        }
    }

    @Override
    public void create(SituationFinanciere s) {
        String sql = """
            INSERT INTO situation_financiere
            (dossier_id, medecin_id, total_des_actes, total_paye, credit, statut, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, s.getDossierId());
            ps.setObject(2, s.getMedecinId(), Types.BIGINT);
            ps.setBigDecimal(3, s.getTotalDesActes() == null ? BigDecimal.ZERO : s.getTotalDesActes());
            ps.setBigDecimal(4, s.getTotalPaye() == null ? BigDecimal.ZERO : s.getTotalPaye());
            ps.setBigDecimal(5, s.getCredit() == null ? BigDecimal.ZERO : s.getCredit());
            ps.setString(6, s.getStatut() != null ? s.getStatut().name() : null);
            ps.setString(7, s.getCreePar());
            ps.setString(8, s.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() SituationFinanciere", e);
        }
    }

    @Override
    public void update(SituationFinanciere s) {
        if (s.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE situation_financiere
               SET dossier_id = ?, medecin_id = ?, total_des_actes = ?, total_paye = ?, credit = ?, statut = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, s.getDossierId());
            ps.setObject(2, s.getMedecinId(), Types.BIGINT);
            ps.setBigDecimal(3, s.getTotalDesActes() == null ? BigDecimal.ZERO : s.getTotalDesActes());
            ps.setBigDecimal(4, s.getTotalPaye() == null ? BigDecimal.ZERO : s.getTotalPaye());
            ps.setBigDecimal(5, s.getCredit() == null ? BigDecimal.ZERO : s.getCredit());
            ps.setString(6, s.getStatut() != null ? s.getStatut().name() : null);
            ps.setString(7, s.getModifiePar());
            ps.setLong(8, s.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() SituationFinanciere, id=" + s.getId(), e);
        }
    }
    @Override
    public void delete(SituationFinanciere entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM situation_financiere WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() SituationFinanciere, id=" + id, e);
        }
    }

    @Override
    public SituationFinanciere findLast() {
        String sql = "SELECT * FROM situation_financiere ORDER BY id DESC LIMIT 1";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findLast() SituationFinanciere", e);
        }
    }
}
