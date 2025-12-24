package ma.dentalTech.repository.test;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.*;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestRepo {

    // ==========================
    // Repos (module dossierMedical)
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

    // =========================================================
    // INSERT PROCESS (créations liées)
    // =========================================================
    void insertProcess() {

        System.out.println("\n================ INSERT PROCESS (dossierMedical) ================\n");

        // 1) ACTE
        Acte acte = Acte.builder()
                .libelle("Composite esthétique")
                .categorie("Soins")
                .prixBase(450.0)
                .description("Composite esthétique sur incisive")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        acteRepo.create(acte);
        System.out.println("✅ Acte créé ID = " + acte.getId());

        // 2) CONSULTATION (sur dossier existant: dossier_id=1)
        // ✅ FIX: date(LocalDate) (pas LocalDateTime)
        Consultation cons = Consultation.builder()
                .dossierId(1L)
                .date(LocalDate.now())
                .status(StatutConsultation.PLANIFIE)
                .observationMedecin("Test consultation")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        consultationRepo.create(cons);
        System.out.println("✅ Consultation créée ID = " + cons.getId());

        // 3) INTERVENTION
        InterventionMedecin inter = InterventionMedecin.builder()
                .consultationId(cons.getId())
                .acteId(acte.getId())
                .prixDePatient(450.0)
                .numDent(11)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        interventionRepo.create(inter);
        System.out.println("✅ Intervention créée ID = " + inter.getId());

        // 4) ORDONNANCE
        Ordonnance ord = Ordonnance.builder()
                .dossierId(1L)
                .consultationId(cons.getId())
                .date(LocalDate.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        ordonnanceRepo.create(ord);
        System.out.println("✅ Ordonnance créée ID = " + ord.getId());

        // 5) MEDICAMENT
        // ✅ FIX: forme(FormeMedicament) (pas String)
        Medicament med = Medicament.builder()
                .nom("Ibuprofène 400mg")
                .laboratoire("TestLab")
                .type("Anti-inflammatoire")
                .forme(FormeMedicament.COMPRIME)
                .remboursable(false)
                .prixUnitaire(18.0)
                .description("Anti-inflammatoire")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        medicamentRepo.create(med);
        System.out.println("✅ Medicament créé ID = " + med.getId());

        // 6) PRESCRIPTION
        Prescription pres = Prescription.builder()
                .ordonnanceId(ord.getId())
                .medicamentId(med.getId())
                .quantite(12)
                .frequence("1 cp 2x/jour")
                .dureeEnJours(5)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        prescriptionRepo.create(pres);
        System.out.println("✅ Prescription créée ID = " + pres.getId());

        // 7) CERTIFICAT
        Certificat cert = Certificat.builder()
                .dossierId(1L)
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(2))
                .duree(3)
                .noteMedecin("Repos test après intervention")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        certificatRepo.create(cert);
        System.out.println("✅ Certificat créé ID = " + cert.getId());

        // 8) DOCUMENT MEDICAL
        DocumentMedical doc = DocumentMedical.builder()
                .dossierId(1L)
                .consultationId(cons.getId())
                .typeDocument(TypeDocument.ANALYSE)
                .titre("Analyse test")
                .nomFichier("analyse_test.pdf")
                .cheminFichier("C:/dentaltech/uploads/analyse_test.pdf")
                .tailleOctets(123456L)
                .dateDocument(LocalDateTime.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        documentRepo.create(doc);
        System.out.println("✅ DocumentMedical créé ID = " + doc.getId());

        System.out.println("\n✅ INSERT PROCESS terminé.\n");
    }

    // =========================================================
    // UPDATE PROCESS
    // =========================================================
    void updateProcess() {
        System.out.println("\n================ UPDATE PROCESS (dossierMedical) ================\n");

        DossierMedical d = dossierRepo.findById(1L);
        if (d == null) {
            System.out.println("❌ Dossier 1 introuvable (seed manquant?)");
            return;
        }
        d.setNotes((d.getNotes() == null ? "" : d.getNotes()) + " | NOTE AJOUTEE PAR TEST");
        d.setModifiePar("TEST");
        dossierRepo.update(d);
        System.out.println("✅ Dossier 1 update notes OK");

        List<Acte> actes = acteRepo.findAll();
        if (!actes.isEmpty()) {
            Acte a = actes.get(actes.size() - 1);
            a.setPrixBase((a.getPrixBase() == null ? 0.0 : a.getPrixBase()) + 50.0);
            a.setModifiePar("TEST");
            acteRepo.update(a);
            System.out.println("✅ Acte update ID=" + a.getId());
        }

        System.out.println("\n✅ UPDATE PROCESS terminé.\n");
    }

    // =========================================================
    // SELECT PROCESS
    // =========================================================
    void selectProcess() {
        System.out.println("\n================ SELECT PROCESS (dossierMedical) ================\n");

        System.out.println("---- Dossiers ----");
        dossierRepo.findAll().forEach(System.out::println);

        System.out.println("\n---- Consultations dossier=1 ----");
        consultationRepo.findByDossierId(1L).forEach(System.out::println);

        System.out.println("\n---- Interventions consultation=1 ----");
        interventionRepo.findByConsultationId(1L).forEach(System.out::println);

        System.out.println("\n---- Ordonnances dossier=1 ----");
        ordonnanceRepo.findByDossierId(1L).forEach(System.out::println);

        System.out.println("\n---- Prescriptions ordonnance=1 ----");
        prescriptionRepo.findByOrdonnanceId(1L).forEach(System.out::println);

        System.out.println("\n---- Certificats dossier=1 ----");
        certificatRepo.findByDossierId(1L).forEach(System.out::println);

        System.out.println("\n---- Documents dossier=1 ----");
        documentRepo.findByDossierId(1L).forEach(System.out::println);

        System.out.println("\n✅ SELECT PROCESS terminé.\n");
    }

    // =========================================================
    // DELETE PROCESS
    // =========================================================
    void deleteProcess() {
        System.out.println("\n================ DELETE PROCESS (dossierMedical) ================\n");

        List<DocumentMedical> docs = documentRepo.findAll();
        if (!docs.isEmpty()) {
            DocumentMedical last = docs.get(0);
            documentRepo.deleteById(last.getId());
            System.out.println("✅ Document supprimé ID=" + last.getId());
        }

        List<Certificat> certs = certificatRepo.findAll();
        if (!certs.isEmpty()) {
            Certificat last = certs.get(certs.size() - 1);
            certificatRepo.deleteById(last.getId());
            System.out.println("✅ Certificat supprimé ID=" + last.getId());
        }

        List<Prescription> pres = prescriptionRepo.findAll();
        if (!pres.isEmpty()) {
            Prescription last = pres.get(pres.size() - 1);
            prescriptionRepo.deleteById(last.getId());
            System.out.println("✅ Prescription supprimée ID=" + last.getId());
        }

        List<Medicament> meds = medicamentRepo.findAll();
        if (!meds.isEmpty()) {
            Medicament last = meds.get(meds.size() - 1);
            medicamentRepo.deleteById(last.getId());
            System.out.println("✅ Medicament supprimé ID=" + last.getId());
        }

        List<Ordonnance> ords = ordonnanceRepo.findAll();
        if (!ords.isEmpty()) {
            Ordonnance last = ords.get(ords.size() - 1);
            ordonnanceRepo.deleteById(last.getId());
            System.out.println("✅ Ordonnance supprimée ID=" + last.getId());
        }

        List<InterventionMedecin> inters = interventionRepo.findAll();
        if (!inters.isEmpty()) {
            InterventionMedecin last = inters.get(inters.size() - 1);
            interventionRepo.deleteById(last.getId());
            System.out.println("✅ Intervention supprimée ID=" + last.getId());
        }

        List<Consultation> cons = consultationRepo.findAll();
        if (!cons.isEmpty()) {
            Consultation last = cons.get(cons.size() - 1);
            consultationRepo.deleteById(last.getId());
            System.out.println("✅ Consultation supprimée ID=" + last.getId());
        }

        List<Acte> actes = acteRepo.findAll();
        if (!actes.isEmpty()) {
            Acte last = actes.get(actes.size() - 1);
            acteRepo.deleteById(last.getId());
            System.out.println("✅ Acte supprimé ID=" + last.getId());
        }

        System.out.println("\n✅ DELETE PROCESS terminé.\n");
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {
        TestRepo t = new TestRepo();

        t.selectProcess();
        t.insertProcess();
        t.selectProcess();
        t.updateProcess();
        t.selectProcess();
        t.deleteProcess();
        t.selectProcess();
    }
}
