package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    // =========================
    // CRUD
    // =========================
    @Override
    public void create(Patient p) throws DaoException {
        if (p == null) throw new DaoException("Patient null");
        if (p.getNom() == null || p.getNom().isBlank()) throw new DaoException("nom obligatoire");
        if (p.getPrenom() == null || p.getPrenom().isBlank()) throw new DaoException("prenom obligatoire");

        // ✅ BD actuelle: audit directement dans patient (pas base_entity)
        String sql = """
            INSERT INTO patient
              (nom, prenom, date_naissance, sexe, telephone, adresse, assurance,
               date_creation, date_modification, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(4, p.getSexe() != null ? toDbSexe(p.getSexe()) : null);
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());
            ps.setString(7, p.getAssurance() != null ? toDbAssurance(p.getAssurance()) : null);
            ps.setString(8, p.getCreePar());
            ps.setString(9, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

            // ✅ champ présent dans ton entity, mais pas en BD
            p.setBaseEntityId(null);

        } catch (Exception e) {
            throw new DaoException("Erreur create(Patient)", e);
        }
    }

    @Override
    public void update(Patient p) throws DaoException {
        if (p == null || p.getId() == null) throw new DaoException("Patient id obligatoire");

        Patient current = findById(p.getId());
        if (current == null) throw new DaoException("Patient introuvable id=" + p.getId());

        String sql = """
            UPDATE patient
               SET nom = ?,
                   prenom = ?,
                   date_naissance = ?,
                   sexe = ?,
                   telephone = ?,
                   adresse = ?,
                   assurance = ?,
                   date_modification = NOW(),
                   modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, p.getNom() != null ? p.getNom() : current.getNom());
            ps.setString(2, p.getPrenom() != null ? p.getPrenom() : current.getPrenom());

            ps.setDate(3,
                    p.getDateNaissance() != null
                            ? Date.valueOf(p.getDateNaissance())
                            : (current.getDateNaissance() != null ? Date.valueOf(current.getDateNaissance()) : null));

            Sexe sexe = (p.getSexe() != null) ? p.getSexe() : current.getSexe();
            ps.setString(4, sexe != null ? toDbSexe(sexe) : null);

            ps.setString(5, p.getTelephone() != null ? p.getTelephone() : current.getTelephone());
            ps.setString(6, p.getAdresse() != null ? p.getAdresse() : current.getAdresse());

            Assurance ass = (p.getAssurance() != null) ? p.getAssurance() : current.getAssurance();
            ps.setString(7, ass != null ? toDbAssurance(ass) : null);

            ps.setString(8, p.getModifiePar());
            ps.setLong(9, p.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur update(Patient) id=" + p.getId(), e);
        }
    }

    @Override
    public void delete(Patient p) throws DaoException {
        if (p == null || p.getId() == null) return;
        deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) throws DaoException {
        if (id == null) return;

        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur deleteById(Patient) id=" + id, e);
        }
    }

    // =========================
    // READ
    // =========================
    @Override
    public Patient findById(Long id) throws DaoException {
        if (id == null) return null;

        String sql = """
            SELECT *
              FROM patient
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPatient(rs) : null;
            }

        } catch (Exception e) {
            throw new DaoException("Erreur findById(Patient) id=" + id, e);
        }
    }

    @Override
    public List<Patient> findAll() throws DaoException {
        String sql = """
            SELECT *
              FROM patient
             ORDER BY id DESC
            """;

        List<Patient> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapPatient(rs));
            return list;

        } catch (Exception e) {
            throw new DaoException("Erreur findAll(Patient)", e);
        }
    }

    // =========================
    // Recherches
    // =========================
    @Override
    public List<Patient> findByNom(String nom) throws DaoException {
        if (nom == null) nom = "";

        String sql = """
            SELECT *
              FROM patient
             WHERE LOWER(nom) LIKE ?
             ORDER BY id DESC
            """;

        List<Patient> result = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + nom.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapPatient(rs));
            }
            return result;

        } catch (Exception e) {
            throw new DaoException("Erreur findByNom(Patient)", e);
        }
    }

    @Override
    public Patient findByTelephone(String telephone) throws DaoException {
        String sql = """
            SELECT *
              FROM patient
             WHERE telephone = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, telephone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPatient(rs) : null;
            }

        } catch (Exception e) {
            throw new DaoException("Erreur findByTelephone(Patient)", e);
        }
    }

    @Override
    public long countAll() throws DaoException {
        String sql = "SELECT COUNT(*) FROM patient";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong(1);
            return 0;

        } catch (Exception e) {
            throw new DaoException("Erreur countAll(Patient)", e);
        }
    }

    // =========================
    // Mapper + helpers
    // =========================
    private Patient mapPatient(ResultSet rs) throws SQLException {

        Timestamp dc = rs.getTimestamp("date_creation");
        Timestamp dm = rs.getTimestamp("date_modification");

        return Patient.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(rs.getDate("date_naissance") != null
                        ? rs.getDate("date_naissance").toLocalDate()
                        : null)
                .sexe(fromDbSexe(rs.getString("sexe")))
                .telephone(rs.getString("telephone"))
                .adresse(rs.getString("adresse"))
                .assurance(fromDbAssurance(rs.getString("assurance")))

                // ✅ pas en BD => null
                .baseEntityId(null)

                .dateCreation(dc != null ? dc.toLocalDateTime() : null)
                .datedeModification(dm != null ? dm.toLocalDateTime() : null)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // BD patient.sexe = 'H'/'F'
    private String toDbSexe(Sexe s) {
        if (s == null) return null;
        String name = s.name().toUpperCase();

        // robust: accepte Homme/Femme ou HOMME/FEMME
        if (name.contains("HOM")) return "H";
        if (name.contains("FEM")) return "F";

        // si ton enum est déjà H / F :
        if ("H".equals(name)) return "H";
        if ("F".equals(name)) return "F";

        return null;
    }

    private Sexe fromDbSexe(String db) {
        if (db == null) return null;
        String x = db.trim().toUpperCase();
        try {
            // si enum contient H/F
            if ("H".equals(x)) return Sexe.valueOf("H");
            if ("F".equals(x)) return Sexe.valueOf("F");
        } catch (Exception ignored) {}

        // sinon enum classique (HOMME/FEMME/AUTRE)
        try {
            if ("H".equals(x)) return Sexe.valueOf("HOMME");
            if ("F".equals(x)) return Sexe.valueOf("FEMME");
        } catch (Exception ignored) {}

        return null;
    }

    // BD patient.assurance enum('CNSS','CNOPS','MUTUELLE','AUTRE','AUCUNE')
    private String toDbAssurance(Assurance a) {
        if (a == null) return null;
        // Ton enum peut être CNSS/CNOPS/Mutuelle/Autre/Aucune
        String x = a.name().toUpperCase();
        return switch (x) {
            case "CNSS" -> "CNSS";
            case "CNOPS" -> "CNOPS";
            case "MUTUELLE", "MUTUEL" -> "MUTUELLE";
            case "AUTRE" -> "AUTRE";
            case "AUCUNE" -> "AUCUNE";
            default -> x; // au cas où
        };
    }

    private Assurance fromDbAssurance(String db) {
        if (db == null) return null;
        String x = db.trim().toUpperCase();

        // Essaie mapping direct
        try {
            return Assurance.valueOf(x);
        } catch (Exception ignored) {}

        // Sinon mapping vers ton enum (Mutuelle/Autre/Aucune)
        try {
            return switch (x) {
                case "CNSS" -> Assurance.valueOf("CNSS");
                case "CNOPS" -> Assurance.valueOf("CNOPS");
                case "MUTUELLE" -> Assurance.valueOf("Mutuelle");
                case "AUTRE" -> Assurance.valueOf("Autre");
                case "AUCUNE" -> Assurance.valueOf("Aucune");
                default -> null;
            };
        } catch (Exception ignored) {
            return null;
        }
    }
}