package ma.dentalTech.service.modules.dossierMedical.test;

import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.entities.enums.TypeDocument;

import ma.dentalTech.service.modules.dossierMedical.api.*;
import ma.dentalTech.service.modules.dossierMedical.dto.common.*;

import ma.dentalTech.service.modules.dossierMedical.dto.certificat.*;
import ma.dentalTech.service.modules.dossierMedical.dto.document.*;
import ma.dentalTech.service.modules.dossierMedical.dto.intervention.*;
import ma.dentalTech.service.modules.dossierMedical.dto.medicament.*;
import ma.dentalTech.service.modules.dossierMedical.dto.ordonnance.*;
import ma.dentalTech.service.modules.dossierMedical.dto.prescription.*;

import ma.dentalTech.service.modules.dossierMedical.impl.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDossierMedicalServices {

    public static void main(String[] args) {

        // Acteur "audit" (cree_par / modifie_par)
        ActorDTO actor = new ActorDTO("drjihane");

        // Services (instanciation directe sans ApplicationContext)
        MedicamentService medicamentService = new MedicamentServiceImpl();
        OrdonnanceService ordonnanceService = new OrdonnanceServiceImpl();
        PrescriptionService prescriptionService = new PrescriptionServiceImpl();
        DocumentMedicalService documentService = new DocumentMedicalServiceImpl();
        CertificatService certificatService = new CertificatServiceImpl();
        InterventionMedecinService interventionService = new InterventionMedecinServiceImpl();

        // IDs créés pour cleanup
        Long medicamentTestId = null;
        Long ordonnanceTestId = null;
        Long prescriptionTestId = null;
        Long documentTestId = null;
        Long certificatTestId = null;
        Long interventionTestId = null;

        // Pour éviter les conflits de noms en DB
        String unique = String.valueOf(System.currentTimeMillis());

        try {
            System.out.println("====================================");
            System.out.println("TEST SERVICES - DOSSIER MEDICAL (MySQL)");
            System.out.println("DB: dentalsoft_db (seed requis)");
            System.out.println("====================================");

            // ==========================================================
            // 1) MEDICAMENT: create / get / update
            // ==========================================================
            MedicamentDTO medIn = new MedicamentDTO(
                    null,
                    "TestMed-" + unique,
                    "LabTest",
                    "Antalgique",
                    FormeMedicament.COMPRIME,
                    false,
                    12.50,
                    "Medicament de test"
            );

            medicamentTestId = medicamentService.create(new SaveMedicamentRequestDTO(medIn, actor)).id();
            System.out.println("[OK] Medicament created id=" + medicamentTestId);

            MedicamentDTO medRead = medicamentService.getById(new IdRequestDTO(medicamentTestId));
            System.out.println("[OK] Medicament read: " + medRead);

            MedicamentDTO medUpd = new MedicamentDTO(
                    medicamentTestId,
                    medRead.nom() + " (upd)",
                    medRead.laboratoire(),
                    medRead.type(),
                    medRead.forme(),
                    medRead.remboursable(),
                    15.00,
                    medRead.description()
            );

            medicamentService.update(new SaveMedicamentRequestDTO(medUpd, actor));
            System.out.println("[OK] Medicament updated");


            // ==========================================================
            // 2) ORDONNANCE + PRESCRIPTION (sur données seed)
            // dossier_id=1 et consultation_id=1 existent dans ton seed
            // ==========================================================
            OrdonnanceDTO ordIn = new OrdonnanceDTO(
                    null,
                    1L,
                    1L,
                    LocalDate.now()
            );

            ordonnanceTestId = ordonnanceService.create(new SaveOrdonnanceRequestDTO(ordIn, actor)).id();
            System.out.println("[OK] Ordonnance created id=" + ordonnanceTestId);

            // medicament_id=1 existe dans ton seed
            PrescriptionDTO presIn = new PrescriptionDTO(
                    null,
                    ordonnanceTestId,
                    1L,
                    6,
                    "1 cp/jour",
                    3
            );

            prescriptionTestId = prescriptionService.create(new SavePrescriptionRequestDTO(presIn, actor)).id();
            System.out.println("[OK] Prescription created id=" + prescriptionTestId);

            var presList = prescriptionService.findByOrdonnanceId(new OrdonnanceIdRequestDTO(ordonnanceTestId));
            System.out.println("[OK] Prescriptions count for ordonnance=" + ordonnanceTestId +
                    " => " + presList.items().size());

            PrescriptionDTO presUpd = new PrescriptionDTO(
                    prescriptionTestId,
                    ordonnanceTestId,
                    1L,
                    10,
                    "1 cp si douleur",
                    5
            );

            prescriptionService.update(new SavePrescriptionRequestDTO(presUpd, actor));
            System.out.println("[OK] Prescription updated");

            System.out.println("[OK] Prescription count (service) => " +
                    prescriptionService.countByOrdonnanceId(new OrdonnanceIdRequestDTO(ordonnanceTestId)).total()
            );


            // ==========================================================
            // 3) DOCUMENT MEDICAL: create / update (sur dossier=1)
            // ==========================================================
            DocumentMedicalDTO docIn = new DocumentMedicalDTO(
                    null,
                    1L,
                    1L,
                    TypeDocument.ANALYSE,
                    "Doc test " + unique,
                    "doc_test_" + unique + ".pdf",
                    "C:/dentaltech/uploads/doc_test_" + unique + ".pdf",
                    12345L,
                    LocalDateTime.now()
            );

            documentTestId = documentService.create(new SaveDocumentMedicalRequestDTO(docIn, actor)).id();
            System.out.println("[OK] DocumentMedical created id=" + documentTestId);

            DocumentMedicalDTO docUpd = new DocumentMedicalDTO(
                    documentTestId,
                    1L,
                    1L,
                    TypeDocument.ANALYSE,
                    "Doc test " + unique + " (upd)",
                    "doc_test_" + unique + ".pdf",
                    "C:/dentaltech/uploads/doc_test_" + unique + ".pdf",
                    12345L,
                    LocalDateTime.now()
            );

            documentService.update(new SaveDocumentMedicalRequestDTO(docUpd, actor));
            System.out.println("[OK] DocumentMedical updated");


            // ==========================================================
            // 4) CERTIFICAT: create / update (sur dossier=1)
            // ==========================================================
            CertificatDTO certIn = new CertificatDTO(
                    null,
                    1L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(2),
                    3,
                    "Certificat test " + unique
            );

            certificatTestId = certificatService.create(new SaveCertificatRequestDTO(certIn, actor)).id();
            System.out.println("[OK] Certificat created id=" + certificatTestId);

            CertificatDTO certUpd = new CertificatDTO(
                    certificatTestId,
                    1L,
                    certIn.dateDebut(),
                    certIn.dateFin(),
                    certIn.duree(),
                    "Certificat test " + unique + " (upd)"
            );

            certificatService.update(new SaveCertificatRequestDTO(certUpd, actor));
            System.out.println("[OK] Certificat updated");


            // ==========================================================
            // 5) INTERVENTION MEDECIN: create / update
            // consultation=1, acte=1 existent dans ton seed
            // ==========================================================
            InterventionMedecinDTO intIn = new InterventionMedecinDTO(
                    null,
                    1L,
                    1L,
                    50.0,
                    12
            );

            interventionTestId = interventionService.create(new SaveInterventionRequestDTO(intIn, actor)).id();
            System.out.println("[OK] Intervention created id=" + interventionTestId);

            InterventionMedecinDTO intUpd = new InterventionMedecinDTO(
                    interventionTestId,
                    1L,
                    1L,
                    75.0,
                    12
            );

            interventionService.update(new SaveInterventionRequestDTO(intUpd, actor));
            System.out.println("[OK] Intervention updated");


            System.out.println("✅✅✅ Tous les scénarios SERVICES DM OK ✅✅✅");

        } catch (Exception e) {
            System.out.println("❌ Erreur pendant les tests:");
            e.printStackTrace();

        } finally {
            // Cleanup (sans lambda => pas d'erreur effectively final)
            try {
                if (interventionTestId != null) {
                    interventionService.delete(new IdRequestDTO(interventionTestId));
                }
            } catch (Exception ignored) {}

            try {
                if (certificatTestId != null) {
                    certificatService.delete(new IdRequestDTO(certificatTestId));
                }
            } catch (Exception ignored) {}

            try {
                if (documentTestId != null) {
                    documentService.delete(new IdRequestDTO(documentTestId));
                }
            } catch (Exception ignored) {}

            // Optionnel: supprimer prescription avant (sinon l'ordonnance supprime en cascade)
            try {
                if (prescriptionTestId != null) {
                    prescriptionService.delete(new IdRequestDTO(prescriptionTestId));
                }
            } catch (Exception ignored) {}

            try {
                if (ordonnanceTestId != null) {
                    ordonnanceService.delete(new IdRequestDTO(ordonnanceTestId));
                }
            } catch (Exception ignored) {}

            try {
                if (medicamentTestId != null) {
                    medicamentService.delete(new IdRequestDTO(medicamentTestId));
                }
            } catch (Exception ignored) {}

            System.out.println("🧹 Cleanup terminé.");
        }
    }
}
