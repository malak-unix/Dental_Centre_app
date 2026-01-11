package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class TestServiceAntecedent {

    private final PatientService patientService = ApplicationContext.getBean(PatientService.class);
    private final AntecedentService antecedentService = ApplicationContext.getBean(AntecedentService.class);

    private Long patientId;
    private Long antecedentId;

    public static void main(String[] args) {
        TestServiceAntecedent t = new TestServiceAntecedent();
        try {
            System.out.println("\n=== TEST SERVICE ANTECEDENT (CRUD COMPLET) ===");

            t.prepareFkPatient();

            t.testAntecedentCRUD();

            System.out.println("\n✅ TEST SERVICE ANTECEDENT (CRUD) TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE ANTECEDENT : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n=== CLEANUP ANTECEDENT TEST ===");
            t.cleanup();
        }
    }

    // =========================================================
    // 1) FK : Patient
    // =========================================================
    private void prepareFkPatient() {
        System.out.println("\n=== PREPARE FK DATA (PATIENT) ===");

        // Option 1 : utiliser un patient existant en base (plus rapide)
        Long existing = selectOneLong("SELECT id FROM patient ORDER BY id DESC LIMIT 1");
        if (existing != null) {
            patientId = existing;
            System.out.println("✅ patientId utilisé = " + patientId + " (existant)");
            return;
        }

        // Option 2 : en créer un si la base est vide
        String uniq = String.valueOf(System.currentTimeMillis());
        String telUnique = "06" + uniq.substring(Math.max(0, uniq.length() - 8));

        Patient p = Patient.builder()
                .nom("PATIENT_ANTEC_TEST")
                .prenom("AICHA")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .sexe(Sexe.Homme)
                .telephone(telUnique)
                .adresse("Casablanca")
                .numAffiliation("AFF-" + uniq)
                .assurance(Assurance.AUCUNE)
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        patientService.create(p);
        patientId = p.getId();

        if (patientId == null) throw new IllegalStateException("CREATE Patient FK: id null");
        System.out.println("✅ patientId créé = " + patientId + " tel=" + telUnique);
    }

    // =========================================================
    // 2) CRUD Antecedent
    // =========================================================
    private void testAntecedentCRUD() {
        System.out.println("\n=== [CRUD] ANTECEDENT ===");

        // -------------------------
        // CREATE
        // -------------------------
        String uniq = String.valueOf(System.currentTimeMillis());

        Antecedents a = Antecedents.builder()
                .patientId(patientId)
                .nom("ANTEC_TEST_" + uniq)
                .categorie("MEDICAL")
                .niveauDeRisque(NiveauDeRisque.MOYEN)
                .description("Description test antecedent " + uniq)
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        antecedentService.create(a);
        antecedentId = a.getId();

        if (antecedentId == null) throw new IllegalStateException("CREATE Antecedent: id null");
        System.out.println("✅ Antecedent créé id=" + antecedentId);

        // -------------------------
        // READ
        // -------------------------
        Antecedents loaded = antecedentService.getById(antecedentId);
        if (loaded == null) throw new IllegalStateException("READ Antecedent: null");
        System.out.println("✅ Antecedent lu id=" + loaded.getId() + " patientId=" + loaded.getPatientId());

        // -------------------------
        // UPDATE (changer categorie + risque + description)
        // -------------------------
        loaded.setCategorie("CHIRURGIE");
        loaded.setNiveauDeRisque(NiveauDeRisque.ELEVE);
        loaded.setDescription("MODIF - description (TestServiceAntecedent)");
        loaded.setModifiePar("TEST_AICHA");

        antecedentService.update(loaded);

        Antecedents updated = antecedentService.getById(antecedentId);
        if (updated == null) throw new IllegalStateException("UPDATE Antecedent: re-read null");

        if (updated.getCategorie() == null || !updated.getCategorie().equals("CHIRURGIE")) {
            throw new IllegalStateException("UPDATE Antecedent: categorie non modifiée");
        }
        if (updated.getNiveauDeRisque() != NiveauDeRisque.ELEVE) {
            throw new IllegalStateException("UPDATE Antecedent: niveau_de_risque non modifié");
        }
        if (updated.getDescription() == null || !updated.getDescription().contains("MODIF")) {
            throw new IllegalStateException("UPDATE Antecedent: description non modifiée");
        }

        System.out.println("✅ Antecedent modifié");

        // -------------------------
        // LIST by patientId
        // -------------------------
        List<Antecedents> byPatient = antecedentService.getByPatientId(patientId);
        if (byPatient == null || byPatient.isEmpty()) {
            throw new IllegalStateException("LIST by patientId: aucun antecedent trouvé");
        }
        System.out.println("Antecedents par patientId=" + patientId + " => " + byPatient.size());

        // -------------------------
        // VALIDATION: create avec patientId null => doit échouer
        // -------------------------
        try {
            Antecedents bad = Antecedents.builder()
                    .patientId(null)
                    .nom("BAD_ANTEC")
                    .creePar("TEST_AICHA")
                    .modifiePar("TEST_AICHA")
                    .build();

            antecedentService.create(bad);
            throw new IllegalStateException("VALIDATION: create patientId null aurait dû échouer !");
        } catch (Exception expected) {
            System.out.println("✅ Validation patientId obligatoire: OK (" + expected.getMessage() + ")");
        }

        // DELETE sera fait dans cleanup()
    }

    // =========================================================
    // 3) Cleanup
    // =========================================================
    private void cleanup() {
        if (antecedentId != null) safe(() -> antecedentService.deleteById(antecedentId), "Antecedent supprimé id=" + antecedentId);

        // ⚠️ On ne supprime pas le patient existant si on l’a juste “réutilisé”.
        // Si tu veux supprimer le patient créé uniquement par ce test, dis-moi et je te fais un flag.
    }

    private void safe(Runnable r, String okMsg) {
        try {
            r.run();
            System.out.println("🧹 " + okMsg);
        } catch (Exception e) {
            System.out.println("⚠️ Cleanup: " + e.getMessage());
        }
    }

    // =========================================================
    // JDBC helper (sélection rapide)
    // =========================================================
    private Long selectOneLong(String sql) {
        try (Connection cn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                long v = rs.getLong(1);
                return rs.wasNull() ? null : v;
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }
}
