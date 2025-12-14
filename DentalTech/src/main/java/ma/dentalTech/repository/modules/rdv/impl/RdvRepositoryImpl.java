package ma.dentalTech.repository.modules.rdv.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RdvRepositoryImpl implements RdvRepository {

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv";
        List<RDV> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapRdv(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() RDV", e);
        }
    }

    @Override
    public RDV findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM rdv WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapRdv(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() RDV, id=" + id, e);
        }
    }

    @Override
    public void create(RDV r) {
        String sql = """
            INSERT INTO rdv
            (patient_id, detail_journee_id, liste_attente_id, date_rdv, heure, motif, statut, note_medecin, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, r.getPatientId(), Types.BIGINT);
            ps.setObject(2, r.getDetailJourneeId(), Types.BIGINT);
            ps.setObject(3, r.getListeAttenteId(), Types.BIGINT);

            ps.setDate(4, Date.valueOf(r.getDate())); // date_rdv NOT NULL
            ps.setTime(5, r.getHeure() != null ? Time.valueOf(r.getHeure()) : null);

            ps.setString(6, r.getMotif());
            ps.setString(7, r.getStatus() != null ? r.getStatus().name() : EtatRendezVous.PREVU.name());
            ps.setString(8, r.getNoteMedecin());

            ps.setString(9, r.getCreePar());
            ps.setString(10, r.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() RDV", e);
        }
    }

    @Override
    public void update(RDV r) {
        if (r.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE rdv
               SET patient_id = ?, detail_journee_id = ?, liste_attente_id = ?, date_rdv = ?, heure = ?,
                   motif = ?, statut = ?, note_medecin = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setObject(1, r.getPatientId(), Types.BIGINT);
            ps.setObject(2, r.getDetailJourneeId(), Types.BIGINT);
            ps.setObject(3, r.getListeAttenteId(), Types.BIGINT);
            ps.setDate(4, Date.valueOf(r.getDate()));
            ps.setTime(5, r.getHeure() != null ? Time.valueOf(r.getHeure()) : null);

            ps.setString(6, r.getMotif());
            ps.setString(7, r.getStatus() != null ? r.getStatus().name() : null);
            ps.setString(8, r.getNoteMedecin());

            ps.setString(9, r.getModifiePar());
            ps.setLong(10, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() RDV, id=" + r.getId(), e);
        }
    }

    @Override
    public void delete(RDV entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM rdv WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() RDV, id=" + id, e);
        }
    }

    @Override
    public List<RDV> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM rdv WHERE patient_id = ?";
        return selectList(sql, patientId);
    }

    @Override
    public List<RDV> findByDetailJourneeId(Long detailJourneeId) {
        String sql = "SELECT * FROM rdv WHERE detail_journee_id = ?";
        return selectList(sql, detailJourneeId);
    }

    @Override
    public List<RDV> findByListeAttenteId(Long listeAttenteId) {
        String sql = "SELECT * FROM rdv WHERE liste_attente_id = ?";
        return selectList(sql, listeAttenteId);
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE date_rdv = ?";
        List<RDV> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapRdv(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDate()", e);
        }
    }

    @Override
    public List<RDV> findByStatus(EtatRendezVous status) {
        String sql = "SELECT * FROM rdv WHERE statut = ?";
        List<RDV> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapRdv(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByStatus()", e);
        }
    }

    @Override
    public List<RDV> findUpcomingFromToday() {
        String sql = "SELECT * FROM rdv WHERE date_rdv >= CURDATE() ORDER BY date_rdv, heure";
        List<RDV> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapRdv(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findUpcomingFromToday()", e);
        }
    }

    private List<RDV> selectList(String sql, Long id) {
        List<RDV> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapRdv(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur selectList()", e);
        }
    }
}
