package ma.dentalTech.repository.modules.rdv.impl;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.repository.common.JdbcUtils;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation JDBC du RdvRepository.
 * Utilise la table RDV définie dans schema.sql.
 */
public class RdvRepositoryImpl implements RdvRepository {

    // =========================================================================
    //  Mapping ResultSet -> RDV
    // =========================================================================
    private RDV map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long patientId = rs.getLong("patient_id");
        if (rs.wasNull()) patientId = null;

        Long detailJourneeId = rs.getLong("detail_journee_id");
        if (rs.wasNull()) detailJourneeId = null;

        Long listeAttenteId = rs.getLong("liste_attente_id");
        if (rs.wasNull()) listeAttenteId = null;

        Date dateRdvSql = rs.getDate("date_rdv");
        LocalDate dateRdv = (dateRdvSql != null) ? dateRdvSql.toLocalDate() : null;

        Time timeSql = rs.getTime("heure");
        LocalTime heure = (timeSql != null) ? timeSql.toLocalTime() : null;

        String motif = rs.getString("motif");
        String statutDb = rs.getString("statut");
        String noteMedecin = rs.getString("note_medecin");

        Timestamp tsCreation = rs.getTimestamp("date_creation");
        Timestamp tsModification = rs.getTimestamp("date_modification");
        LocalDateTime dateCreation = (tsCreation != null) ? tsCreation.toLocalDateTime() : null;
        LocalDateTime dateModification = (tsModification != null) ? tsModification.toLocalDateTime() : null;

        String creePar = rs.getString("cree_par");
        String modifiePar = rs.getString("modifie_par");

        // Mapping statut DB -> enum EtatRendezVous
        EtatRendezVous statut = null;
        if (statutDb != null) {
            try {
                // La colonne SQL est ENUM('PREVU','CONFIRME','EN_COURS','TERMINE','ANNULE','ABSENT')
                // et l'enum Java a exactement les mêmes noms
                statut = EtatRendezVous.valueOf(statutDb.toUpperCase());
            } catch (IllegalArgumentException ex) {
                // Valeur inconnue en base : on laisse statut = null
            }
        }

        return RDV.builder()
                .id(id)
                .patientId(patientId)
                .detailJourneeId(detailJourneeId)
                .listeAttenteId(listeAttenteId)
                .date(dateRdv)
                .heure(heure)
                .motif(motif)
                .status(statut)
                .noteMedecin(noteMedecin)
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModification)
                .creePar(creePar)
                .modifiePar(modifiePar)
                .build();
    }

    // =========================================================================
    //  CRUD de base
    // =========================================================================

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la récupération des RDV", e);
        }
    }

    @Override
    public RDV findById(Long id) {
        if (id == null) return null;

        String sql = "SELECT * FROM rdv WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche du RDV par id", e);
        }
    }

    @Override
    public void create(RDV entity) {
        String sql = "INSERT INTO rdv " +
                "(patient_id, detail_journee_id, liste_attente_id, " +
                " date_rdv, heure, motif, statut, note_medecin, cree_par, modifie_par) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // patient_id
            if (entity.getPatientId() != null) {
                ps.setLong(1, entity.getPatientId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            // detail_journee_id
            if (entity.getDetailJourneeId() != null) {
                ps.setLong(2, entity.getDetailJourneeId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            // liste_attente_id
            if (entity.getListeAttenteId() != null) {
                ps.setLong(3, entity.getListeAttenteId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            // date_rdv
            if (entity.getDate() != null) {
                ps.setDate(4, Date.valueOf(entity.getDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            // heure
            if (entity.getHeure() != null) {
                ps.setTime(5, Time.valueOf(entity.getHeure()));
            } else {
                ps.setNull(5, Types.TIME);
            }

            // motif
            ps.setString(6, entity.getMotif());

            // statut enum -> string DB (exactement PREVU, CONFIRME, EN_COURS, TERMINE, ANNULE, ABSENT)
            if (entity.getStatus() != null) {
                ps.setString(7, entity.getStatus().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            // note_medecin
            ps.setString(8, entity.getNoteMedecin());

            // cree_par / modifie_par
            ps.setString(9, entity.getCreePar());
            ps.setString(10, entity.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la création du RDV", e);
        }
    }

    @Override
    public void update(RDV entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un RDV sans id");
        }

        String sql = "UPDATE rdv SET " +
                "date_rdv = ?, " +
                "heure = ?, " +
                "motif = ?, " +
                "statut = ?, " +
                "note_medecin = ?, " +
                "modifie_par = ? " +
                "WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            // 1 - date_rdv
            if (entity.getDate() != null) {
                ps.setDate(1, Date.valueOf(entity.getDate()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            // 2 - heure
            if (entity.getHeure() != null) {
                ps.setTime(2, Time.valueOf(entity.getHeure()));
            } else {
                ps.setNull(2, Types.TIME);
            }

            // 3 - motif
            ps.setString(3, entity.getMotif());

            // 4 - statut
            if (entity.getStatus() != null) {
                ps.setString(4, entity.getStatus().name());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            // 5 - note_medecin
            ps.setString(5, entity.getNoteMedecin());

            // 6 - modifie_par
            ps.setString(6, entity.getModifiePar());

            // 7 - id (WHERE id = ?)
            ps.setLong(7, entity.getId());

            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du RDV", e);
        }
    }


    @Override
    public void delete(RDV entity) {
        if (entity == null || entity.getId() == null) return;
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        String sql = "DELETE FROM rdv WHERE id = ?";

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la suppression du RDV", e);
        }
    }

    // =========================================================================
    //  Méthodes spécifiques
    // =========================================================================

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE date_rdv = ? ORDER BY heure";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par date", e);
        }
    }

    @Override
    public List<RDV> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM rdv WHERE patient_id = ? ORDER BY date_rdv, heure";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par patient", e);
        }
    }

    @Override
    public List<RDV> findByStatus(EtatRendezVous status) {
        if (status == null) return new ArrayList<>();

        // On utilise directement le nom de l'enum, qui correspond à l'ENUM SQL
        String statutDb = status.name();

        String sql = "SELECT * FROM rdv WHERE statut = ? ORDER BY date_rdv, heure";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, statutDb);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV par statut", e);
        }
    }

    @Override
    public List<RDV> findUpcomingFromToday() {
        String sql = "SELECT * FROM rdv WHERE date_rdv >= CURRENT_DATE ORDER BY date_rdv, heure";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV à venir", e);
        }
    }
    @Override
    public List<RDV> findByListeAttenteId(Long listeAttenteId) {
        String sql = "SELECT * FROM rdv WHERE liste_attente_id = ?";
        List<RDV> result = new ArrayList<>();

        try (Connection cn = JdbcUtils.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, listeAttenteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }

            return result;

        } catch (SQLException | DaoException e) {
            throw new RuntimeException("Erreur lors de la recherche des RDV pour liste_attente_id=" + listeAttenteId, e);
        }
    }

}
