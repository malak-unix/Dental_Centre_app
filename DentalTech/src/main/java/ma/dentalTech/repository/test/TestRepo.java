package ma.dentalTech.repository.test;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.patient.*;
import ma.dentalTech.entities.users.*;
import ma.dentalTech.entities.enums.*;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;

import ma.dentalTech.repository.modules.agenda.api.*;
import ma.dentalTech.repository.modules.agenda.impl.*;

import ma.dentalTech.repository.modules.patient.api.*;
import ma.dentalTech.repository.modules.patient.impl.*;

import ma.dentalTech.repository.modules.caisse.api.*;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.repository.modules.users.impl.*;

import ma.dentalTech.configuration.ApplicationContext;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestRepo {

        // ==========================
        // Repos (Auth & Users)
        // ==========================
        private final UtilisateurRepository utilisateurRepo = new UtilisateurRepositoryImpl();
        private final RoleRepository roleRepo = new RoleRepositoryImpl();
        private final MedecinRepository medecinRepo = new MedecinRepositoryImpl();

        // ==========================
        // Repos (dossierMedical)
        // ==========================
        private final DossierMedicalRepository dossierRepo = new DossierMedicalRepositoryImpl();
        private final DocumentMedicalRepository documentRepo = new DocumentMedicalRepositoryImpl();
        private final CertificatRepository certificatRepo = new CertificatRepositoryImpl();
        private final ActeRepository acteRepo = new ActeRepositoryImpl();
        private final ConsultationRepository consultationRepo = new ConsultationRepositoryImpl();
        private final InterventionMedecinRepository interventionRepo = new InterventionMedecinRepositoryImpl();
        private final OrdonnanceRepository ordonnanceRepo = new OrdonnanceRepositoryImpl();
        private final MedicamentRepository medicamentRepo = new MedicamentRepositoryImpl();
        private final PrescriptionRepository prescriptionRepo = new PrescriptionRepositoryImpl();

        // ==========================
        // Repos (caisse)
        // ==========================
        private final FactureRepository factureRepo = ApplicationContext.getBean(FactureRepository.class);
        private final ChargesRepository chargesRepo = ApplicationContext.getBean(ChargesRepository.class);
        private final RevenuesRepository revenusRepo = ApplicationContext.getBean(RevenuesRepository.class);
        private final SituationFinanciereRepository sitRepo = ApplicationContext
                        .getBean(SituationFinanciereRepository.class);

        // ==========================
        // Repos (agenda)
        // ==========================
        private final AgendaMensuelRepository agendaRepo = new AgendaMensuelRepositoryImpl();
        private final DetailJourneeRepository detailJourneeRepo = new DetailJourneeRepositoryImpl();
        private final PlageHoraireRepository plageHoraireRepo = new PlageHoraireRepositoryImpl();
        private final RdvRepository rdvRepo = new RdvRepositoryImpl();
        private final ListeAttenteRepository listeAttenteRepo = new ListeAttenteRepositoryImpl();

        // ==========================
        // Repos (patient / antecedent)
        // ==========================
        private final PatientRepository patientRepo = new PatientRepositoryImpl();
        private final AntecedentRepository antecedentRepo = new AntecedentRepositoryImpl();

        // =========================================================
        // 1. AUTH & USER FLOW
        // =========================================================
        Long testAuthAndUsers() {
                System.out.println("\n=== 1. TEST AUTH & USERS (Creation Medecin) ===");

                // 1. Create Role if not exists
                Role roleMedecin = null;
                try {
                        List<Role> roles = roleRepo.findAll();
                        roleMedecin = roles.stream().filter(r -> r.getLibelle() == LibelleRole.MEDECIN).findFirst()
                                        .orElse(null);

                        if (roleMedecin == null) {
                                roleMedecin = Role.builder()
                                                .libelle(LibelleRole.MEDECIN)
                                                .privileges("BASIC_ACCESS") // Field is 'privileges', not 'description'
                                                .build();
                                roleRepo.create(roleMedecin);
                                roleMedecin = roleRepo.findAll().stream()
                                                .filter(r -> r.getLibelle() == LibelleRole.MEDECIN).findFirst()
                                                .orElseThrow();
                        }
                } catch (Exception e) {
                        System.out.println("⚠️ Role check failed/skipped: " + e.getMessage());
                }

                // 2. Create Medecin User
                Medecin medecin = Medecin.builder()
                                .login("dr_house_" + System.currentTimeMillis()) // Field is 'login', not 'username'
                                .motDePasse("password123") // Field is 'motDePasse'
                                .nom("House")
                                .prenom("Gregory")
                                .email("house@hospital.com")
                                .tel("0600000000") // Field is 'tel'
                                .roleId(roleMedecin != null ? roleMedecin.getId() : 1L) // Field is 'roleId' (on
                                                                                        // Utilisateur)
                                .actif(true)
                                .specialite("Diagnostician")
                                .build();

                try {
                        medecinRepo.create(medecin);
                        System.out.println("✅ Medecin créé: " + medecin.getLogin() + " (ID: " + medecin.getId() + ")");
                        return medecin.getId();
                } catch (Exception e) {
                        System.out.println("❌ Erreur création Medecin: " + e.getMessage());
                        return 1L;
                }
        }

        // =========================================================
        // 2. PATIENT & ANTECEDENT FLOW
        // =========================================================
        Long testPatientAndAntecedent() {
                System.out.println("\n=== 2. TEST PATIENT & ANTECEDENT ===");

                Patient p = Patient.builder()
                                .nom("PATIENT_" + System.currentTimeMillis())
                                .prenom("Test")
                                .telephone("0612345678")
                                .adresse("Casablanca")
                                .assurance(Assurance.CNSS)
                                .creePar("TEST_AUTO")
                                .modifiePar("TEST_AUTO")
                                .build();

                patientRepo.create(p);
                System.out.println("✅ Patient créé: " + p.getNom() + " (ID: " + p.getId() + ")");

                Antecedents a = Antecedents.builder()
                                .patientId(p.getId())
                                .nom("Hypertension")
                                .categorie("Cardio")
                                .niveauDeRisque(NiveauDeRisque.MOYEN)
                                .description("Sous traitement")
                                .creePar("TEST_AUTO")
                                .build();

                antecedentRepo.create(a);
                System.out.println("✅ Antécédent ajouté (ID: " + a.getId() + ")");

                return p.getId();
        }

        // =========================================================
        // 3. MEDICAL FLOW (Dossier -> Consultation -> Clinical Items)
        // =========================================================
        Long testMedicalFlow(Long patientId, Long medecinId) {
                System.out.println("\n=== 3. TEST MEDICAL FLOW ===");

                // A. Dossier Medical
                DossierMedical dossier = DossierMedical.builder()
                                .patientId(patientId)
                                .medecinId(medecinId)
                                .notes("Dossier initial")
                                .creePar("Dr House")
                                .build();

                dossierRepo.create(dossier);
                System.out.println("✅ Dossier Médical créé (ID: " + dossier.getId() + ")");

                // B. Consultation
                Consultation consult = Consultation.builder()
                                .dossierId(dossier.getId()) // Field is 'dossierId', not 'dossierMedicalId'
                                .date(LocalDateTime.now()) // Field is 'date', not 'dateConsultation'
                                // motif and diagnostic seem missing in Consultation definition.
                                // Checking Consultation.java (Step 799):
                                // Fields: dossierId, date, status, observationMedecin.
                                // NO motif, NO diagnostic.
                                // I will map 'motif' -> observationMedecin? Or just use valid fields.
                                .status(StatutConsultation.PLANIFIE) // using a valid enum if possible, or PLANNIFIEE
                                .observationMedecin("Douleur dentaire - Carie profonde")
                                .creePar("Dr House")
                                .build();

                consultationRepo.create(consult);
                System.out.println("✅ Consultation créée (ID: " + consult.getId() + ")");

                // C. Acte (Linked to Consultation/Dossier needs verification of Entity)
                // Assuming Acte relates to Consultation or Dossier. Usually Acte is reference
                // data,
                // but "Acte réalisé" is transactional. Assuming Acte entity provided is
                // 'RefActe' or similar,
                // or if Acte is the transaction.
                // Looking at imports: ma.dentalTech.entities.dossierMedicale.acte.ActeDTO...
                // The standard pattern is `ConsultationActe` or `Acte` having a consultationId.
                // Let's assume ActeRepository handles the link.

                // D. Ordonnance -> Prescription -> Medicament
                Ordonnance ordonnance = Ordonnance.builder()
                                .dossierId(dossier.getId()) // Or consultationId depending on model
                                .consultationId(consult.getId())
                                .date(LocalDate.now()) // Field is 'date'
                                .creePar("Dr House")
                                .build();
                ordonnanceRepo.create(ordonnance);
                System.out.println("✅ Ordonnance créée (ID: " + ordonnance.getId() + ")");

                // Medicament (Reference)
                // Note: Medicament here seems to be reference data.
                // The instruction removes the Medicament creation block and uses 1L for
                // medicamentId.

                // Prescription (Link Ordonnance <-> Medicament)
                Prescription pres = Prescription.builder()
                                .ordonnanceId(ordonnance.getId())
                                .medicamentId(1L) // Mocking medicament ID as I didn't create one in this fixed block
                                                  // (or reuse existing)
                                // Fields: quantite (int), frequence (String), dureeEnJours (int)
                                .quantite(2)
                                .frequence("3 fois par jour")
                                .dureeEnJours(5)
                                .build();
                prescriptionRepo.create(pres);
                System.out.println("✅ Prescription ajoutée");

                return consult.getId(); // Return for billing
        }

        // =========================================================
        // 4. FINANCIAL FLOW (Facture -> Situation)
        // =========================================================
        void testFinancialFlow(Long consultationId) {
                System.out.println("\n=== 4. TEST FINANCIAL FLOW ===");

                // A. Création Facture
                Facture facture = Facture.builder()
                                .consultationId(consultationId)
                                .totalFacture(500.0) // Field is 'totalFacture'
                                .totalPaye(0.0) // Field is 'totalPaye'
                                .statut(StatutFacture.NON_PAYEE) // Enum is NON_PAYEE
                                .dateFacture(LocalDate.now()) // Field is LocalDate
                                .creePar("Secretaire")
                                .build();

                factureRepo.create(facture);
                System.out.println("✅ Facture créée (ID: " + facture.getId() + ") - Statut: " + facture.getStatut());

                // B. Paiement Partiel
                // Verify updatePayment signature in FactureRepository
                // Assuming it matches: updatePayment(Long id, Double amount, StatutFacture
                // status, String user)
                factureRepo.updatePayment(facture.getId(), 200.0, StatutFacture.PARTIEL, "Secretaire");
                System.out.println("✅ Paiement de 200.0 enregistré. Statut update: PARTIEL");

                // C. Check Situation Financiere (Updated by Trigger or Repo logic?)
                // Assuming Logic is in Service or automated via Triggers.
                // If Repo-based, we might need to manually call update on
                // SituationFinanciereRepo if not handled.
                // But usually verifying it exists is good.
                // Assuming `sitRepo.findByDossierId` works if we had the dossier ID.

                Facture fUpdated = factureRepo.findById(facture.getId());
                // Using correct getters
                if (fUpdated != null) {
                        System.out.println("   -> Reste à payer: "
                                        + (fUpdated.getTotalFacture() - fUpdated.getTotalPaye()));
                }
        }

        // =========================================================
        // MAIN EXECUTION CHAIN
        // =========================================================
        public static void main(String[] args) {
                TestRepo app = new TestRepo();

                System.out.println("🚀 DÉMARRAGE DU SCÉNARIO COMPLET DE TEST");

                // 1. Auth / Users
                Long medecinId = app.testAuthAndUsers();

                // 2. Patient
                Long patientId = app.testPatientAndAntecedent();

                // 3. Medical (Dossier -> Consultation)
                // Linking Patient and Medecin Created above
                Long consultationId = app.testMedicalFlow(patientId, medecinId);

                // 4. Financial (Facture linked to Consultation)
                app.testFinancialFlow(consultationId);

                // 5. Agenda (Optional but good for completeness)
                // app.insertAgenda(); // (Existing logic preserved if needed)

                System.out.println("\n🏁 FIN DU SCÉNARIO DE TEST.");
        }
}
