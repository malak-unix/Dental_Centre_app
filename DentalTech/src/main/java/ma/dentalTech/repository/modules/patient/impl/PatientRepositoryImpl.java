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

        String insertBase = """
            INSERT INTO base_entity (date_creation, date_modification, cree_par, modifie_par)
            VALUES (NOW(), NOW(), ?, ?)
            """;

        String insertPatient = """
            INSERT INTO patient (nom, prenom, date_naissance, sexe, telephone, adresse, assurance, base_entity_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection()) {
            cn.setAutoCommit(false);

            Long baseId;
            try (PreparedStatement ps = cn.prepareStatement(insertBase, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getCreePar());
                ps.setString(2, p.getModifiePar());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new DaoException("Impossible de créer base_entity");
                    baseId = keys.getLong(1);
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(insertPatient, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getNom());
                ps.setString(2, p.getPrenom());
                ps.setDate(3, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
                ps.setString(4, p.getSexe() != null ? toDbSexe(p.getSexe()) : null);
                ps.setString(5, p.getTelephone());
                ps.setString(6, p.getAdresse());
                ps.setString(7, p.getAssurance() != null ? p.getAssurance().name() : null);
                ps.setLong(8, baseId);

                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setId(keys.getLong(1));
                }
            }

            p.setBaseEntityId(baseId);
            cn.commit();

        } catch (Exception e) {
            throw new DaoException("Erreur create(Patient)", e);
        }
    }

    @Override
    public void update(Patient p) throws DaoException {
        if (p == null || p.getId() == null) throw new DaoException("Patient id obligatoire");

        Patient current = findById(p.getId());
        if (current == null) throw new DaoException("Patient introuvable id=" + p.getId());
        Long baseId = (p.getBaseEntityId() != null) ? p.getBaseEntityId() : current.getBaseEntityId();

        String updPatient = """
            UPDATE patient
               SET nom=?, prenom=?, date_naissance=?, sexe=?, telephone=?, adresse=?, assurance=?
             WHERE id=?
            """;

        String updBase = """
            UPDATE base_entity
               SET date_modification = NOW(), modifie_par = ?
             WHERE id = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(updPatient)) {
                ps.setString(1, p.getNom() != null ? p.getNom() : current.getNom());
                ps.setString(2, p.getPrenom() != null ? p.getPrenom() : current.getPrenom());
                ps.setDate(3, (p.getDateNaissance() != null) ? Date.valueOf(p.getDateNaissance())
                        : (current.getDateNaissance() != null ? Date.valueOf(current.getDateNaissance()) : null));
                ps.setString(4, p.getSexe() != null ? toDbSexe(p.getSexe())
                        : (current.getSexe() != null ? toDbSexe(current.getSexe()) : null));
                ps.setString(5, p.getTelephone() != null ? p.getTelephone() : current.getTelephone());
                ps.setString(6, p.getAdresse() != null ? p.getAdresse() : current.getAdresse());
                ps.setString(7, p.getAssurance() != null ? p.getAssurance().name()
                        : (current.getAssurance() != null ? current.getAssurance().name() : null));
                ps.setLong(8, p.getId());
                ps.executeUpdate();
            }

            if (baseId != null) {
                try (PreparedStatement ps = cn.prepareStatement(updBase)) {
                    ps.setString(1, p.getModifiePar());
                    ps.setLong(2, baseId);
                    ps.executeUpdate();
                }
            }

            cn.commit();

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

        Patient current = findById(id);
        if (current == null) return;

        String delPatient = "DELETE FROM patient WHERE id = ?";
        String delBase = "DELETE FROM base_entity WHERE id = ?";

        try (Connection cn = SessionFactory.getInstance().getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(delPatient)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            if (current.getBaseEntityId() != null) {
                try (PreparedStatement ps = cn.prepareStatement(delBase)) {
                    ps.setLong(1, current.getBaseEntityId());
                    ps.executeUpdate();
                }
            }

            cn.commit();

        } catch (Exception e) {
            throw new DaoException("Erreur deleteById(Patient) id=" + id, e);
        }
    }

    @Override
    public Patient findById(Long id) throws DaoException {
        if (id == null) return null;

        String sql = """
        SELECT p.*,
               b.date_creation              AS be_date_creation,
               b.date_derniere_modification AS be_date_modification,
               b.cree_par                   AS be_cree_par,
               b.modifie_par                AS be_modifie_par
          FROM patient p
          LEFT JOIN base_entity b ON b.id = p.base_entity_id
         WHERE p.id = ?
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
        SELECT 
            p.id,
            p.nom,
            p.prenom,
            p.date_naissance,
            p.sexe,
            p.telephone,
            p.adresse,
            p.assurance,
            p.base_entity_id,

            b.date_creation,
            b.date_derniere_modification,
            b.cree_par,
            b.modifie_par

        FROM patient p
        LEFT JOIN base_entity b ON b.id = p.base_entity_id
        ORDER BY p.id DESC
        """;

        List<Patient> list = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapPatient(rs));
            }
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
            SELECT p.*,
                   b.date_creation, b.date_modification, b.cree_par, b.modifie_par
              FROM patient p
              LEFT JOIN base_entity b ON b.id = p.base_entity_id
             WHERE LOWER(p.nom) LIKE ?
             ORDER BY p.id DESC
            """;

        List<Patient> result = new ArrayList<>();

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + nom.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapPatient(rs)); // ✅ pas map(rs)
                }
            }

            return result;

        } catch (Exception e) {
            throw new DaoException("Erreur findByNom()", e);
        }
    }

    @Override
    public Patient findByTelephone(String telephone) throws DaoException {
        String sql = """
            SELECT p.*,
                   b.date_creation, b.date_modification, b.cree_par, b.modifie_par
              FROM patient p
              LEFT JOIN base_entity b ON b.id = p.base_entity_id
             WHERE p.telephone = ?
            """;

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, telephone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPatient(rs) : null; // ✅ pas map(rs)
            }

        } catch (Exception e) {
            throw new DaoException("Erreur findByTelephone()", e);
        }
    }

    // =========================
    // Mapper + helpers
    // =========================
    private Patient mapPatient(ResultSet rs) throws SQLException {

        Timestamp dc = rs.getTimestamp("date_creation");
        Timestamp dm = rs.getTimestamp("date_derniere_modification");

        return Patient.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(rs.getDate("date_naissance") != null
                        ? rs.getDate("date_naissance").toLocalDate()
                        : null)
                .sexe(toSexeEnum(rs.getString("sexe")))
                .telephone(rs.getString("telephone"))
                .adresse(rs.getString("adresse"))
                .assurance(toAssuranceEnum(rs.getString("assurance")))

                .baseEntityId(rs.getObject("base_entity_id") != null
                        ? rs.getLong("base_entity_id")
                        : null)

                .dateCreation(dc != null ? dc.toLocalDateTime() : null)
                .datedeModification(dm != null ? dm.toLocalDateTime() : null)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }


    private String toDbSexe(Sexe s) {
        if (s == null) return null;
        if (s == Sexe.Homme) return "H";
        if (s == Sexe.Femme) return "F";
        return null;
    }

    private Sexe toSexeEnum(String db) {
        if (db == null) return null;
        if ("H".equalsIgnoreCase(db)) return Sexe.Homme;
        if ("F".equalsIgnoreCase(db)) return Sexe.Femme;
        return null;
    }

    private Assurance toAssuranceEnum(String db) {
        if (db == null) return null;
        try {
            return Assurance.valueOf(db);
        } catch (Exception e) {
            String x = db.trim().toUpperCase();
            return switch (x) {
                case "CNSS" -> Assurance.CNSS;
                case "CNOPS" -> Assurance.CNOPS;
                case "MUTUELLE" -> Assurance.Mutuelle;
                case "AUTRE" -> Assurance.Autre;
                case "AUCUNE" -> Assurance.Aucune;
                default -> null;
            };
        }
    }
    @Override
    public long countAll() throws DaoException {
        String sql = "SELECT COUNT(*) FROM patient";

        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (Exception e) {
            throw new DaoException("Erreur countAll(Patient)", e);
        }
    }

}
