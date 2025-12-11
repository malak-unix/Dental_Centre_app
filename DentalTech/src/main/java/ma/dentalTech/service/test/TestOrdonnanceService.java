package ma.dentalTech.service.test;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.ordonnance.Ordonnance;
import ma.dentalTech.repository.modules.ordonnance.api.OrdonnanceRepository;
import ma.dentalTech.service.modules.ordonnance.api.OrdonnanceService;
import ma.dentalTech.service.modules.ordonnance.baseImplementation.OrdonnanceServiceImpl;

import java.time.LocalDate;
import java.util.List;

public class TestOrdonnanceService {

    private final OrdonnanceService ordonnanceService;

    // on garde l’id créé pour pouvoir tester select/delete
    private Long ordonnanceId;

    public TestOrdonnanceService() {
        // on récupère le repository depuis l’ApplicationContext
        OrdonnanceRepository ordoRepo = ApplicationContext.getBean(OrdonnanceRepository.class);
        // et on construit le service à partir de ce repo
        this.ordonnanceService = new OrdonnanceServiceImpl(ordoRepo);
    }

    // =====================================================
    // INSERT : tester creerOrdonnance(...)
    // =====================================================
    void insertProcess() {

        System.out.println("=== [OrdonnanceService] INSERT ===");

        // dossier_id = 1 et consultation_id = 1 viennent de tes seeds
        Ordonnance o = ordonnanceService.creerOrdonnance(
                1L,                       // dossierId
                1L,                       // consultationId
                LocalDate.now(),          // date de l’ordonnance
                "TEST_SERVICE"            // utilisateur courant
        );

        ordonnanceId = o.getId();
        System.out.println("Ordonnance créée via service, id = " + ordonnanceId);
    }

    // =====================================================
    // SELECT : tester getById / getByDossier / getByConsultation / getByDate
    // =====================================================
    void selectProcess() {

        System.out.println("=== [OrdonnanceService] SELECT ===");

        if (ordonnanceId != null) {
            Ordonnance o = ordonnanceService.getById(ordonnanceId);
            System.out.println("getById(" + ordonnanceId + ") => " + o);
        }

        // par dossier
        List<Ordonnance> listDossier = ordonnanceService.getByDossier(1L);
        System.out.println("getByDossier(1) => " + listDossier.size() + " ordonnances");

        // par consultation
        List<Ordonnance> listCons = ordonnanceService.getByConsultation(1L);
        System.out.println("getByConsultation(1) => " + listCons.size() + " ordonnances");

        // par date (aujourd’hui)
        List<Ordonnance> listDate = ordonnanceService.getByDate(LocalDate.now());
        System.out.println("getByDate(aujourd’hui) => " + listDate.size() + " ordonnances");
    }

    // =====================================================
    // UPDATE : pour l’instant, ton service n’a pas encore de méthode update
    // =====================================================
    void updateProcess() {

        System.out.println("=== [OrdonnanceService] UPDATE ===");
        // Pour le moment, aucune méthode métier de mise à jour
        // à ce niveau de service.
        // Quand tu ajouteras par ex: mettreAJourDate(...),
        // on l’appellera ici.
    }

    // =====================================================
    // DELETE : tester supprimerOrdonnance(...)
    // =====================================================
    void deleteProcess() {

        System.out.println("=== [OrdonnanceService] DELETE ===");

        if (ordonnanceId != null) {
            ordonnanceService.supprimerOrdonnance(ordonnanceId);
            System.out.println("Ordonnance supprimée via service, id = " + ordonnanceId);
        } else {
            System.out.println("Aucune ordonnance à supprimer (ordonnanceId == null)");
        }
    }

    public static void main(String[] args) {
        TestOrdonnanceService t = new TestOrdonnanceService();

        t.insertProcess();
        t.selectProcess();
        t.updateProcess();   // pour l’instant, ne fait rien de spécial
        t.selectProcess();   // on vérifie que la lecture fonctionne encore
        t.deleteProcess();
    }
}
