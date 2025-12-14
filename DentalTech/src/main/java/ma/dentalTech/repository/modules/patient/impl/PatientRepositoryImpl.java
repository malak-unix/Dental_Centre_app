package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient";
        List<Patient> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapPatient(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll() Patient", e);
        }
    }

    @Override
    public Patient findById(Long id) {
        if (id == null) return null;
        String sql = "SELECT * FROM patient WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapPatient(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById() Patient, id=" + id, e);
        }
    }

    @Override
    public void create(Patient p) {
        String sql = """
            INSERT INTO patient
            (nom, prenom, date_naissance, sexe, telephone, adresse, num_affiliation, etat_civil, assurance, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(4, p.getSexe()); // si enum -> p.getSexe().name()
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());
            ps.setString(7, p.getNumAffiliation());
            ps.setString(8, p.getEtatCivil());
            ps.setString(9, p.getAssurance());
            ps.setString(10, p.getCreePar());
            ps.setString(11, p.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create() Patient", e);
        }
    }

    @Override
    public void update(Patient p) {
        if (p.getId() == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
            UPDATE patient
               SET nom = ?, prenom = ?, date_naissance = ?, sexe = ?, telephone = ?, adresse = ?,
                   num_affiliation = ?, etat_civil = ?, assurance = ?, modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(4, p.getSexe());
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());
            ps.setString(7, p.getNumAffiliation());
            ps.setString(8, p.getEtatCivil());
            ps.setString(9, p.getAssurance());
            ps.setString(10, p.getModifiePar());
            ps.setLong(11, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Patient, id=" + p.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById() Patient, id=" + id, e);
        }
    }

    @Override
    public List<Patient> findByNomLike(String nomPart) {
        String sql = "SELECT * FROM patient WHERE nom LIKE ?";
        List<Patient> out = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + (nomPart == null ? "" : nomPart) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByNomLike() Patient", e);
        }
    }
}
