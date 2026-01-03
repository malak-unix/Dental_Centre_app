package ma.dentalTech.repository.modules.caisse.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

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

        if (s == null) throw new IllegalArgumentException("SituationFinanciere null dans create()");
        if (s.getDossierId() == null) throw new IllegalArgumentException("dossierId obligatoire (NOT NULL)");

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, s.getDossierId());
            ps.setObject(2, s.getMedecinId(), Types.BIGINT);
            ps.setDouble(3, s.getTotalDesActes() == null ? 0.0 : s.getTotalDesActes());
            ps.setDouble(4, s.getTotalPaye() == null ? 0.0 : s.getTotalPaye());
            ps.setDouble(5, s.getCredit() == null ? 0.0 : s.getCredit());
            ps.setString(6, s.getStatut() != null ? s.getStatut().name() : "NORMAL");
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
        if (s == null) throw new IllegalArgumentException("SituationFinanciere null dans update()");
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
            ps.setDouble(3, s.getTotalDesActes() == null ? 0.0 : s.getTotalDesActes());
            ps.setDouble(4, s.getTotalPaye() == null ? 0.0 : s.getTotalPaye());
            ps.setDouble(5, s.getCredit() == null ? 0.0 : s.getCredit());
            ps.setString(6, s.getStatut() != null ? s.getStatut().name() : "NORMAL");
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

    // =============================================================
    // AJOUTS POUR DOSSIER MEDICAL (onglet Situation financière)
    // =============================================================

    @Override
    public SituationFinanciere findByDossierId(Long dossierId) {
        if (dossierId == null) return null;

        String sql = """
            SELECT * FROM situation_financiere
             WHERE dossier_id = ?
             LIMIT 1
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDossierId() SituationFinanciere, dossierId=" + dossierId, e);
        }
    }

    @Override
    public SituationFinanciere findByPatientId(Long patientId) {
        if (patientId == null) return null;

        // On passe par dossier_medical car situation_financiere référence dossier_id
        String sql = """
            SELECT sf.*
              FROM situation_financiere sf
              JOIN dossier_medical d ON d.id = sf.dossier_id
             WHERE d.patient_id = ?
             ORDER BY sf.id DESC
             LIMIT 1
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByPatientId() SituationFinanciere, patientId=" + patientId, e);
        }
    }

    @Override
    public boolean resetByDossierId(Long dossierId, String modifiePar) {
        if (dossierId == null) return false;

        String sql = """
            UPDATE situation_financiere
               SET total_des_actes = 0,
                   total_paye = 0,
                   credit = 0,
                   statut = 'NORMAL',
                   modifie_par = ?
             WHERE dossier_id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, modifiePar);
            ps.setLong(2, dossierId);

            int updated = ps.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur resetByDossierId() SituationFinanciere, dossierId=" + dossierId, e);
        }
    }

    @Override
    public List<SituationFinanciere> findPage(int limit, int offset) {
        String sql = """
            SELECT * FROM situation_financiere
             ORDER BY id DESC
             LIMIT ? OFFSET ?
            """;

        List<SituationFinanciere> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findPage() SituationFinanciere, limit=" + limit + ", offset=" + offset, e);
        }

        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM situation_financiere";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur count() SituationFinanciere", e);
        }
    }
}
