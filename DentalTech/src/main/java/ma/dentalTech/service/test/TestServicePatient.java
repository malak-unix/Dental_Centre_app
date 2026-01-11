package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.time.LocalDate;
import java.util.List;

public class TestServicePatient {

    private final PatientService patientService = ApplicationContext.getBean(PatientService.class);

    private Long patientId;

    public static void main(String[] args) {
        TestServicePatient t = new TestServicePatient();
        try {
            System.out.println("\n=== TEST SERVICE PATIENT (CRUD COMPLET) ===");
            t.testPatientCRUD();
            System.out.println("\n✅ TEST SERVICE PATIENT (CRUD) TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE PATIENT : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\n=== CLEANUP PATIENT TEST ===");
            t.cleanup();
        }
    }

    private void testPatientCRUD() {
        System.out.println("\n=== [CRUD] PATIENT ===");

        // -------------------------
        // CREATE (données uniques)
        // -------------------------
        String uniq = String.valueOf(System.currentTimeMillis());
        String telUnique = "06" + uniq.substring(Math.max(0, uniq.length() - 8)); // ex: 06XXXXXXXX

        Patient p = Patient.builder()
                .nom("PATIENT_TEST")
                .prenom("AICHA")
                .dateNaissance(LocalDate.of(2000, 1, 1))
                .sexe(Sexe.Homme) // adapte si ton enum = Homme/Femme
                .telephone(telUnique)
                .adresse("Casablanca")
                .numAffiliation("AFF-" + uniq)
                .assurance(Assurance.AUCUNE)
                .creePar("TEST_AICHA")
                .modifiePar("TEST_AICHA")
                .build();

        patientService.create(p);
        patientId = p.getId();

        if (patientId == null) throw new IllegalStateException("CREATE Patient: id null");
        System.out.println("✅ Patient créé id=" + patientId + " tel=" + telUnique);

        // -------------------------
        // READ
        // -------------------------
        Patient loaded = patientService.getById(patientId);
        if (loaded == null) throw new IllegalStateException("READ Patient: null");
        System.out.println("✅ Patient lu id=" + loaded.getId());

        // -------------------------
        // UPDATE (changer adresse + assurance)
        // -------------------------
        loaded.setAdresse("Rabat - MODIF");
        loaded.setAssurance(Assurance.AUTRE);
        loaded.setModifiePar("TEST_AICHA");

        patientService.update(loaded);

        Patient updated = patientService.getById(patientId);
        if (updated == null) throw new IllegalStateException("UPDATE Patient: re-read null");
        if (updated.getAdresse() == null || !updated.getAdresse().contains("MODIF")) {
            throw new IllegalStateException("UPDATE Patient: adresse non modifiée");
        }
        if (updated.getAssurance() != Assurance.AUTRE) {
            throw new IllegalStateException("UPDATE Patient: assurance non modifiée");
        }
        System.out.println("✅ Patient modifié");

        // -------------------------
        // SEARCH by nom
        // -------------------------
        List<Patient> byNom = patientService.searchByNom("PATIENT_TEST");
        if (byNom == null || byNom.isEmpty()) throw new IllegalStateException("SEARCH by nom: aucun résultat");
        System.out.println("Recherche par nom => " + byNom.size());

        // -------------------------
        // SEARCH by telephone
        // -------------------------
        Patient byTel = patientService.getByTelephone(telUnique);
        if (byTel == null) throw new IllegalStateException("SEARCH by tel: null");
        if (!telUnique.equals(byTel.getTelephone())) throw new IllegalStateException("SEARCH by tel: telephone mismatch");
        System.out.println("✅ Recherche par téléphone OK");

        // -------------------------
        // VALIDATION: create avec même tel => doit échouer
        // -------------------------
        try {
            Patient p2 = Patient.builder()
                    .nom("PATIENT_TEST_2")
                    .prenom("AICHA")
                    .telephone(telUnique) // même tel => interdit
                    .creePar("TEST_AICHA")
                    .modifiePar("TEST_AICHA")
                    .build();

            patientService.create(p2);
            throw new IllegalStateException("VALIDATION: create doublon tel aurait dû échouer !");
        } catch (Exception expected) {
            System.out.println("✅ Validation doublon téléphone: OK (" + expected.getMessage() + ")");
        }

        // -------------------------
        // DELETE sera fait dans cleanup()
        // -------------------------
    }

    private void cleanup() {
        if (patientId == null) return;
        try {
            patientService.deleteById(patientId);
            System.out.println("🧹 Patient supprimé id=" + patientId);
        } catch (Exception e) {
            System.out.println("⚠️ Cleanup patient: " + e.getMessage());
        }
    }
}
