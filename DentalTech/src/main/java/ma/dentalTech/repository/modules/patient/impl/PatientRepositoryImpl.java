package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.EtatCivil;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    // =========================
    // CrudRepository
    // =========================

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient ORDER BY id DESC";
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

    @Override
    public Patient findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM patient WHERE id=?";
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
    public void create(Patient p) {
        if (p == null) throw new DaoException("Patient null");
        if (isBlank(p.getNom())) throw new DaoException("nom obligatoire");
        if (isBlank(p.getPrenom())) throw new DaoException("prenom obligatoire");

        String sql = """
            INSERT INTO patient
              (nom, prenom, date_naissance, sexe, telephone, adresse, num_affiliation, etat_civil, assurance,
               date_creation, date_modification, cree_par, modifie_par)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());

            if (p.getDateNaissance() != null) ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            else ps.setNull(3, Types.DATE);

            ps.setString(4, p.getSexe() != null ? toDbSexe(p.getSexe()) : null);
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAdresse());

            ps.setString(7, p.getNumAffiliation()); // ✅ colonne existe
            ps.setString(8, p.getEtatCivil() != null ? p.getEtatCivil().name() : null); // ✅ colonne existe
            ps.setString(9, p.getAssurance() != null ? p.getAssurance().name() : null); // ✅ colonne existe

            ps.setString(10, p.getCreePar());
            ps.setString(11, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (Exception e) {
            throw new DaoException("Erreur create(Patient)", e);
        }
    }

    @Override
    public void update(Patient p) {
        if (p == null || p.getId() == null) throw new DaoException("Patient id obligatoire");

        Patient current = findById(p.getId());
        if (current == null) throw new DaoException("Patient introuvable id=" + p.getId());

        String sql = """
            UPDATE patient
               SET nom=?,
                   prenom=?,
                   date_naissance=?,
                   sexe=?,
                   telephone=?,
                   adresse=?,
                   num_affiliation=?,
                   etat_civil=?,
                   assurance=?,
                   date_modification=NOW(),
                   modifie_par=?
             WHERE id=?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, or(p.getNom(), current.getNom()));
            ps.setString(2, or(p.getPrenom(), current.getPrenom()));

            if (p.getDateNaissance() != null) ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            else if (current.getDateNaissance() != null) ps.setDate(3, Date.valueOf(current.getDateNaissance()));
            else ps.setNull(3, Types.DATE);

            Sexe sexe = (p.getSexe() != null) ? p.getSexe() : current.getSexe();
            ps.setString(4, sexe != null ? toDbSexe(sexe) : null);

            ps.setString(5, or(p.getTelephone(), current.getTelephone()));
            ps.setString(6, (p.getAdresse() != null) ? p.getAdresse() : current.getAdresse());

            ps.setString(7, (p.getNumAffiliation() != null) ? p.getNumAffiliation() : current.getNumAffiliation());

            EtatCivil ec = (p.getEtatCivil() != null) ? p.getEtatCivil() : current.getEtatCivil();
            ps.setString(8, ec != null ? ec.name() : null);

            Assurance ass = (p.getAssurance() != null) ? p.getAssurance() : current.getAssurance();
            ps.setString(9, ass != null ? ass.name() : null);

            ps.setString(10, p.getModifiePar());
            ps.setLong(11, p.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur update(Patient) id=" + p.getId(), e);
        }
    }

    @Override
    public void delete(Patient p) {
        if (p == null || p.getId() == null) return;
        deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM patient WHERE id=?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DaoException("Erreur deleteById(Patient) id=" + id, e);
        }
    }

    // =========================
    // PatientRepository extra
    // =========================

    @Override
    public Optional<Patient> findByEmail(String email) {
        // ❌ patient.email n’existe pas dans ton schema.sql
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        if (isBlank(telephone)) return Optional.empty();

        String sql = "SELECT * FROM patient WHERE telephone=?";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapPatient(rs)) : Optional.empty();
            }

        } catch (Exception e) {
            throw new DaoException("Erreur findByTelephone(Patient)", e);
        }
    }

    @Override
    public List<Patient> findByNom(String nom) {
        String key = (nom == null) ? "" : nom.trim().toLowerCase();

        String sql = """
            SELECT *
              FROM patient
             WHERE LOWER(nom) LIKE ?
             ORDER BY id DESC
            """;

        List<Patient> result = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + key + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapPatient(rs));
            }
            return result;

        } catch (Exception e) {
            throw new DaoException("Erreur findByNom(Patient)", e);
        }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String key = (keyword == null) ? "" : keyword.trim().toLowerCase();

        String sql = """
            SELECT *
              FROM patient
             WHERE LOWER(nom) LIKE ?
                OR LOWER(prenom) LIKE ?
             ORDER BY id DESC
            """;

        List<Patient> result = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String like = "%" + key + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapPatient(rs));
            }
            return result;

        } catch (Exception e) {
            throw new DaoException("Erreur searchByNomPrenom(Patient)", e);
        }
    }

    @Override
    public List<Patient> searchByNom(String nomPart) {
        return findByNom(nomPart);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM patient WHERE id=? LIMIT 1";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new DaoException("Erreur existsById(Patient) id=" + id, e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM patient";
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;

        } catch (Exception e) {
            throw new DaoException("Erreur count(Patient)", e);
        }
    }

    @Override
    public Integer countAll() {
        long v = count();
        return (v > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) v;
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        if (limit <= 0) limit = 10;
        if (offset < 0) offset = 0;

        String sql = "SELECT * FROM patient ORDER BY id DESC LIMIT ? OFFSET ?";
        List<Patient> result = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapPatient(rs));
            }
            return result;

        } catch (Exception e) {
            throw new DaoException("Erreur findPage(Patient)", e);
        }
    }

    // =========================
    // Many-to-many Antecedents
    // ⚠️ Ton schema.sql montre antecedent.patient_id (1-N), pas table pivot
    // Donc on ne fait pas many-to-many ici.
    // =========================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        throw new UnsupportedOperationException("Schema actuel: antecedent.patient_id => pas de many-to-many");
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        throw new UnsupportedOperationException("Schema actuel: antecedent.patient_id => pas de many-to-many");
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        throw new UnsupportedOperationException("Schema actuel: antecedent.patient_id => pas de many-to-many");
    }

    @Override
    public List<Antecedents> getAntecedentsOfPatient(Long patientId) {
        throw new UnsupportedOperationException("Utilise AntecedentRepository.findByPatientId(patientId)");
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        throw new UnsupportedOperationException("Schema actuel: antecedent.patient_id => pas de many-to-many");
    }

    // =========================
    // Mapper + helpers
    // =========================

    private Patient mapPatient(ResultSet rs) throws SQLException {
        Date dn = rs.getDate("date_naissance");
        Timestamp dc = rs.getTimestamp("date_creation");
        Timestamp dm = rs.getTimestamp("date_modification");

        return Patient.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .adresse(rs.getString("adresse"))
                .telephone(rs.getString("telephone"))
                .dateNaissance(dn != null ? dn.toLocalDate() : null)
                .sexe(fromDbSexe(rs.getString("sexe")))
                .numAffiliation(rs.getString("num_affiliation"))
                .etatCivil(fromDbEtatCivil(rs.getString("etat_civil")))
                .assurance(fromDbAssurance(rs.getString("assurance")))

                // ⚠️ email pas en BD patient => null
                .email(null)

                .dateCreation(dc != null ? dc.toLocalDateTime() : null)
                .dateDerniereModification(dm != null ? dm.toLocalDateTime() : null)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String or(String a, String b) {
        return !isBlank(a) ? a : b;
    }

    private String toDbSexe(Sexe s) {
        if (s == null) return null;
        if (s == Sexe.Homme) return "H";
        if (s == Sexe.Femme) return "F";
        return "H"; // défaut
    }

    private Sexe fromDbSexe(String db) {
        if (db == null) return null;
        String x = db.trim().toUpperCase();
        if ("H".equals(x) || "HOMME".equals(x)) return Sexe.Homme;
        if ("F".equals(x) || "FEMME".equals(x)) return Sexe.Femme;
        return null;
    }


    private Assurance fromDbAssurance(String db) {
        if (db == null) return null;
        try { return Assurance.valueOf(db.trim().toUpperCase()); } catch (Exception ignored) {}
        return null;
    }

    private EtatCivil fromDbEtatCivil(String db) {
        if (db == null) return null;
        try { return EtatCivil.valueOf(db.trim().toUpperCase()); } catch (Exception ignored) {}
        return null;
    }
}