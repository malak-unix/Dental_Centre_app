package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    @Override
    public void create(Antecedents a) {
        if (a == null) throw new DaoException("Antecedent null");
        if (a.getPatientId() == null || a.getPatientId() <= 0) throw new DaoException("patientId obligatoire");

        String sql = """
            INSERT INTO antecedent
              (patient_id, nom, categorie, niveau_de_risque, description, date_creation, date_modification, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, a.getPatientId());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getCategorie());
            ps.setString(4, a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : null);
            ps.setString(5, a.getDescription());
            ps.setString(6, a.getCreePar());
            ps.setString(7, a.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (Exception e) {
            throw new DaoException("Erreur create(Antecedent)", e);
        }
    }

    @Override
    public void update(Antecedents a) {
        if (a == null || a.getId() == null) throw new DaoException("Antecedent id obligatoire");
        if (a.getPatientId() == null || a.getPatientId() <= 0) throw new DaoException("patientId obligatoire");

        String sql = """
            UPDATE antecedent
               SET patient_id=?,
                   nom=?,
                   categorie=?,
                   niveau_de_risque=?,
                   description=?,
                   date_modification=NOW(),
                   modifie_par=?
             WHERE id=?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, a.getPatientId());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getCategorie());
            ps.setString(4, a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : null);
            ps.setString(5, a.getDescription());
            ps.setString(6, a.getModifiePar());
            ps.setLong(7, a.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur update(Antecedent) id=" + a.getId(), e);
        }
    }

    @Override
    public void delete(Antecedents a) {
        if (a == null || a.getId() == null) return;
        deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM antecedent WHERE id=?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur deleteById(Antecedent) id=" + id, e);
        }
    }

    @Override
    public Antecedents findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM antecedent WHERE id=?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAntecedent(rs) : null;
            }

        } catch (Exception e) {
            throw new DaoException("Erreur findById(Antecedent) id=" + id, e);
        }
    }

    @Override
    public List<Antecedents> findAll() {
        String sql = "SELECT * FROM antecedent ORDER BY id DESC";
        List<Antecedents> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapAntecedent(rs));
            return list;

        } catch (Exception e) {
            throw new DaoException("Erreur findAll(Antecedent)", e);
        }
    }

    @Override
    public List<Antecedents> findByPatientId(Long patientId) {
        if (patientId == null) return List.of();

        String sql = "SELECT * FROM antecedent WHERE patient_id=? ORDER BY id DESC";
        List<Antecedents> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapAntecedent(rs));
            }

            return list;

        } catch (Exception e) {
            throw new DaoException("Erreur findByPatientId(Antecedent) patientId=" + patientId, e);
        }
    }

    private Antecedents mapAntecedent(ResultSet rs) throws SQLException {
        Timestamp dc = rs.getTimestamp("date_creation");
        Timestamp dm = rs.getTimestamp("date_modification");

        String niv = rs.getString("niveau_de_risque");
        NiveauDeRisque n = null;
        if (niv != null) {
            try { n = NiveauDeRisque.valueOf(niv.toUpperCase()); } catch (Exception ignored) {}
        }

        return Antecedents.builder()
                .id(rs.getLong("id"))
                .patientId(rs.getLong("patient_id"))
                .nom(rs.getString("nom"))
                .categorie(rs.getString("categorie"))
                .niveauDeRisque(n)
                .description(rs.getString("description"))
                .dateCreation(dc != null ? dc.toLocalDateTime() : null)
                .dateDerniereModification(dm != null ? dm.toLocalDateTime() : null)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }
}
