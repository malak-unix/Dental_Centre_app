package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class TestServicePatient {

    private final PatientService patientService = ApplicationContext.getBean(PatientService.class);
    private final AntecedentService antecedentService = ApplicationContext.getBean(AntecedentService.class);

    private Long patientId;
    private Long antecedentId;

    // =====================================================
    // PATIENT CRUD
    // =====================================================
    private void testPatientCRUD() {
        System.out.println("\n=== [CRUD] PATIENT ===");

        // CREATE (schema.sql: nom, prenom NOT NULL)
        Patient p = Patient.builder()
                .nom("BERDAY_TEST")
                .prenom("AICHA")
                .telephone("0611111111")
                .adresse("Casablanca")
                // email existe dans entity mais PAS dans schema.sql -> on laisse null
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        patientService.create(p);

        patientId = p.getId();
        if (patientId == null) throw new IllegalStateException("CREATE Patient: id null");
        System.out.println("✅ Patient créé id=" + patientId);

        // READ
        Patient loaded = patientService.getById(patientId);
        if (loaded == null) throw new IllegalStateException("READ Patient: getById null");

        // UPDATE
        loaded.setAdresse("Rabat");
        patientService.update(loaded);

        Patient updated = patientService.getById(patientId);
        if (updated == null) throw new IllegalStateException("UPDATE Patient: relire null");
        if (!"Rabat".equals(updated.getAdresse())) throw new IllegalStateException("UPDATE Patient: adresse non modifiée");
        System.out.println("✅ Patient modifié");

        // SEARCH
        List<Patient> found = patientService.searchByNom("BERDAY_TEST");
        System.out.println("Recherche 'BERDAY_TEST' => " + found.size());
    }

    // =====================================================
    // ANTECEDENT CRUD
    // =====================================================
    private void testAntecedentCRUD() {
        System.out.println("\n=== [CRUD] ANTECEDENT ===");
        if (patientId == null) throw new IllegalStateException("Antecedent nécessite patientId (crée Patient avant).");

        // CREATE (schema.sql antecedent.patient_id NOT NULL)
        Antecedents a = Antecedents.builder()
                .patientId(patientId)
                .nom("Diabète")
                .categorie("Medical")
                .niveauDeRisque(NiveauDeRisque.MOYEN)
                .description("Ajout TestServicePatient")
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        antecedentService.create(a);

        antecedentId = a.getId();
        if (antecedentId == null) throw new IllegalStateException("CREATE Antecedent: id null");
        System.out.println("✅ Antecedent créé id=" + antecedentId);

        // READ
        Antecedents loaded = antecedentService.getById(antecedentId);
        if (loaded == null) throw new IllegalStateException("READ Antecedent: getById null");

        // UPDATE
        loaded.setDescription("MODIF - " + loaded.getDescription());
        antecedentService.update(loaded);

        Antecedents updated = antecedentService.getById(antecedentId);
        if (updated == null) throw new IllegalStateException("UPDATE Antecedent: relire null");
        if (updated.getDescription() == null || !updated.getDescription().startsWith("MODIF")) {
            throw new IllegalStateException("UPDATE Antecedent: description non modifiée");
        }
        System.out.println("✅ Antecedent modifié");

        // LIST BY PATIENT
        System.out.println("Antecedents du patientId=" + patientId + " => " + antecedentService.getByPatientId(patientId).size());
    }

    // =====================================================
    // CLEANUP
    // =====================================================
    private void cleanup() {
        System.out.println("\n=== CLEANUP PATIENT TEST ===");

        if (antecedentId != null) {
            try {
                antecedentService.deleteById(antecedentId);
                System.out.println("🧹 Antecedent supprimé id=" + antecedentId);
            } catch (Exception e) {
                System.out.println("⚠️ Cleanup antecedent échoué: " + e.getMessage());
            }
        }

        if (patientId != null) {
            try {
                patientService.deleteById(patientId);
                System.out.println("🧹 Patient supprimé id=" + patientId);
            } catch (Exception e) {
                System.out.println("⚠️ Cleanup patient échoué: " + e.getMessage());
            }
        }
    }

    // =====================================================
    // MAIN
    // =====================================================
    public static void main(String[] args) {
        TestServicePatient t = new TestServicePatient();
        try {
            System.out.println("\n=== TEST SERVICE PATIENT (CRUD COMPLET) ===");

            t.testPatientCRUD();
            t.testAntecedentCRUD();
            t.cleanup();

            System.out.println("\n✅ TEST SERVICE PATIENT (CRUD) TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE PATIENT : " + e.getMessage());
            e.printStackTrace();
            t.cleanup();
        }
    }
}
