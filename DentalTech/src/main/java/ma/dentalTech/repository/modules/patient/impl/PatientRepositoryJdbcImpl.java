package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.antecedents.Antecedents;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.EtatCivil;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implémentation JDBC du PatientRepository.
 * Utilise la table PATIENT définie dans schema.sql.
 */
public class PatientRepositoryJdbcImpl implements PatientRepository {

    // =====================================================================================
    //  MAPPING ResultSet -> Patient
    // =====================================================================================

    private Patient mapPatient(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) {
            id = null;
        }

        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String adresse = rs.getString("adresse");
        String telephone = rs.getString("telephone");
        LocalDate dateNaissance = rs.getDate("date_naissance") != null
                ? rs.getDate("date_naissance").toLocalDate()
                : null;

        String numAffiliation = rs.getString("num_affiliation");

        // sexe : H / F  -> enum Sexe (on suppose Homme / Femme)
        String sexeDb = rs.getString("sexe");
        Sexe sexe = null;
        if (sexeDb != null && !sexeDb.isBlank()) {
            String s = sexeDb.trim().toUpperCase();
            if ("H".equals(s)) {
                try {
                    sexe = Sexe.Homme;   // si ton enum a Homme/Femme
                } catch (IllegalArgumentException ex) {
                    try { sexe = Sexe.valueOf("HOMME"); } catch (Exception ignore) {}
                }
            } else if ("F".equals(s)) {
                try {
                    sexe = Sexe.Femme;
                } catch (IllegalArgumentException ex) {
                    try { sexe = Sexe.valueOf("FEMME"); } catch (Exception ignore) {}
                }
            }
        }

        // etat_civil : CELIBATAIRE / MARIE / DIVORCE / VEUF
        String etatCivilDb = rs.getString("etat_civil");
        EtatCivil etatCivil = null;
        if (etatCivilDb != null && !etatCivilDb.isBlank()) {
            try {
                etatCivil = EtatCivil.valueOf(etatCivilDb.trim().toUpperCase());
            } catch (IllegalArgumentException ignore) {
                // valeur inconnue -> on laisse null
            }
        }

        // assurance : CNSS / CNOPS / MUTUELLE / AUTRE / AUCUNE
        String assuranceDb = rs.getString("assurance");
        Assurance assurance = null;
        if (assuranceDb != null && !assuranceDb.isBlank()) {
            try {
                assurance = Assurance.valueOf(assuranceDb.trim().toUpperCase());
            } catch (IllegalArgumentException ignore) {
                // valeur inconnue -> null
            }
        }

        LocalDateTime dateCreation = null;
        java.sql.Timestamp tsCreation = rs.getTimestamp("date_creation");
        if (tsCreation != null) {
            dateCreation = tsCreation.toLocalDateTime();
        }

        LocalDateTime dateModification = null;
        java.sql.Timestamp tsModif = rs.getTimestamp("date_modification");
        if (tsModif != null) {
            dateModification = tsModif.toLocalDateTime();
        }

        String creePar = rs.getString("cree_par");
        String modifiePar = rs.getString("modifie_par");

        // Remarque : le schéma SQL ne contient pas de colonne email pour patient.
        // On laisse donc email = null.
        return Patient.builder()
                .id(id)
                .nom(nom)
                .prenom(prenom)
                .adresse(adresse)
                .telephone(telephone)
                .email(null)
                .dateNaissance(dateNaissance)
                .numAffiliation(numAffiliation)
                .etatCivil(etatCivil)
                .sexe(sexe)
                .assurance(assurance)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModification)
                .creePar(creePar)
                .modifiePar(modifiePar)
                .build();
    }

    // =====================================================================================
    //  CRUD de base
    // =====================================================================================

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient";
        List<Patient> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapPatient(rs));
            }
            return result;
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les patients", e);
        }
    }

    @Override
    public Patient findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM patient WHERE id = ?";
        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
            return null;
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par id", e);
        }
    }

    @Override
    public void create(Patient entity) {
        String sql = "INSERT INTO patient " +
                "(nom, prenom, date_naissance, sexe, telephone, adresse, " +
                " num_affiliation, etat_civil, assurance, cree_par, modifie_par) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getPrenom());

            if (entity.getDateNaissance() != null) {
                ps.setDate(3, Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            // sexe 'H' / 'F'
            String sexeDb = null;
            if (entity.getSexe() != null) {
                String name = entity.getSexe().name().toLowerCase(Locale.ROOT);
                if (name.startsWith("h")) {
                    sexeDb = "H";
                } else if (name.startsWith("f")) {
                    sexeDb = "F";
                }
            }
            if (sexeDb != null) {
                ps.setString(4, sexeDb);
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            ps.setString(5, entity.getTelephone());
            ps.setString(6, entity.getAdresse());
            ps.setString(7, entity.getNumAffiliation());

            if (entity.getEtatCivil() != null) {
                ps.setString(8, entity.getEtatCivil().name());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            if (entity.getAssurance() != null) {
                ps.setString(9, entity.getAssurance().name());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }

            ps.setString(10, entity.getCreePar());
            ps.setString(11, entity.getModifiePar());

            ps.executeUpdate();

            // Récup id généré
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création du patient", e);
        }
    }

    @Override
    public void update(Patient entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un patient sans id");
        }

        String sql = "UPDATE patient SET " +
                "nom = ?, prenom = ?, date_naissance = ?, sexe = ?, telephone = ?, " +
                "adresse = ?, num_affiliation = ?, etat_civil = ?, assurance = ?, " +
                "modifie_par = ?, date_modification = CURRENT_TIMESTAMP " +
                "WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getPrenom());

            if (entity.getDateNaissance() != null) {
                ps.setDate(3, Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            String sexeDb = null;
            if (entity.getSexe() != null) {
                String name = entity.getSexe().name().toLowerCase(Locale.ROOT);
                if (name.startsWith("h")) {
                    sexeDb = "H";
                } else if (name.startsWith("f")) {
                    sexeDb = "F";
                }
            }
            if (sexeDb != null) {
                ps.setString(4, sexeDb);
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            ps.setString(5, entity.getTelephone());
            ps.setString(6, entity.getAdresse());
            ps.setString(7, entity.getNumAffiliation());

            if (entity.getEtatCivil() != null) {
                ps.setString(8, entity.getEtatCivil().name());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            if (entity.getAssurance() != null) {
                ps.setString(9, entity.getAssurance().name());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }

            ps.setString(10, entity.getModifiePar());
            ps.setLong(11, entity.getId());

            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du patient", e);
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

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression du patient", e);
        }
    }

    // =====================================================================================
    //  Méthodes spécifiques PatientRepository
    // =====================================================================================

    @Override
    public Optional<Patient> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        // pas de colonne email dans le schéma -> filtrage en mémoire
        return findAll().stream()
                .filter(p -> email.equalsIgnoreCase(p.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM patient WHERE telephone = ?";
        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPatient(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par téléphone", e);
        }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String sql = "SELECT * FROM patient WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ?";
        List<Patient> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String like = "%" + keyword.toLowerCase(Locale.ROOT).trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapPatient(rs));
                }
            }
            return result;
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche de patients par nom/prénom", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        String sql = "SELECT 1 FROM patient WHERE id = ?";
        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la vérification d'existence du patient", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM patient";
        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0L;
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du comptage des patients", e);
        }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        if (limit <= 0) return new ArrayList<>();
        if (offset < 0) offset = 0;

        String sql = "SELECT * FROM patient ORDER BY id LIMIT ? OFFSET ?";
        List<Patient> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapPatient(rs));
                }
            }
            return result;
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération paginée des patients", e);
        }
    }

    // =====================================================================================
    //  Méthodes antécédents : laissées vides pour l'instant
    // =====================================================================================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        // TODO: implémenter si besoin (update antecedent.patient_id ou table d'association)
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        // TODO: implémenter si besoin
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        // TODO: implémenter si besoin
    }

    @Override
    public List<Antecedents> getAntecedentsOfPatient(Long patientId) {
        // TODO: implémenter si besoin
        return new ArrayList<>();
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        // TODO: implémenter si besoin
        return new ArrayList<>();
    }
}
