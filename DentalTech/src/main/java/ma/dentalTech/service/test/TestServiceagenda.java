package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.agenda.RDV;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.Mois;

import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.api.RdvService;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;
import ma.dentalTech.service.modules.agenda.api.PlageHoraireService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestServiceagenda {

    private final AgendaService agendaService = ApplicationContext.getBean(AgendaService.class);
    private final RdvService rdvService = ApplicationContext.getBean(RdvService.class);
    private final ListeAttenteService listeAttenteService = ApplicationContext.getBean(ListeAttenteService.class);
    private final PlageHoraireService plageHoraireService = ApplicationContext.getBean(PlageHoraireService.class);

    private Long agendaId;
    private Long detailId;
    private Long plageId;
    private Long listeId;
    private Long rdvId;

    private Long medecinId;
    private Long patientId;

    private void prepareFkData() {
        System.out.println("\n=== PREPARE FK DATA (MEDECIN + PATIENT) ===");
        this.medecinId = ensureMedecinExists();
        System.out.println("✅ medecinId utilisé = " + medecinId);

        this.patientId = ensurePatientExists();
        System.out.println("✅ patientId utilisé = " + patientId);
    }

    private Long ensureMedecinExists() {
        try (Connection cn = SessionFactory.getInstance().getConnection()) {

            Long existing = selectOneLong(cn, "SELECT id FROM medecin LIMIT 1");
            if (existing != null) return existing;

            System.out.println("⚠️ Aucun medecin trouvé. Création chaîne utilisateur -> staff -> medecin ...");

            Long roleId = selectOneLong(cn, "SELECT id FROM role WHERE libelle='MEDECIN' LIMIT 1");
            if (roleId == null) {
                roleId = insertAndGetId(cn,
                        "INSERT INTO role(libelle, privileges, cree_par, modifie_par) VALUES('MEDECIN', NULL, 'TEST_AICHA', 'TEST_AICHA')");
                System.out.println("  - role MEDECIN créé id=" + roleId);
            } else {
                System.out.println("  - role MEDECIN existe id=" + roleId);
            }

            final Long roleIdFinal = roleId;

            String uniq = String.valueOf(System.currentTimeMillis());
            String login = "med_test_" + uniq;
            String email = "med_test_" + uniq + "@test.ma";

            Long utilisateurId = insertAndGetId(cn,
                    "INSERT INTO utilisateur(nom, prenom, email, adresse, tel, sexe, login, mot_de_passe, actif, cree_par, modifie_par, role_id) " +
                            "VALUES(?,?,?,?,?,'HOMME',?,?,TRUE,'TEST_AICHA','TEST_AICHA',?) ",
                    ps -> {
                        ps.setString(1, "MEDECIN");
                        ps.setString(2, "TEST");
                        ps.setString(3, email);
                        ps.setString(4, "Casablanca");
                        ps.setString(5, "0600000000");
                        ps.setString(6, login);
                        ps.setString(7, "pass");
                        ps.setLong(8, roleIdFinal);
                    }
            );
            System.out.println("  - utilisateur créé id=" + utilisateurId + " login=" + login);

            exec(cn,
                    "INSERT INTO staff(id, salaire, prime, date_recrutement, solde_conge, cabinet_id, cree_par, modifie_par) " +
                            "VALUES(?,0,0,CURDATE(),0,NULL,'TEST_AICHA','TEST_AICHA')",
                    ps -> ps.setLong(1, utilisateurId)
            );
            System.out.println("  - staff créé id=" + utilisateurId);

            exec(cn,
                    "INSERT INTO medecin(id, specialite, cree_par, modifie_par) VALUES(?, 'Dentiste', 'TEST_AICHA', 'TEST_AICHA')",
                    ps -> ps.setLong(1, utilisateurId)
            );
            System.out.println("  - medecin créé id=" + utilisateurId);

            return utilisateurId;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ensureMedecinExists(): " + e.getMessage(), e);
        }
    }

    private Long ensurePatientExists() {
        try (Connection cn = SessionFactory.getInstance().getConnection()) {

            Long existing = selectOneLong(cn, "SELECT id FROM patient LIMIT 1");
            if (existing != null) return existing;

            System.out.println("⚠️ Aucun patient trouvé. Création patient ...");

            Long id = insertAndGetId(cn,
                    "INSERT INTO patient(nom, prenom, date_naissance, sexe, telephone, adresse, num_affiliation, etat_civil, assurance, cree_par, modifie_par) " +
                            "VALUES(?, ?, NULL, 'H', ?, ?, NULL, 'CELIBATAIRE', 'AUCUNE', 'TEST_AICHA', 'TEST_AICHA')",
                    ps -> {
                        ps.setString(1, "BERDAY_TEST");
                        ps.setString(2, "AICHA");
                        ps.setString(3, "0611111111");
                        ps.setString(4, "Casablanca");
                    }
            );

            System.out.println("  - patient créé id=" + id);
            return id;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ensurePatientExists(): " + e.getMessage(), e);
        }
    }

    private void testAgendaCRUD() {
        System.out.println("\n=== [CRUD] AGENDA MENSUEL + DETAIL JOURNEE ===");

        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecinId(medecinId)
                .mois(Mois.DECEMBRE)
                .annee(2025)
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        agendaService.createAgenda(agenda);

        agendaId = agenda.getId();
        if (agendaId == null) throw new IllegalStateException("CREATE AgendaMensuel: id null");
        System.out.println("✅ AgendaMensuel créé id=" + agendaId);

        AgendaMensuel loadedAgenda = agendaService.getAgendaById(agendaId);
        if (loadedAgenda == null) throw new IllegalStateException("READ AgendaMensuel: null");

        DetailJournee dj = DetailJournee.builder()
                .agendaId(agendaId)
                .dateJour(LocalDate.now().plusDays(2))
                .heureDebutTravail(LocalTime.of(9, 0))
                .heureFinTravail(LocalTime.of(17, 0))
                .commentaire("Journee test - AICHA")
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        agendaService.createDetail(dj);

        detailId = dj.getId();
        if (detailId == null) throw new IllegalStateException("CREATE DetailJournee: id null");
        System.out.println("✅ DetailJournee créé id=" + detailId + " date=" + dj.getDateJour());

        DetailJournee loadedDj = agendaService.getDetailById(detailId);
        loadedDj.setCommentaire("MODIF - commentaire (TestServiceagenda)");
        agendaService.updateDetail(loadedDj);

        DetailJournee updatedDj = agendaService.getDetailById(detailId);
        if (updatedDj == null || updatedDj.getCommentaire() == null || !updatedDj.getCommentaire().contains("MODIF")) {
            throw new IllegalStateException("UPDATE DetailJournee: commentaire non modifié");
        }
        System.out.println("✅ DetailJournee modifié");

        List<DetailJournee> details = agendaService.getDetailsByAgenda(agendaId);
        System.out.println("Détails par agendaId=" + agendaId + " => " + details.size());
    }

    private void testPlageHoraireCRUD() {
        System.out.println("\n=== [CRUD] PLAGE HORAIRE ===");
        if (detailId == null) throw new IllegalStateException("PlageHoraire nécessite detailId.");

        PlageHoraire p = PlageHoraire.builder()
                .detailJourneeId(detailId)
                .heureDebut(LocalTime.of(10, 0))
                .heureFin(LocalTime.of(10, 30))
                .disponible(true)
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        plageHoraireService.create(p);

        plageId = p.getId();
        if (plageId == null) throw new IllegalStateException("CREATE PlageHoraire: id null");
        System.out.println("✅ PlageHoraire créée id=" + plageId);

        PlageHoraire loaded = plageHoraireService.getById(plageId);
        loaded.setDisponible(false);
        plageHoraireService.update(loaded);

        PlageHoraire updated = plageHoraireService.getById(plageId);
        if (updated == null || Boolean.TRUE.equals(updated.getDisponible())) {
            throw new IllegalStateException("UPDATE PlageHoraire: disponible non modifié");
        }
        System.out.println("✅ PlageHoraire modifiée");

        System.out.println("Plages par detailJourneeId=" + detailId + " => " + plageHoraireService.getByDetailJournee(detailId).size());
        System.out.println("Plages disponibles par detailJourneeId=" + detailId + " => " + plageHoraireService.getDisponiblesByDetailJournee(detailId).size());
    }

    private void testListeAttenteCRUD() {
        System.out.println("\n=== [CRUD] LISTE ATTENTE ===");

        ListeAttente l = ListeAttente.builder()
                .nom("LISTE_TEST_AICHA_" + System.currentTimeMillis())
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        listeAttenteService.create(l);

        listeId = l.getId();
        if (listeId == null) throw new IllegalStateException("CREATE ListeAttente: id null");
        System.out.println("✅ ListeAttente créée id=" + listeId);

        ListeAttente loaded = listeAttenteService.getById(listeId);
        loaded.setNom(loaded.getNom() + "_MODIF");
        listeAttenteService.update(loaded);

        ListeAttente updated = listeAttenteService.getById(listeId);
        if (updated == null || updated.getNom() == null || !updated.getNom().endsWith("_MODIF")) {
            throw new IllegalStateException("UPDATE ListeAttente: nom non modifié");
        }
        System.out.println("✅ ListeAttente modifiée");

        System.out.println("Recherche 'LISTE_TEST_AICHA' => " + listeAttenteService.searchByNomListe("LISTE_TEST_AICHA").size());
    }

    private void testRdvCRUD() {
        System.out.println("\n=== [CRUD] RDV ===");
        if (detailId == null) throw new IllegalStateException("RDV nécessite detailId.");

        RDV r = RDV.builder()
                .patientId(patientId)
                .detailJourneeId(detailId)
                .listeAttenteId(listeId)
                .dateRdv(LocalDate.now().plusDays(2))
                .heure(LocalTime.of(11, 0))
                .motif("RDV TestServiceagenda")
                .statut(EtatRendezVous.PLANIFIE) // ✅ enum
                .noteMedecin("Note test")
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        rdvService.create(r);

        rdvId = r.getId();
        if (rdvId == null) throw new IllegalStateException("CREATE RDV: id null");
        System.out.println("✅ RDV créé id=" + rdvId);

        RDV loaded = rdvService.getById(rdvId);
        loaded.setMotif("MODIF - motif (TestServiceagenda)");
        loaded.setStatut(EtatRendezVous.TERMINE); // ✅ enum
        rdvService.update(loaded);

        RDV updated = rdvService.getById(rdvId);
        if (updated == null || updated.getMotif() == null || !updated.getMotif().contains("MODIF")) {
            throw new IllegalStateException("UPDATE RDV: motif non modifié");
        }
        if (updated.getStatut() != EtatRendezVous.TERMINE) {
            throw new IllegalStateException("UPDATE RDV: statut non modifié");
        }
        System.out.println("✅ RDV modifié");

        System.out.println("RDV par date=" + r.getDateRdv() + " => " + rdvService.getByDate(r.getDateRdv()).size());
        System.out.println("RDV par patientId=" + patientId + " => " + rdvService.getByPatient(patientId).size());
        System.out.println("RDV à venir => " + rdvService.getUpcomingFromToday().size());
    }

    private void cleanup() {
        System.out.println("\n=== CLEANUP AGENDA TEST ===");

        if (rdvId != null) safe(() -> rdvService.deleteById(rdvId), "RDV supprimé id=" + rdvId);
        if (plageId != null) safe(() -> plageHoraireService.deleteById(plageId), "PlageHoraire supprimée id=" + plageId);
        if (detailId != null) safe(() -> agendaService.deleteDetailById(detailId), "DetailJournee supprimé id=" + detailId);
        if (agendaId != null) safe(() -> agendaService.deleteAgendaById(agendaId), "AgendaMensuel supprimé id=" + agendaId);
        if (listeId != null) safe(() -> listeAttenteService.deleteById(listeId), "ListeAttente supprimée id=" + listeId);
    }

    private void safe(Runnable r, String okMsg) {
        try {
            r.run();
            System.out.println("🧹 " + okMsg);
        } catch (Exception e) {
            System.out.println("⚠️ Cleanup: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TestServiceagenda t = new TestServiceagenda();
        try {
            System.out.println("\n=== TEST SERVICE AGENDA (CRUD COMPLET) ===");

            t.prepareFkData();

            t.testAgendaCRUD();
            t.testPlageHoraireCRUD();
            t.testListeAttenteCRUD();
            t.testRdvCRUD();

            t.cleanup();

            System.out.println("\n✅ TEST SERVICE AGENDA (CRUD) TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE AGENDA : " + e.getMessage());
            e.printStackTrace();
            t.cleanup();
        }
    }

    // JDBC helpers
    private Long selectOneLong(Connection cn, String sql) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long v = rs.getLong(1);
                return rs.wasNull() ? null : v;
            }
            return null;
        }
    }

    private void exec(Connection cn, String sql, SQLConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            if (binder != null) binder.accept(ps);
            ps.executeUpdate();
        }
    }

    private Long insertAndGetId(Connection cn, String sql) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Insertion OK mais aucun id généré (sql=" + sql + ")");
    }

    private Long insertAndGetId(Connection cn, String sql, SQLConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            binder.accept(ps);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Insertion OK mais aucun id généré");
    }

    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
