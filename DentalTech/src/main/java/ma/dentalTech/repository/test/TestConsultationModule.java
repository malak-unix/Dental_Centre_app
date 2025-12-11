package ma.dentalTech.repository.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.interventionMedecin.InterventionMedecin;
import ma.dentalTech.repository.modules.consultation.api.ConsultationRepository;
import ma.dentalTech.repository.modules.consultation.api.InterventionMedecinRepository;

import java.util.List;

public class TestConsultationModule {

    private final ConsultationRepository consultationRepo;
    private final InterventionMedecinRepository interventionRepo;

    private Long idIntervention;

    public TestConsultationModule() {
        this.consultationRepo = ApplicationContext.getBean(ConsultationRepository.class);
        this.interventionRepo = ApplicationContext.getBean(InterventionMedecinRepository.class);
    }

    // INSERT
    void insertProcess() {

        System.out.println("=== [Consultation] INSERT (Intervention) ===");

        // consultation_id = 1 et id = 101 doivent exister (seeds.sql)
        InterventionMedecin inter = InterventionMedecin.builder()
                .consultationId(1L)
                .acteId(1L)
                .prixDePatient(300.0)
                .numDent(26)
                .creePar("TEST")
                .modifiePar("TEST")
                .build();

        interventionRepo.create(inter);
        idIntervention = inter.getId();
        System.out.println("Intervention créée id=" + idIntervention);
    }

    // SELECT
    void selectProcess() {

        System.out.println("=== [Consultation] SELECT ===");

        // Lire la consultation 1 (créée dans seeds.sql)
        var cons = consultationRepo.findById(1L);
        System.out.println("Consultation id=1 : " + cons);

        if (idIntervention != null) {
            InterventionMedecin inter = interventionRepo.findById(idIntervention);
            System.out.println("Intervention trouvée : " + inter);
        }

        List<InterventionMedecin> list = interventionRepo.findByConsultationId(1L);
        System.out.println("Nb interventions pour consultation 1 = " + list.size());
    }

    // UPDATE
    void updateProcess() {

        System.out.println("=== [Consultation] UPDATE (Intervention) ===");
        if (idIntervention == null) return;

        InterventionMedecin inter = interventionRepo.findById(idIntervention);
        if (inter == null) return;

        inter.setPrixDePatient(inter.getPrixDePatient() + 50.0);
        inter.setNumDent(27);
        inter.setModifiePar("TEST_UPDATE");

        interventionRepo.update(inter);
        System.out.println("Intervention après update : " + interventionRepo.findById(idIntervention));
    }

    // DELETE
    void deleteProcess() {

        System.out.println("=== [Consultation] DELETE (Intervention) ===");
        if (idIntervention == null) return;

        interventionRepo.deleteById(idIntervention);
        System.out.println("Intervention supprimée id=" + idIntervention);
    }

    public static void main(String[] args) {
        TestConsultationModule t = new TestConsultationModule();
        t.insertProcess();
        t.selectProcess();
        t.updateProcess();
        t.selectProcess();
        t.deleteProcess();
    }
}
