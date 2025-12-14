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

            if (p == null) throw new IllegalArgumentException("Patient null");

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);

            // ⚠️ sexe en DB = 'H'/'F' (script), ton RowMappers gère la lecture.
            // Pour écrire: si ton enum est Sexe.Homme/Femme -> on met H/F
            if (p.getSexe() == null) {
                ps.setString(4, null);
            } else {
                String dbSexe = switch (p.getSexe().name().toUpperCase()) {
                    case "HOMME" -> "H";
                    case "FEMME" -> "F";
                    default -> "H";
                };
                ps.setString(4, dbSexe);
            }

            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());
            ps.setString(7, p.getNumAffiliation());

            // enums -> DB stocke les noms (CELIBATAIRE / CNSS / ...)
            ps.setString(8, p.getEtatCivil() != null ? p.getEtatCivil().name() : null);

            if (p.getAssurance() == null) {
                ps.setString(9, null);
            } else {
                // ton enum : Mutuelle/Autre/Aucune... mais DB = MUTUELLE/AUTRE/AUCUNE
                String dbAssurance = switch (p.getAssurance().name().toUpperCase()) {
                    case "MUTUELLE" -> "MUTUELLE";
                    case "CNSS" -> "CNSS";
                    case "CNOPS" -> "CNOPS";
                    case "AUTRE" -> "AUTRE";
                    case "AUCUNE" -> "AUCUNE";
                    default -> p.getAssurance().name().toUpperCase();
                };
                ps.setString(9, dbAssurance);
            }

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
        if (p == null || p.getId() == null) throw new IllegalArgumentException("Patient id obligatoire");

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

            if (p.getSexe() == null) {
                ps.setString(4, null);
            } else {
                String dbSexe = switch (p.getSexe().name().toUpperCase()) {
                    case "HOMME" -> "H";
                    case "FEMME" -> "F";
                    default -> "H";
                };
                ps.setString(4, dbSexe);
            }

            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());
            ps.setString(7, p.getNumAffiliation());
            ps.setString(8, p.getEtatCivil() != null ? p.getEtatCivil().name() : null);

            if (p.getAssurance() == null) {
                ps.setString(9, null);
            } else {
                String dbAssurance = switch (p.getAssurance().name().toUpperCase()) {
                    case "MUTUELLE" -> "MUTUELLE";
                    case "CNSS" -> "CNSS";
                    case "CNOPS" -> "CNOPS";
                    case "AUTRE" -> "AUTRE";
                    case "AUCUNE" -> "AUCUNE";
                    default -> p.getAssurance().name().toUpperCase();
                };
                ps.setString(9, dbAssurance);
            }

            ps.setString(10, p.getModifiePar());
            ps.setLong(11, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update() Patient, id=" + p.getId(), e);
        }
    }

    @Override
    public void delete(Patient entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
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

    @Override
    public Patient findByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) return null;

        String sql = "SELECT * FROM patient WHERE telephone = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapPatient(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByTelephone() Patient", e);
        }
    }
}
