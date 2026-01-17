package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListeAttenteRepositoryImpl implements ListeAttenteRepository {

    @Override
    public List<ListeAttente> findAll() {
        String sql = "SELECT * FROM liste_attente";
        List<ListeAttente> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(RowMappers.mapListeAttente(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() ListeAttente", e);
        }
    }

    @Override
    public ListeAttente findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM liste_attente WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapListeAttente(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() ListeAttente, id=" + id, e);
        }
    }

    @Override
    public void create(ListeAttente l) {
        String sql = """
            INSERT INTO liste_attente (patient_id, nom, motif, date_ajout, priorite, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, l.getPatientId(), Types.BIGINT);
            ps.setString(2, l.getNom());
            ps.setString(3, l.getMotif());
            ps.setTimestamp(4, l.getDateAjout() != null ? Timestamp.valueOf(l.getDateAjout()) : null);
            ps.setString(5, l.getPriorite());
            ps.setString(6, l.getCreePar());
            ps.setString(7, l.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) l.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() ListeAttente", e);
        }
    }

    @Override
    public void update(ListeAttente l) {
        if (l.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE liste_attente
               SET patient_id = ?, nom = ?, motif = ?, date_ajout = ?, priorite = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setObject(1, l.getPatientId(), Types.BIGINT);
            ps.setString(2, l.getNom());
            ps.setString(3, l.getMotif());
            ps.setTimestamp(4, l.getDateAjout() != null ? Timestamp.valueOf(l.getDateAjout()) : null);
            ps.setString(5, l.getPriorite());
            ps.setString(6, l.getModifiePar());
            ps.setLong(7, l.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() ListeAttente, id=" + l.getId(), e);
        }
    }

    @Override
    public void delete(ListeAttente entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM liste_attente WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() ListeAttente, id=" + id, e);
        }
    }

    @Override
    public List<ListeAttente> findByNomListe(String nomListe) {
        String sql = "SELECT * FROM liste_attente WHERE nom LIKE ?";
        List<ListeAttente> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + (nomListe == null ? "" : nomListe) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapListeAttente(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNomListe()", e);
        }
    }

    @Override
    public int countActifs() {
        // "actifs" = patients en attente aujourd'hui (rdv rattachés à une liste d'attente)
        String sql = """
        SELECT COUNT(*)
        FROM liste_attente
        WHERE DATE(date_ajout) = CURDATE()
    """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countActifs() ListeAttente", e);
        }
    }

    @Override
    public int countPourMedecin(Long medecinId) {
        String sql = """
        SELECT COUNT(*)
        FROM rdv r
        JOIN detail_journee dj ON dj.id = r.detail_journee_id
        JOIN agenda_mensuel am ON am.id = dj.agenda_id
        WHERE r.date_rdv = CURDATE()
          AND r.liste_attente_id IS NOT NULL
          AND r.statut = 'PLANIFIE'
          AND am.medecin_id = ?
    """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur countPourMedecin() ListeAttente", e);
        }
    }


}
