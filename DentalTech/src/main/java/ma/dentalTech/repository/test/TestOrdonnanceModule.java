package ma.dentalTech.repository.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.medicament.Medicament;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.entities.prescription.Prescription;
import ma.dentalTech.repository.modules.ordonnance.api.MedicamentRepository;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.repository.modules.ordonnance.api.PrescriptionRepository;

import java.time.LocalDate;
import java.util.List;

public class TestOrdonnanceModule {

    private final OrdonnanceRepository ordonnanceRepo;
    private final MedicamentRepository medicamentRepo;
    private final PrescriptionRepository prescriptionRepo;

    private Long idMedicament;
    private Long idOrdonnance;
    private Long idPrescription;

    public TestOrdonnanceModule() {
        this.ordonnanceRepo = ApplicationContext.getBean(OrdonnanceRepository.class);
        this.medicamentRepo = ApplicationContext.getBean(MedicamentRepository.class);
        this.prescriptionRepo = ApplicationContext.getBean(PrescriptionRepository.class);
    }

    // =====================================================
    // INSERT
    // =====================================================
    void insertProcess() {

        // 1. Créer un médicament
        Medicament med = Medicament.builder()
                .nom("TestMed_" + System.currentTimeMillis())
                .laboratoire("LabTest")
                .type("Antalgique")
                .remboursable(true)
                .prixUnitaire(120.0)
                .description("Med test ordonnance")
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        medicamentRepo.create(med);
        idMedicament = med.getId();
        System.out.println("[Ordonnance] Médicament créé id = " + idMedicament);

        // 2. Créer une ordonnance (dossier=1, consultation=1 venant des seeds)
        Ordonnance ordo = Ordonnance.builder()
                .dossierId(1L)
                .consultationId(1L)
                .date(LocalDate.now())
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        ordonnanceRepo.create(ordo);
        idOrdonnance = ordo.getId();
        System.out.println("[Ordonnance] Ordonnance créée id = " + idOrdonnance);

        // 3. Créer une prescription liée à l’ordonnance et au médicament
        Prescription p = Prescription.builder()
                .ordonnanceId(idOrdonnance)
                .medicamentId(idMedicament)
                .quantite(2)
                .frequence("2 fois / jour")
                .dureeEnJours(5)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();
        prescriptionRepo.create(p);
        idPrescription = p.getId();
        System.out.println("[Ordonnance] Prescription créée id = " + idPrescription);
    }

    // =====================================================
    // SELECT
    // =====================================================
    void selectProcess() {

        System.out.println("=== [Ordonnance] SELECT ===");

        if (idMedicament != null) {
            Medicament med = medicamentRepo.findById(idMedicament);
            System.out.println("Médicament trouvé : " + med);
        }

        if (idOrdonnance != null) {
            Ordonnance o = ordonnanceRepo.findById(idOrdonnance);
            System.out.println("Ordonnance trouvée : " + o);

            List<Ordonnance> listDossier = ordonnanceRepo.findByDossierId(1L);
            System.out.println("Ordonnances du dossier 1 = " + listDossier.size());
        }

        if (idPrescription != null) {
            Prescription p = prescriptionRepo.findById(idPrescription);
            System.out.println("Prescription trouvée : " + p);

            List<Prescription> listOrdo = prescriptionRepo.findByOrdonnanceId(idOrdonnance);
            System.out.println("Nb prescriptions de l’ordonnance " + idOrdonnance + " = " + listOrdo.size());
        }
    }

    // =====================================================
    // UPDATE
    // =====================================================
    void updateProcess() {

        System.out.println("=== [Ordonnance] UPDATE ===");

        if (idMedicament != null) {
            Medicament med = medicamentRepo.findById(idMedicament);
            if (med != null) {
                med.setPrixUnitaire(med.getPrixUnitaire() + 10.0);
                med.setDescription("Medicament MAJ");
                med.setModifiePar("TEST_UPDATE");
                medicamentRepo.update(med);
                System.out.println("Médicament après update : " + medicamentRepo.findById(idMedicament));
            }
        }

        if (idOrdonnance != null) {
            Ordonnance o = ordonnanceRepo.findById(idOrdonnance);
            if (o != null) {
                o.setDate(o.getDate().plusDays(1));
                o.setModifiePar("TEST_UPDATE");
                ordonnanceRepo.update(o);
                System.out.println("Ordonnance après update : " + ordonnanceRepo.findById(idOrdonnance));
            }
        }

        if (idPrescription != null) {
            Prescription p = prescriptionRepo.findById(idPrescription);
            if (p != null) {
                p.setQuantite(p.getQuantite() + 1);
                p.setDureeEnJours(p.getDureeEnJours() + 2);
                p.setModifiePar("TEST_UPDATE");
                prescriptionRepo.update(p);
                System.out.println("Prescription après update : " + prescriptionRepo.findById(idPrescription));
            }
        }
    }

    // =====================================================
    // DELETE
    // =====================================================
    void deleteProcess() {

        System.out.println("=== [Ordonnance] DELETE ===");
        // respecter les FK : prescription -> ordonnance -> medicament

        if (idPrescription != null) {
            prescriptionRepo.deleteById(idPrescription);
            System.out.println("Prescription supprimée id=" + idPrescription);
        }

        if (idOrdonnance != null) {
            ordonnanceRepo.deleteById(idOrdonnance);
            System.out.println("Ordonnance supprimée id=" + idOrdonnance);
        }

        if (idMedicament != null) {
            medicamentRepo.deleteById(idMedicament);
            System.out.println("Médicament supprimé id=" + idMedicament);
        }
    }

    public static void main(String[] args) {
        TestOrdonnanceModule t = new TestOrdonnanceModule();
        t.insertProcess();
        t.selectProcess();
        t.updateProcess();
        t.selectProcess();
        t.deleteProcess();
    }
}
