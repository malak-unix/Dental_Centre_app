package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.consultation.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    // =========================================================================
    // Mapping ResultSet -> Consultation
    // =========================================================================
    private Consultation map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        if (rs.wasNull()) id = null;

        Long dossierId = rs.getLong("dossier_id");
        if (rs.wasNull()) dossierId = null;

        // date_consultation (DATETIME -> LocalDate)
        LocalDate date = null;
        Timestamp tsDate = rs.getTimestamp("date_consultation");
        if (tsDate != null) {
            date = tsDate.toLocalDateTime().toLocalDate();
        }

        // audit
        LocalDateTime dateCreation = null;
        Timestamp tCreate = rs.getTimestamp("date_creation");
        if (tCreate != null) dateCreation = tCreate.toLocalDateTime();

        LocalDateTime dateModif = null;
        Timestamp tModif = rs.getTimestamp("date_modification");
        if (tModif != null) dateModif = tModif.toLocalDateTime();

        // statut
        StatutConsultation statut = null;
        String st = rs.getString("statut");
        if (st != null && !st.isBlank()) {
            try {
                statut = StatutConsultation.valueOf(st.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }

        return Consultation.builder()
                .id(id)
                .dossierId(dossierId)
                .date(date)
                .status(statut)
                .observationMedecin(rs.getString("observation_medecin"))
                .dateCreation(dateCreation)
                .dateDerniereModification(dateModif)
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public void create(Consultation c) {
        String sql = """
                INSERT INTO consultation
                (dossier_id, date_consultation, statut, observation_medecin,
                 date_creation, cree_par, modifie_par)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // dossier_id (NOT NULL selon script SQL)
            ps.setLong(1, c.getDossierId());

            // date_consultation : on met midi par défaut si tu n'as pas l'heure
            LocalDateTime dateTime = c.getDate() != null
                    ? c.getDate().atTime(12, 0)
                    : LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dateTime));

            // statut
            if (c.getStatus() != null) {
                ps.setString(3, c.getStatus().name());
            } else {
                ps.setString(3, StatutConsultation.PLANIFIE.name()); // valeur par défaut
            }

            ps.setString(4, c.getObservationMedecin());

            LocalDateTime dc = (c.getDateCreation() != null) ? c.getDateCreation() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dc));

            ps.setString(6, c.getCreePar());
            ps.setString(7, c.getModifiePar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public void update(Consultation c) {
        String sql = """
                UPDATE consultation
                   SET dossier_id = ?,
                       date_consultation = ?,
                       statut = ?,
                       observation_medecin = ?,
                       date_modification = ?,
                       modifie_par = ?
                 WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, c.getDossierId());

            LocalDateTime dateTime = c.getDate() != null
                    ? c.getDate().atTime(12, 0)
                    : LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dateTime));

            if (c.getStatus() != null) {
                ps.setString(3, c.getStatus().name());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            ps.setString(4, c.getObservationMedecin());

            LocalDateTime dm = (c.getDateDerniereModification() != null)
                    ? c.getDateDerniereModification()
                    : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));

            ps.setString(6, c.getModifiePar());
            ps.setLong(7, c.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM consultation WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return null;
    }

    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM consultation ORDER BY date_consultation DESC, id DESC";
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }

    @Override
    public void delete(Consultation c) {
        if (c != null && c.getId() != null) deleteById(c.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM consultation WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

    }

    // =========================================================================
    // Méthodes spécifiques
    // =========================================================================

    @Override
    public List<Consultation> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM consultation WHERE dossier_id = ? ORDER BY date_consultation DESC, id DESC";
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        String sql = """
                SELECT * FROM consultation
                 WHERE DATE(date_consultation) = ?
                 ORDER BY date_consultation DESC, id DESC
                """;
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
                SELECT * FROM consultation
                 WHERE DATE(date_consultation) BETWEEN ? AND ?
                 ORDER BY date_consultation DESC, id DESC
                """;
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        String sql = "SELECT * FROM consultation WHERE statut = ? ORDER BY date_consultation DESC, id DESC";
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) AS total FROM consultation";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong("total");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return 0;
    }

    @Override
    public List<Consultation> findPage(int limit, int offset) {
        String sql = """
                SELECT * FROM consultation
                 ORDER BY date_consultation DESC, id DESC
                 LIMIT ? OFFSET ?
                """;
        List<Consultation> list = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }

        return list;
    }
    @Override
    public Integer countTermineesPourMedecin(Long medecinId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        // TEMP (Aya): stub pour compilation. À remplacer par vraie requête SQL.
        return 0;
    }

    @Override
    public Integer countEnCoursPourMedecin(Long medecinId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        // TEMP (Aya): stub pour compilation. À remplacer par vraie requête SQL.
        return 0;
    }


}
