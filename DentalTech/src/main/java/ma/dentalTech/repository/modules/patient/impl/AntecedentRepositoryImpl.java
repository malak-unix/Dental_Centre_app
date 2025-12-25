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
    public void create(Antecedents a) throws DaoException {
        if (a == null) throw new DaoException("Antecedent null");
        if (a.getNom() == null || a.getNom().isBlank()) throw new DaoException("nom obligatoire");

        String sql = """
            INSERT INTO antecedent (nom, categorie, niveau_de_risque, description)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getNom());
            ps.setString(2, a.getCategorie());
            ps.setString(3, a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : null);
            ps.setString(4, a.getDescription());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (Exception e) {
            throw new DaoException("Erreur create(Antecedent)", e);
        }
    }

    @Override
    public void update(Antecedents a) throws DaoException {
        if (a == null || a.getId() == null) throw new DaoException("Antecedent id obligatoire");
        if (a.getNom() == null || a.getNom().isBlank()) throw new DaoException("nom obligatoire");

        String sql = """
            UPDATE antecedent
               SET nom=?,
                   categorie=?,
                   niveau_de_risque=?,
                   description=?
             WHERE id=?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, a.getNom());
            ps.setString(2, a.getCategorie());
            ps.setString(3, a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : null);
            ps.setString(4, a.getDescription());
            ps.setLong(5, a.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur update(Antecedent) id=" + a.getId(), e);
        }
    }

    @Override
    public void delete(Antecedents a) throws DaoException {
        if (a == null || a.getId() == null) return;
        deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        if (id == null) return;

        String sql = "DELETE FROM antecedent WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur deleteById(Antecedent) id=" + id, e);
        }
    }

    @Override
    public Antecedents findById(Long id) throws DaoException {
        if (id == null) return null;

        String sql = "SELECT * FROM antecedent WHERE id = ?";

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
    public List<Antecedents> findAll() throws DaoException {
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
    public List<Antecedents> findByPatientId(Long patientId) throws DaoException {
        // ✅ Ici on passe par la table de liaison patient_antecedent
        String sql = """
            SELECT a.*
              FROM antecedent a
              JOIN patient_antecedent pa ON pa.antecedent_id = a.id
             WHERE pa.patient_id = ?
             ORDER BY a.id DESC
            """;

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
        String niv = rs.getString("niveau_de_risque");
        NiveauDeRisque n = null;
        if (niv != null) {
            try { n = NiveauDeRisque.valueOf(niv); } catch (Exception ignored) {}
        }

        return Antecedents.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .categorie(rs.getString("categorie"))
                .niveauDeRisque(n)
                .description(rs.getString("description"))
                .build();
    }
}
