package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.SituationFinanciere;

import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.entities.enums.FormeMedicament;

import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import ma.dentalTech.repository.modules.dossierMedical.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TestRepo {

    // ==========================================================
    // Repos (obligatoires)
    // ==========================================================
    private final PatientRepository patientRepo =
            ApplicationContext.getBean(PatientRepository.class);

    private final FactureRepository factureRepo =
            ApplicationContext.getBean(FactureRepository.class);

    private final SituationFinanciereRepository sitFinRepo =
            ApplicationContext.getBean(SituationFinanciereRepository.class);

    // ==========================================================
    // Repos (optionnels - dossier medical)
    // ==========================================================
    private final DossierMedicalRepository dossierMedicalRepo =
            ApplicationContext.getBean(DossierMedicalRepository.class);

    private final ConsultationRepository consultationRepo =
            ApplicationContext.getBean(ConsultationRepository.class);

    private final InterventionMedecinRepository interventionRepo =
            ApplicationContext.getBean(InterventionMedecinRepository.class);

    private final ActeRepository acteRepo =
            ApplicationContext.getBean(ActeRepository.class);

    private final MedicamentRepository medicamentRepo =
            ApplicationContext.getBean(MedicamentRepository.class);

    private final OrdonnanceRepository ordonnanceRepo =
            ApplicationContext.getBean(OrdonnanceRepository.class);

    private final PrescriptionRepository prescriptionRepo =
            ApplicationContext.getBean(PrescriptionRepository.class);

    private final CertificatRepository certificatRepo =
            ApplicationContext.getBean(CertificatRepository.class);

    // ==========================================================
    // IDs gardés entre insert/update/delete
    // ==========================================================
    private Long patientId;
    private Long dossierId;
    private Long consultationId;
    private Long interventionId;
    private Long acteId;

    private Long factureId;
    private Long sitFinId;

    private Long ordonnanceId;
    private Long prescriptionId;
    private Long medicamentId;

    private Long certificatId;

    // ==========================================================
    // Helpers
    // ==========================================================
    private boolean dossierMedicalBeansOk() {
        return dossierMedicalRepo != null
                && consultationRepo != null
                && interventionRepo != null
                && acteRepo != null
                && medicamentRepo != null
                && ordonnanceRepo != null
                && prescriptionRepo != null
                && certificatRepo != null;
    }

    // ==========================================================
    // 1) INSERT PROCESS
    // ==========================================================
    void insertProcess() {
        System.out.println("\n=== INSERT PROCESS ===");

        try {
            if (patientRepo == null) {
                System.out.println("❌ patientRepo absent (ApplicationContext). Stop insert.");
                return;
            }
            if (!dossierMedicalBeansOk()) {
                System.out.println("⚠️ Un ou plusieurs repos dossierMedical sont absents. Stop insert.");
                return;
            }
            if (factureRepo == null || sitFinRepo == null) {
                System.out.println("⚠️ factureRepo / sitFinRepo absent. On continue sans facture/SF.");
            }

            // -------------------------
            // 1) Patient
            // -------------------------
            Patient p = Patient.builder()
                    .nom("TEST_NOM")
                    .prenom("TEST_PRENOM")
                    .telephone("0600000000")
                    .adresse("TEST_ADRESSE")
                    .dateNaissance(LocalDate.of(2000, 1, 1))
                    .build();

            patientRepo.create(p);
            patientId = p.getId();
            System.out.println("✅ Patient créé: id=" + patientId);

            // -------------------------
            // 2) Dossier médical
            // -------------------------
            DossierMedical dm = DossierMedical.builder()
                    .patientId(patientId)
                    .medecinId(null) // nullable
                    .notes("Dossier test repo")
                    .dateCreation(LocalDateTime.now())
                    .build();

            dossierMedicalRepo.create(dm);
            dossierId = dm.getId();
            System.out.println("✅ DossierMedical créé: id=" + dossierId);

            // -------------------------
            // 3) Acte
            // -------------------------
            Acte a = Acte.builder()
                    .libelle("Acte Test Repo")
                    .categorie("TEST")
                    .prixBase(250.0)
                    .description("Description test")
                    .build();

            acteRepo.create(a);
            acteId = a.getId();
            System.out.println("✅ Acte créé: id=" + acteId);

            // -------------------------
            // 4) Consultation
            // -------------------------
            Consultation c = Consultation.builder()
                    .dossierId(dossierId)
                    .date(LocalDate.now()) // ton entity consultation utilise LocalDate
                    .status(StatutConsultation.PLANIFIE)
                    .observationMedecin("Observation initiale")
                    .build();

            consultationRepo.create(c);
            consultationId = c.getId();
            System.out.println("✅ Consultation créée: id=" + consultationId);

            // -------------------------
            // 5) InterventionMedecin
            // -------------------------
            InterventionMedecin im = InterventionMedecin.builder()
                    .consultationId(consultationId)
                    .acteId(acteId)
                    .prixDePatient(300.0)
                    .numDent(11)
                    .build();

            interventionRepo.create(im);
            interventionId = im.getId();
            System.out.println("✅ InterventionMedecin créée: id=" + interventionId);

            // -------------------------
            // 6) Facture + SituationFinanciere (si dispo)
            // -------------------------
            if (factureRepo != null) {
                Facture f = Facture.builder()
                        .consultationId(consultationId)
                        .dateFacture(LocalDate.now())
                        .totalFacture(300.0)
                        .totalPaye(0.0)
                        .statut(StatutFacture.NON_PAYEE)
                        .build();

                factureRepo.create(f);
                factureId = f.getId();
                System.out.println("✅ Facture créée: id=" + factureId);
            }

            if (sitFinRepo != null) {
                SituationFinanciere sf = SituationFinanciere.builder()
                        .dossierId(dossierId)
                        .medecinId(null)
                        .totalDesActes(300.0)
                        .totalPaye(0.0)
                        .credit(300.0)
                        .build();

                sitFinRepo.create(sf);
                sitFinId = sf.getId();
                System.out.println("✅ SituationFinanciere créée: id=" + sitFinId);
            }

            // -------------------------
            // 7) Medicament
            // -------------------------
            Medicament m = Medicament.builder()
                    .nom("Medicament Test Repo")
                    .laboratoire("LAB_TEST")
                    .type("TYPE_TEST")
                    .forme(FormeMedicament.COMPRIME)
                    .remboursable(false)
                    .prixUnitaire(12.5)
                    .description("Desc medicament")
                    .build();

            medicamentRepo.create(m);
            medicamentId = m.getId();
            System.out.println("✅ Medicament créé: id=" + medicamentId);

            // -------------------------
            // 8) Ordonnance
            // -------------------------
            Ordonnance o = Ordonnance.builder()
                    .dossierId(dossierId)
                    .consultationId(consultationId)
                    .date(LocalDate.now())
                    .build();

            ordonnanceRepo.create(o);
            ordonnanceId = o.getId();
            System.out.println("✅ Ordonnance créée: id=" + ordonnanceId);

            // -------------------------
            // 9) Prescription
            // -------------------------
            Prescription pr = Prescription.builder()
                    .ordonnanceId(ordonnanceId)
                    .medicamentId(medicamentId)
                    .quantite(2)
                    .frequence("2 fois/jour")
                    .dureeEnJours(5)
                    .build();

            prescriptionRepo.create(pr);
            prescriptionId = pr.getId();
            System.out.println("✅ Prescription créée: id=" + prescriptionId);

            // -------------------------
            // 10) Certificat
            // -------------------------
            Certificat cert = Certificat.builder()
                    .dossierId(dossierId)
                    .dateDebut(LocalDate.now())
                    .dateFin(LocalDate.now().plusDays(2))
                    .duree(2)
                    .noteMedecin("Repos médical")
                    .build();

            certificatRepo.create(cert);
            certificatId = cert.getId();
            System.out.println("✅ Certificat créé: id=" + certificatId);

            System.out.println("✅ INSERT PROCESS OK");

        } catch (Exception e) {
            System.err.println("❌ INSERT PROCESS FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 2) UPDATE PROCESS
    // ==========================================================
    void updateProcess() {
        System.out.println("\n=== UPDATE PROCESS ===");

        try {
            if (!dossierMedicalBeansOk() || patientRepo == null) {
                System.out.println("⚠️ Beans manquants. Stop update.");
                return;
            }

            // Patient
            Patient p = patientRepo.findById(patientId);
            if (p != null) {
                p.setTelephone("0611111111");
                p.setAdresse("ADRESSE UPDATE");
                patientRepo.update(p);
                System.out.println("✅ Patient updated");
            }

            // Dossier
            DossierMedical dm = dossierMedicalRepo.findById(dossierId);
            if (dm != null) {
                dm.setNotes("Notes UPDATE test repo");
                dossierMedicalRepo.update(dm);
                System.out.println("✅ DossierMedical updated");
            }

            // Acte
            Acte a = acteRepo.findById(acteId);
            if (a != null) {
                a.setPrixBase(400.0);
                a.setDescription("Desc UPDATE");
                acteRepo.update(a);
                System.out.println("✅ Acte updated");
            }

            // Consultation
            Consultation c = consultationRepo.findById(consultationId);
            if (c != null) {
                c.setObservationMedecin("Observation UPDATE");
                c.setStatus(StatutConsultation.TERMINE);
                consultationRepo.update(c);
                System.out.println("✅ Consultation updated");
            }

            // Intervention
            InterventionMedecin im = interventionRepo.findById(interventionId);
            if (im != null) {
                im.setPrixDePatient(450.0);
                interventionRepo.update(im);
                System.out.println("✅ Intervention updated");
            }

            // Facture
            if (factureRepo != null && factureId != null) {
                Facture f = factureRepo.findById(factureId);
                if (f != null) {
                    f.setTotalPaye(200.0);
                    f.setStatut(StatutFacture.PARTIEL);
                    factureRepo.update(f);
                    System.out.println("✅ Facture updated");
                }
            }

            // Situation Financière
            if (sitFinRepo != null && sitFinId != null) {
                SituationFinanciere sf = sitFinRepo.findById(sitFinId);
                if (sf != null) {
                    sf.setTotalPaye(200.0);
                    sf.setCredit(Math.max(0.0, sf.getTotalDesActes() - sf.getTotalPaye()));
                    sitFinRepo.update(sf);
                    System.out.println("✅ SituationFinanciere updated");
                }
            }

            // Medicament
            Medicament m = medicamentRepo.findById(medicamentId);
            if (m != null) {
                m.setPrixUnitaire(20.0);
                m.setRemboursable(true);
                medicamentRepo.update(m);
                System.out.println("✅ Medicament updated");
            }

            // Ordonnance
            Ordonnance o = ordonnanceRepo.findById(ordonnanceId);
            if (o != null) {
                o.setDate(LocalDate.now().minusDays(1));
                ordonnanceRepo.update(o);
                System.out.println("✅ Ordonnance updated");
            }

            // Prescription
            Prescription pr = prescriptionRepo.findById(prescriptionId);
            if (pr != null) {
                pr.setQuantite(3);
                pr.setFrequence("3 fois/jour");
                prescriptionRepo.update(pr);
                System.out.println("✅ Prescription updated");
            }

            // Certificat
            Certificat cert = certificatRepo.findById(certificatId);
            if (cert != null) {
                cert.setNoteMedecin("Note UPDATE");
                certificatRepo.update(cert);
                System.out.println("✅ Certificat updated");
            }

            System.out.println("✅ UPDATE PROCESS OK");

        } catch (Exception e) {
            System.err.println("❌ UPDATE PROCESS FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 3) DELETE PROCESS (ordre FK)
    // ==========================================================
    void deleteProcess() {
        System.out.println("\n=== DELETE PROCESS ===");

        try {
            if (!dossierMedicalBeansOk() || patientRepo == null) {
                System.out.println("⚠️ Beans manquants. Stop delete.");
                return;
            }

            // supprimer d’abord les enfants
            if (prescriptionId != null) {
                prescriptionRepo.deleteById(prescriptionId);
                System.out.println("✅ Prescription deleted");
            }

            if (ordonnanceId != null) {
                // sécurité: si jamais il reste des prescriptions
                try { prescriptionRepo.deleteByOrdonnanceId(ordonnanceId); } catch (Exception ignore) {}
                ordonnanceRepo.deleteById(ordonnanceId);
                System.out.println("✅ Ordonnance deleted");
            }

            if (certificatId != null) {
                certificatRepo.deleteById(certificatId);
                System.out.println("✅ Certificat deleted");
            }

            if (factureRepo != null && factureId != null) {
                factureRepo.deleteById(factureId);
                System.out.println("✅ Facture deleted");
            }

            if (sitFinRepo != null && sitFinId != null) {
                sitFinRepo.deleteById(sitFinId);
                System.out.println("✅ SituationFinanciere deleted");
            }

            if (interventionId != null) {
                interventionRepo.deleteById(interventionId);
                System.out.println("✅ Intervention deleted");
            }

            if (consultationId != null) {
                // sécurité: supprimer toutes les interventions de la consultation
                try { interventionRepo.deleteByConsultationId(consultationId); } catch (Exception ignore) {}
                consultationRepo.deleteById(consultationId);
                System.out.println("✅ Consultation deleted");
            }

            if (acteId != null) {
                acteRepo.deleteById(acteId);
                System.out.println("✅ Acte deleted");
            }

            if (medicamentId != null) {
                medicamentRepo.deleteById(medicamentId);
                System.out.println("✅ Medicament deleted");
            }

            if (dossierId != null) {
                dossierMedicalRepo.deleteById(dossierId);
                System.out.println("✅ DossierMedical deleted");
            }

            if (patientId != null) {
                patientRepo.deleteById(patientId);
                System.out.println("✅ Patient deleted");
            }

            System.out.println("✅ DELETE PROCESS OK");

        } catch (Exception e) {
            System.err.println("❌ DELETE PROCESS FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // 4) SELECT PROCESS (affichages)
    // ==========================================================
    void selectProcess() {
        System.out.println("\n=== SELECT PROCESS ===");

        try {
            if (patientRepo != null) {
                System.out.println("Patients total = " + patientRepo.findAll().size());
            }

            if (dossierMedicalRepo != null) {
                System.out.println("Dossiers total = " + dossierMedicalRepo.count());
                List<DossierMedical> list = dossierMedicalRepo.findPage(5, 0);
                System.out.println("Dossiers page(5,0) = " + list.size());
                if (!list.isEmpty()) {
                    DossierMedical dm = list.get(0);
                    System.out.println("Ex dossier id=" + dm.getId() + " patientId=" + dm.getPatientId());
                    Optional<DossierMedical> opt = dossierMedicalRepo.findByPatientId(dm.getPatientId());
                    System.out.println("findByPatientId -> present ? " + opt.isPresent());
                }
            }

            if (consultationRepo != null) {
                System.out.println("Consultations total = " + consultationRepo.count());
                System.out.println("Consultations page(5,0) = " + consultationRepo.findPage(5, 0).size());
            }

            if (interventionRepo != null) {
                System.out.println("Interventions total = " + interventionRepo.count());
                System.out.println("Interventions page(5,0) = " + interventionRepo.findPage(5, 0).size());
            }

            if (acteRepo != null) {
                System.out.println("Actes total = " + acteRepo.count());
            }

            if (medicamentRepo != null) {
                System.out.println("Medicaments total = " + medicamentRepo.count());
            }

            if (ordonnanceRepo != null) {
                System.out.println("Ordonnances total = " + ordonnanceRepo.count());
            }

            if (prescriptionRepo != null) {
                System.out.println("Prescriptions total = " + prescriptionRepo.findAll().size());
            }

            if (certificatRepo != null) {
                System.out.println("Certificats total = " + certificatRepo.count());
            }

            // Caisse: exemple de stats (30j) si dispo dans tes repos
            if (factureRepo != null) {
                LocalDateTime start = LocalDateTime.now().minusDays(30);
                LocalDateTime end = LocalDateTime.now();
                try {
                    System.out.println("Total factures (30j) = " + factureRepo.calculateTotalFactures(start, end));
                } catch (Exception ignore) {
                    System.out.println("calculateTotalFactures() non dispo -> skip");
                }
            }

            System.out.println("✅ SELECT PROCESS OK");

        } catch (Exception e) {
            System.err.println("❌ SELECT PROCESS FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================================
    // MAIN (ordre classique)
    // ==========================================================
    public static void main(String[] args) {
        TestRepo t = new TestRepo();

        // ordre conseillé : insert -> select -> update -> select -> delete -> select
        t.insertProcess();
        t.selectProcess();

        t.updateProcess();
        t.selectProcess();

        t.deleteProcess();
        t.selectProcess();

        System.out.println("\n✅ TestRepo terminé.");
    }
}
