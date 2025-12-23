package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class TestServicePatient {

    private final PatientService patientService =
            ApplicationContext.getBean(PatientService.class);

    private final AntecedentService antecedentService =
            ApplicationContext.getBean(AntecedentService.class);

    // =====================================================
    // PATIENT SERVICE
    // =====================================================
    void testPatientService() {
        System.out.println("\n=== TEST SERVICE PATIENT ===");

        List<Patient> patients = patientService.getAll();
        System.out.println("Patients = " + patients.size());

        if (!patients.isEmpty()) {
            Patient p = patients.get(0);
            System.out.println("Patient ID=" + p.getId()
                    + " | " + p.getNom() + " " + p.getPrenom()
                    + " | tel=" + p.getTelephone()
                    + " | assurance=" + p.getAssurance());

            // test getById
            Patient p2 = patientService.getById(p.getId());
            System.out.println("getById(" + p.getId() + ") = " + (p2 != null ? "OK" : "NULL"));

            // test antecedents du patient
            List<Antecedents> ants = antecedentService.getByPatientId(p.getId());
            System.out.println("Antecedents(patientId=" + p.getId() + ") = " + ants.size());
        }

        System.out.println("PatientService OK");
    }

    // =====================================================
    // ANTECEDENT SERVICE
    // =====================================================
    void testAntecedentService() {
        System.out.println("\n=== TEST SERVICE ANTECEDENT ===");

        List<Antecedents> all = antecedentService.getAll();
        System.out.println("Tous antecedents = " + all.size());

        if (!all.isEmpty()) {
            Antecedents a = all.get(0);
            System.out.println("Antecedent id=" + a.getId()
                    + " | patientId=" + a.getPatientId()
                    + " | nom=" + a.getNom()
                    + " | risque=" + a.getNiveauDeRisque());

            Antecedents a2 = antecedentService.getById(a.getId());
            System.out.println("getById(" + a.getId() + ") = " + (a2 != null ? "OK" : "NULL"));
        }

        System.out.println("AntecedentService OK");
    }

    // =====================================================
    // INSERT EXEMPLE (OPTIONNEL)
    // =====================================================
    void insertAntecedentExample() {
        System.out.println("\n=== INSERT ANTECEDENT (TEST SERVICE) ===");

        // ⚠️ patientId doit exister dans ta DB
        Long patientId = 1L;

        Antecedents a = Antecedents.builder()
                .patientId(patientId)
                .nom("TEST antecedent")
                .categorie("Medical")
                .niveauDeRisque(NiveauDeRisque.MOYEN)
                .description("Ajout depuis TestServicePatient")
                .build();

        antecedentService.create(a);
        System.out.println("Antecedent créé ID=" + a.getId());
    }

    // =====================================================
    // MAIN
    // =====================================================
    public static void main(String[] args) {
        try {
            TestServicePatient t = new TestServicePatient();

            t.testPatientService();
            t.testAntecedentService();

            // Décommente si tu veux tester INSERT (attention FK patientId)
            // t.insertAntecedentExample();

            System.out.println("\n✅ TEST SERVICE PATIENT TERMINÉ");
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR TEST SERVICE PATIENT : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
