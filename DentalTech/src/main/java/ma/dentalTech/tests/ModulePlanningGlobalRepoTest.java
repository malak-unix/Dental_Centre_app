package ma.dentalTech.tests;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.entities.enums.StatutJournee;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.entities.rdv.RDV;

import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.repository.modules.listeAttente.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.plageHoraire.api.PlageHoraireRepository;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ModulePlanningGlobalRepoTest {

    private static final AgendaMensuelRepository agendaRepo =
            new ma.dentalTech.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl();

    private static final DetailJourneeRepository detailRepo =
            new ma.dentalTech.repository.modules.agenda.impl.DetailJourneeRepositoryImpl();

    private static final PlageHoraireRepository plageRepo =
            new ma.dentalTech.repository.modules.plageHoraire.impl.PlageHoraireRepositoryImpl();

    private static final ListeAttenteRepository listeRepo =
            new ma.dentalTech.repository.modules.listeAttente.impl.ListeAttenteRepositoryImpl();

    private static final RdvRepository rdvRepo =
            new ma.dentalTech.repository.modules.rdv.impl.RdvRepositoryImpl();

    public static void main(String[] args) throws Exception {

        System.out.println("=============================================");
        System.out.println("TEST GLOBAL - MODULE RDV / AGENDA / PARAMETRAGE");
        System.out.println("=============================================");

        Long medecinId = ensureMedecinExists();
        Long patientId = ensurePatientExists();

        // -------------------------
        // INSERT AgendaMensuel
        // -------------------------
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(Mois.DECEMBRE)
                .annee(LocalDate.now().getYear())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        agendaRepo.create(agenda);
        System.out.println("[INSERT] AgendaMensuel id=" + agenda.getId());

        // SELECT
        System.out.println("[SELECT] AgendaMensuel findById -> " + (agendaRepo.findById(agenda.getId()) != null));
        System.out.println("[SELECT] AgendaMensuel findByMedecin -> count=" + agendaRepo.findByMedecin(medecinId).size());

        // UPDATE
        agenda.setModifiePar("TEST_UPDATE");
        agendaRepo.update(agenda);
        System.out.println("[UPDATE] AgendaMensuel OK");

        // -------------------------
        // INSERT DetailJournee
        // -------------------------
        DetailJournee detail = DetailJournee.builder()
                .agendaId(agenda.getId())
                .dateJour(LocalDate.now().plusDays(1))
                .heureDebutTravaillee(LocalTime.of(9, 0))
                .heureFinTravaillee(LocalTime.of(12, 0))
                .etatJour(StatutJournee.valueOf("OUVERT")) // ✅ état jour: OUVERT / FERME / FERIE / VACANCES
                .commentaire("Detail test")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        detailRepo.create(detail);
        System.out.println("[INSERT] DetailJournee id=" + detail.getId());

        // SELECT
        List<DetailJournee> details = detailRepo.findByAgendaId(agenda.getId());
        System.out.println("[SELECT] DetailJournee findByAgendaId -> count=" + details.size());

        // UPDATE
        detail.setCommentaire("Detail UPDATED");
        detail.setModifiePar("TEST_UPDATE");
        detailRepo.update(detail);
        System.out.println("[UPDATE] DetailJournee OK");

        // -------------------------
        // INSERT PlageHoraire
        // -------------------------
        PlageHoraire plage = PlageHoraire.builder()
                .detailJourneeId(detail.getId())
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(9, 30))
                .disponible(true)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        plageRepo.create(plage);
        System.out.println("[INSERT] PlageHoraire id=" + plage.getId());

        // SELECT
        System.out.println("[SELECT] PlageHoraire disponibles -> count=" +
                plageRepo.findDisponiblesByDetailJournee(detail.getId()).size());

        // UPDATE
        plage.setDisponible(false);
        plage.setModifiePar("TEST_UPDATE");
        plageRepo.update(plage);
        System.out.println("[UPDATE] PlageHoraire OK");

        // -------------------------
        // INSERT ListeAttente
        // -------------------------
        ListeAttente liste = ListeAttente.builder()
                .nomListe("Liste RDV Test")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        listeRepo.create(liste);
        System.out.println("[INSERT] ListeAttente id=" + liste.getId());

        // SELECT
        System.out.println("[SELECT] ListeAttente findAll -> count=" + listeRepo.findAll().size());

        // UPDATE
        liste.setNomListe("Liste RDV UPDATED");
        liste.setModifiePar("TEST_UPDATE");
        listeRepo.update(liste);
        System.out.println("[UPDATE] ListeAttente OK");

        // -------------------------
        // INSERT RDV
        // -------------------------
        RDV rdv = RDV.builder()
                .patientId(patientId)
                .detailJourneeId(detail.getId())
                .listeAttenteId(liste.getId())
                .date(detail.getDateJour())
                .heure(LocalTime.of(9, 0))
                .motif("Motif test")
                .status(EtatRendezVous.PREVU)
                .noteMedecin("note test")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        rdvRepo.create(rdv);
        System.out.println("[INSERT] RDV id=" + rdv.getId());

        // SELECT
        System.out.println("[SELECT] RDV findById -> " + (rdvRepo.findById(rdv.getId()) != null));
        System.out.println("[SELECT] RDV findByDate -> count=" + rdvRepo.findByDate(rdv.getDate()).size());

        // UPDATE
        rdv.setStatus(EtatRendezVous.CONFIRME);
        rdv.setModifiePar("TEST_UPDATE");
        rdvRepo.update(rdv);
        System.out.println("[UPDATE] RDV OK");

        // -------------------------
        // DELETE (ordre FK)
        // -------------------------
        rdvRepo.deleteById(rdv.getId());
        System.out.println("[DELETE] RDV OK");

        plageRepo.deleteById(plage.getId());
        System.out.println("[DELETE] PlageHoraire OK");

        detailRepo.deleteById(detail.getId());
        System.out.println("[DELETE] DetailJournee OK");

        agendaRepo.deleteById(agenda.getId());
        System.out.println("[DELETE] AgendaMensuel OK");

        listeRepo.deleteById(liste.getId());
        System.out.println("[DELETE] ListeAttente OK");

        System.out.println("=============================================");
        System.out.println("✅ TEST GLOBAL TERMINE AVEC SUCCES");
        System.out.println("=============================================");
    }

    // ========= Helpers DB pour FK =========

    private static Long ensurePatientExists() throws SQLException {
        try (Connection cn = SessionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = cn.prepareStatement("SELECT id FROM patient LIMIT 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

            long patientId;
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO patient(nom, prenom, telephone) VALUES ('PAT','TEST', ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, "06" + (System.currentTimeMillis() % 1000000000));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    patientId = keys.getLong(1);
                }
            }
            return patientId;
        }
    }


    private static Long ensureMedecinExists() throws SQLException {
        try (Connection cn = SessionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = cn.prepareStatement("SELECT id FROM medecin LIMIT 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

            cn.setAutoCommit(false);
            try {
                Long roleId = ensureRoleExists(cn, "MEDECIN");

                long utilisateurId;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO utilisateur(nom, prenom, email, login, mot_de_passe, actif, role_id) " +
                                "VALUES ('MED','TEST', ?, ?, 'pass', 1, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    String u = "med_" + System.currentTimeMillis();
                    ps.setString(1, u + "@mail.com");
                    ps.setString(2, u);
                    ps.setLong(3, roleId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        utilisateurId = keys.getLong(1);
                    }
                }


                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO staff(id, salaire, prime) VALUES (?,0,0)")) {
                    ps.setLong(1, utilisateurId);
                    ps.executeUpdate();
                }


                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO medecin(id, specialite) VALUES (?, 'GENERAL')")) {
                    ps.setLong(1, utilisateurId);
                    ps.executeUpdate();
                }

                cn.commit();
                cn.setAutoCommit(true);
                return utilisateurId;

            } catch (Exception e) {
                cn.rollback();
                cn.setAutoCommit(true);
                throw new SQLException("Erreur création medecin test", e);
            }
        }
    }

    private static Long ensureRoleExists(Connection cn, String libelle) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT id FROM role WHERE libelle=? LIMIT 1")) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO role(libelle) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libelle);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

}
