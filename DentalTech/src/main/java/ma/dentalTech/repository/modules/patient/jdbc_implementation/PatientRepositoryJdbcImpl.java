package ma.dentalTech.repository.modules.patient.jdbc_implementation;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.antecedents.Antecedents;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.EtatCivil;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryJdbcImpl implements PatientRepository {

    // =====================================================================================
    // Mapping ResultSet -> Patient
    // =====================================================================================
    private Patient mapPatient(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        // date_naissance
        LocalDate dateNaissance = null;
        Date dn = rs.getDate("date_naissance");
        if (dn != null) dateNaissance = dn.toLocalDate();

        // date_creation / date_modification
        LocalDateTime dateCreation = null;
        Timestamp tsCreation = rs.getTimestamp("date_creation");
        if (tsCreation != null) dateCreation = tsCreation.toLocalDateTime();

        LocalDateTime dateDerniereModification = null;
        Timestamp tsModif = rs.getTimestamp("date_modification");
        if (tsModif != null) dateDerniereModification = tsModif.toLocalDateTime();

        // etat civil
        EtatCivil etatCivil = null;
        String ec = rs.getString("etat_civil");
        if (ec != null && !ec.isBlank()) {
            try {
                etatCivil = EtatCivil.valueOf(ec.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        // sexe : la table a 'H'/'F' mais ton enum peut être HOMME/FEMME/etc.
        Sexe sexe = null;
        String sexeStr = rs.getString("sexe");
        if (sexeStr != null && !sexeStr.isBlank()) {
            String s = sexeStr.trim().toUpperCase();
            try {
                // si ton enum est HOMME/FEMME/AUTRE
                if (s.equals("H")) s = "HOMME";
                if (s.equals("F")) s = "FEMME";
                sexe = Sexe.valueOf(s);
            } catch (IllegalArgumentException ignored) { }
        }

        // assurance
        Assurance assurance = null;
        String assurStr = rs.getString("assurance");
        if (assurStr != null && !assurStr.isBlank()) {
            try {
                assurance = Assurance.valueOf(assurStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        return Patient.builder()
                .id(id)
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .adresse(rs.getString("adresse"))
                .telephone(rs.getString("telephone"))
                .email(rs.getString("email"))
                .dateNaissance(dateNaissance)
                .numAffiliation(rs.getString("num_affiliation"))
                .etatCivil(etatCivil)
                .sexe(sexe)
                .assurance(assurance)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateDerniereModification)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =====================================================================================
    // Mapping ResultSet -> Antecedents
    // (adapter si le nom des colonnes est différent dans ta table antecedent)
    // =====================================================================================
    private Antecedents mapAntecedent(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        NiveauDeRisque niveau = null;
        String nr = rs.getString("niveau_de_risque");
        if (nr != null && !nr.isBlank()) {
            try {
                niveau = NiveauDeRisque.valueOf(nr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        return Antecedents.builder()
                .id(id)
                .nom(rs.getString("nom"))
                .categorie(rs.getString("categorie"))
                .niveauDeRisque(niveau)
                .description(rs.getString("description"))
                .build();
    }

    // =====================================================================================
    // CRUD de base
    // =====================================================================================

    @Override
    public void create(Patient p) {
        String sql = """
                INSERT INTO patient
                (nom, prenom, adresse, telephone, email,
                 date_naissance, num_affiliation, etat_civil, sexe, assurance,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            if (p.getDateNaissance() != null) {
                ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setString(7, p.getNumAffiliation());

            if (p.getEtatCivil() != null) {
                ps.setString(8, p.getEtatCivil().name());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            if (p.getSexe() != null) {
                // on suppose que ton enum a des noms compatibles avec la colonne
                ps.setString(9, p.getSexe().name());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }

            if (p.getAssurance() != null) {
                ps.setString(10, p.getAssurance().name());
            } else {
                ps.setNull(10, Types.VARCHAR);
            }

            LocalDateTime dc = (p.getDateCreation() != null) ? p.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(11, Timestamp.valueOf(dc));

            ps.setString(12, p.getCreePar());
            ps.setString(13, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getLong(1));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création du patient", e);
        }
    }

    @Override
    public void update(Patient p) {
        String sql = """
                UPDATE patient
                   SET nom = ?,
                       prenom = ?,
                       adresse = ?,
                       telephone = ?,
                       email = ?,
                       date_naissance = ?,
                       num_affiliation = ?,
                       etat_civil = ?,
                       sexe = ?,
                       assurance = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            if (p.getDateNaissance() != null) {
                ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setString(7, p.getNumAffiliation());

            if (p.getEtatCivil() != null) {
                ps.setString(8, p.getEtatCivil().name());
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            if (p.getSexe() != null) {
                ps.setString(9, p.getSexe().name());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }

            if (p.getAssurance() != null) {
                ps.setString(10, p.getAssurance().name());
            } else {
                ps.setNull(10, Types.VARCHAR);
            }

            LocalDateTime dm = (p.getDateDerniereModification() != null)
                    ? p.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(11, Timestamp.valueOf(dm));

            ps.setString(12, p.getModifiePar());
            ps.setLong(13, p.getId());

            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du patient", e);
        }
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM patient WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par ID", e);
        }

        return null;
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient ORDER BY id";
        List<Patient> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapPatient(rs));
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les patients", e);
        }

        return list;
    }

    @Override
    public void delete(Patient p) {
        if (p != null && p.getId() != null) {
            deleteById(p.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM patient WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression du patient", e);
        }
    }

    // =====================================================================================
    // Méthodes supplémentaires de PatientRepository
    // =====================================================================================

    @Override
    public Optional<Patient> findByEmail(String email) {
        String sql = "SELECT * FROM patient WHERE email = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPatient(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par email", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = "SELECT * FROM patient WHERE telephone = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, telephone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPatient(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par téléphone", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = """
                SELECT * FROM patient
                 WHERE nom LIKE ? OR prenom LIKE ?
                 ORDER BY nom, prenom
                """;
        List<Patient> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPatient(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche par nom/prénom", e);
        }

        return list;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM patient WHERE id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong("total");
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors du comptage des patients", e);
        }

        return 0;
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM patient
                 ORDER BY id
                 LIMIT ? OFFSET ?
                """;
        List<Patient> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPatient(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération de la page de patients", e);
        }

        return list;
    }

    // =====================================================================================
    // Gestion de la relation Many-to-Many Patient <-> Antecedents
    // (adapte les noms de tables/colonnes si ton script SQL est différent)
    // =====================================================================================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT IGNORE INTO patient_antecedent(patient_id, antecedent_id) VALUES(?, ?)";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de l'association patient/antécédent", e);
        }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM patient_antecedent WHERE patient_id = ? AND antecedent_id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression d'un antécédent du patient", e);
        }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM patient_antecedent WHERE patient_id = ?";

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression des antécédents du patient", e);
        }
    }

    @Override
    public List<Antecedents> getAntecedentsOfPatient(Long patientId) {
        String sql = """
                SELECT a.*
                  FROM antecedent a
                  JOIN patient_antecedent pa ON pa.antecedent_id = a.id
                 WHERE pa.patient_id = ?
                 ORDER BY a.categorie, a.niveau_de_risque, a.nom
                """;
        List<Antecedents> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAntecedent(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération des antécédents du patient", e);
        }

        return list;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
                SELECT p.*
                  FROM patient p
                  JOIN patient_antecedent pa ON pa.patient_id = p.id
                 WHERE pa.antecedent_id = ?
                 ORDER BY p.nom, p.prenom
                """;
        List<Patient> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, antecedentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPatient(rs));
                }
            }
        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération des patients par antécédent", e);
        }

        return list;
    }
}
