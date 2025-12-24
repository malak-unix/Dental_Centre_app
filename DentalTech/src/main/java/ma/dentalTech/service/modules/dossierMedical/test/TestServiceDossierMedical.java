package ma.dentalTech.service.modules.dossierMedical.test;

import ma.dentalTech.configuration.ApplicationContext;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.entities.enums.StatutConsultation;

import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.dossierMedical.api.*;

import java.time.LocalDate;
import java.util.List;

public class TestServiceDossierMedical {

    // ==========================================================
    // Services (DI via ApplicationContext)
    // ==========================================================
    private final PatientService patientService =
            ApplicationContext.getBean(PatientService.class);

    private final DossierMedicalService dossierMedicalService =
            ApplicationContext.getBean(DossierMedicalService.class);

    private final ConsultationService consultationService =
            ApplicationContext.getBean(ConsultationService.class);

    private final InterventionMedecinService interventionService =
            ApplicationContext.getBean(InterventionMedecinService.class);

    private final ActeService acteService =
            ApplicationContext.getBean(ActeService.class);

    private final MedicamentService medicamentService =
            ApplicationContext.getBean(MedicamentService.class);

    private final OrdonnanceService ordonnanceService =
            ApplicationContext.getBean(OrdonnanceService.class);

    private final PrescriptionService prescriptionService =
            ApplicationContext.getBean(PrescriptionService.class);

    private final CertificatService certificatService =
            ApplicationContext.getBean(CertificatService.class);

    // ==========================================================
    // IDs gardés entre les méthodes
    // ==========================================================
    private Long patientId;
    private Long dossierId;
    private Long acteId;
    private Long consultationId;
    private Long interventionId;

    private Long medicamentId;
    private Long ordonnanceId;
    private Long certificatId;

    // ==========================================================
    // Mini assertion helpers (sans JUnit)
    // ==========================================================
    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError("ASSERT FAILED: " + msg);
    }

    private boolean servicesOk() {
        return patientService != null
                && dossierMedicalService != null
                && consultationService != null
                && interventionService != null
                && acteService != null
                && medicamentService != null
                && ordonnanceService != null
                && prescriptionService != null
                && certificatService != null;
    }

    // ==========================================================
    // 1) INSERT
    // ==========================================================
    void insertProcess() {
        System.out.println("\n=== SERVICE TEST : INSERT PROCESS ===");

        try {
            if (!servicesOk()) {
                System.out.println("❌ Services manquants (beans.properties / ApplicationContext). Stop.");
                return;
            }

            // 1) Patient
            Patient p = Patient.builder()
                    .nom("SVC_TEST")
                    .prenom("PATIENT")
                    .telephone("0600000000")
                    .adresse("ADDR SVC TEST")
                    .dateNaissance(LocalDate.of(2000, 1, 1))
                    .build();

            int beforePatients = patientService.getAll().size();
            patientService.create(p);

            patientId = p.getId();
            assertTrue(patientId != null, "patientId doit être généré");
            assertTrue(patientService.getAll().size() == beforePatients + 1, "patient count +1");
            System.out.println("✅ Patient créé: id=" + patientId);

            // 2) DossierMedical
            DossierMedical dm = DossierMedical.builder()
                    .patientId(patientId)
                    .notes("DM créé via service")
                    .build();

            dossierMedicalService.create(dm);
            dossierId = dm.getId();
            assertTrue(dossierId != null, "dossierId généré");
            System.out.println("✅ DossierMedical créé: id=" + dossierId);

            // 3) Acte
            Acte acte = Acte.builder()
                    .libelle("ACTE SERVICE TEST")
                    .categorie("TEST")
                    .prixBase(250.0)
                    .description("Acte créé via service")
                    .build();

            acteService.create(acte);
            acteId = acte.getId();
            assertTrue(acteId != null, "acteId généré");
            System.out.println("✅ Acte créé: id=" + acteId);

            // 4) Consultation
            Consultation c = Consultation.builder()
                    .dossierId(dossierId)
                    .date(LocalDate.now())
                    .status(StatutConsultation.PLANIFIE)
                    .observationMedecin("Obs service insert")
                    .build();

            consultationService.create(c);
            consultationId = c.getId();
            assertTrue(consultationId != null, "consultationId généré");
            System.out.println("✅ Consultation créée: id=" + consultationId);

            // 5) Intervention
            InterventionMedecin im = InterventionMedecin.builder()
                    .consultationId(consultationId)
                    .acteId(acteId)
                    .prixDePatient(300.0)
                    .numDent(11)
                    .build();

            interventionService.create(im);
            interventionId = im.getId();
            assertTrue(interventionId != null, "interventionId généré");
            System.out.println("✅ Intervention créée: id=" + interventionId);

            // 6) Medicament
            Medicament m = Medicament.builder()
                    .nom("MEDICAMENT SERVICE TEST")
                    .laboratoire("LAB_TEST")
                    .type("TYPE_TEST")
                    .forme(FormeMedicament.COMPRIME)
                    .remboursable(false)
                    .prixUnitaire(10.0)
                    .description("Médicament créé via service")
                    .build();

            medicamentService.create(m);
            medicamentId = m.getId();
            assertTrue(medicamentId != null, "medicamentId généré");
            System.out.println("✅ Medicament créé: id=" + medicamentId);

            // 7) Ordonnance + Prescriptions (méthode métier)
            Ordonnance o = Ordonnance.builder()
                    .dossierId(dossierId)
                    .consultationId(consultationId)
                    .date(LocalDate.now())
                    .build();

            Prescription pr = Prescription.builder()
                    .medicamentId(medicamentId)
                    .quantite(2)
                    .frequence("2 fois/jour")
                    .dureeEnJours(5)
                    .build();

            ordonnanceService.createWithPrescriptions(o, List.of(pr));

            ordonnanceId = o.getId();
            assertTrue(ordonnanceId != null, "ordonnanceId généré");
            System.out.println("✅ Ordonnance créée: id=" + ordonnanceId);

            // Vérification prescriptions via TES interfaces:
            // - OrdonnanceService : findPrescriptions(Long)
            List<Prescription> presViaOrd = ordonnanceService.findPrescriptions(ordonnanceId);
            assertTrue(presViaOrd != null && presViaOrd.size() == 1, "1 prescription via ordonnanceService.findPrescriptions");
            assertTrue(presViaOrd.get(0).getId() != null, "prescriptionId généré");
            System.out.println("✅ Prescription créée: id=" + presViaOrd.get(0).getId());

            // (optionnel) vérif via PrescriptionService : findByOrdonnanceId(Long)
            List<Prescription> presViaService = prescriptionService.findByOrdonnanceId(ordonnanceId);
            assertTrue(presViaService != null && presViaService.size() == 1, "1 prescription via prescriptionService.findByOrdonnanceId");

            // 8) Certificat
            Certificat cert = Certificat.builder()
                    .dossierId(dossierId)
                    .dateDebut(LocalDate.now())
                    .dateFin(LocalDate.now().plusDays(2))
                    .duree(2)
                    .noteMedecin("Certificat service test")
                    .build();

            certificatService.create(cert);
            certificatId = cert.getId();
            assertTrue(certificatId != null, "certificatId généré");
            System.out.println("✅ Certificat créé: id=" + certificatId);

            System.out.println("✅ INSERT OK (service -> repo -> DB)");

        } catch (Exception e) {
            System.err.println("❌ INSERT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 2) UPDATE
    // ==========================================================
    void updateProcess() {
        System.out.println("\n=== SERVICE TEST : UPDATE PROCESS ===");

        try {
            if (!servicesOk()) {
                System.out.println("❌ Services manquants. Stop.");
                return;
            }
            if (patientId == null || dossierId == null) {
                System.out.println("⚠️ insertProcess n'a pas généré les IDs. Stop update.");
                return;
            }

            // Patient update
            Patient p = patientService.getById(patientId);
            assertTrue(p != null, "patient existe");
            p.setTelephone("0611111111");
            patientService.update(p);

            Patient p2 = patientService.getById(patientId);
            assertTrue(p2 != null && "0611111111".equals(p2.getTelephone()), "patient tel updated");
            System.out.println("✅ Patient update OK");

            // Dossier update
            DossierMedical dm = dossierMedicalService.getById(dossierId);
            assertTrue(dm != null, "dossier existe");
            dm.setNotes("Notes updated via service");
            dossierMedicalService.update(dm);

            DossierMedical dm2 = dossierMedicalService.getById(dossierId);
            assertTrue(dm2 != null && "Notes updated via service".equals(dm2.getNotes()), "notes updated");
            System.out.println("✅ DossierMedical update OK");

            // Consultation update
            if (consultationId != null) {
                Consultation c = consultationService.getById(consultationId);
                assertTrue(c != null, "consultation existe");
                c.setObservationMedecin("Obs updated via service");
                c.setStatus(StatutConsultation.TERMINE);
                consultationService.update(c);

                Consultation c2 = consultationService.getById(consultationId);
                assertTrue(c2 != null && c2.getStatus() == StatutConsultation.TERMINE, "consultation status updated");
                System.out.println("✅ Consultation update OK");
            }

            // Replace prescriptions
            if (ordonnanceId != null) {
                Prescription prNew = Prescription.builder()
                        .medicamentId(medicamentId)
                        .quantite(1)
                        .frequence("1 fois/jour")
                        .dureeEnJours(3)
                        .build();

                ordonnanceService.replacePrescriptions(ordonnanceId, List.of(prNew));

                List<Prescription> after = ordonnanceService.findPrescriptions(ordonnanceId);
                assertTrue(after != null && after.size() == 1, "replace => 1 ligne");
                assertTrue(after.get(0).getQuantite() == 1, "quantite updated");
                System.out.println("✅ replacePrescriptions OK");
            }

            System.out.println("✅ UPDATE OK");

        } catch (Exception e) {
            System.err.println("❌ UPDATE FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 3) SELECT
    // ==========================================================
    void selectProcess() {
        System.out.println("\n=== SERVICE TEST : SELECT PROCESS ===");

        try {
            if (!servicesOk()) {
                System.out.println("❌ Services manquants. Stop.");
                return;
            }

            System.out.println("Patients total       = " + patientService.getAll().size());
            System.out.println("Dossiers total       = " + dossierMedicalService.count());
            System.out.println("Consultations total  = " + consultationService.count());
            System.out.println("Interventions total  = " + interventionService.count());
            System.out.println("Actes total          = " + acteService.count());
            System.out.println("Medicaments total    = " + medicamentService.count());
            System.out.println("Ordonnances total    = " + ordonnanceService.count());
            System.out.println("Certificats total    = " + certificatService.count());

            if (dossierId != null) {
                System.out.println("Ordonnances (dossierId=" + dossierId + ") = "
                        + ordonnanceService.findByDossierId(dossierId).size());

                System.out.println("Consultations (dossierId=" + dossierId + ") = "
                        + consultationService.getByDossierId(dossierId).size());
            }

            if (consultationId != null) {
                System.out.println("Interventions (consultationId=" + consultationId + ") = "
                        + interventionService.getByConsultationId(consultationId).size());
            }

            System.out.println("✅ SELECT OK");

        } catch (Exception e) {
            System.err.println("❌ SELECT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 4) DELETE (ordre FK)
    // ==========================================================
    void deleteProcess() {
        System.out.println("\n=== SERVICE TEST : DELETE PROCESS ===");

        try {
            if (!servicesOk()) {
                System.out.println("❌ Services manquants. Stop.");
                return;
            }

            // Ordonnance (cascade prescription)
            if (ordonnanceId != null) {
                ordonnanceService.deleteById(ordonnanceId);
                assertTrue(ordonnanceService.findById(ordonnanceId) == null, "ordonnance supprimée");
                System.out.println("✅ Ordonnance deleted (cascade prescriptions)");
            }

            // Certificat
            if (certificatId != null) {
                certificatService.deleteById(certificatId);
                assertTrue(certificatService.getById(certificatId) == null, "certificat supprimé");
                System.out.println("✅ Certificat deleted");
            }

            // Intervention
            if (interventionId != null) {
                interventionService.deleteById(interventionId);
                assertTrue(interventionService.getById(interventionId) == null, "intervention supprimée");
                System.out.println("✅ Intervention deleted");
            }

            // Consultation
            if (consultationId != null) {
                consultationService.deleteById(consultationId);
                assertTrue(consultationService.getById(consultationId) == null, "consultation supprimée");
                System.out.println("✅ Consultation deleted");
            }

            // Acte
            if (acteId != null) {
                acteService.deleteById(acteId);
                assertTrue(!acteService.existsById(acteId), "acte supprimé");
                System.out.println("✅ Acte deleted");
            }

            // Medicament
            if (medicamentId != null) {
                medicamentService.deleteById(medicamentId);
                assertTrue(!medicamentService.existsById(medicamentId), "medicament supprimé");
                System.out.println("✅ Medicament deleted");
            }

            // DossierMedical
            if (dossierId != null) {
                dossierMedicalService.deleteById(dossierId);
                assertTrue(dossierMedicalService.getById(dossierId) == null, "dossier supprimé");
                System.out.println("✅ DossierMedical deleted");
            }

            // Patient (pas de existsById dans ton PatientService => vérif via getById == null)
            if (patientId != null) {
                patientService.deleteById(patientId);
                assertTrue(patientService.getById(patientId) == null, "patient supprimé");
                System.out.println("✅ Patient deleted");
            }

            System.out.println("✅ DELETE OK");

        } catch (Exception e) {
            System.err.println("❌ DELETE FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // MAIN
    // ==========================================================
    public static void main(String[] args) {
        TestServiceDossierMedical t = new TestServiceDossierMedical();

        t.insertProcess();
        t.selectProcess();

        t.updateProcess();
        t.selectProcess();

        t.deleteProcess();
        t.selectProcess();

        System.out.println("\n✅ TestServiceDossierMedical terminé.");
    }
}
