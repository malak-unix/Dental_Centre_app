package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.common.exceptions.DaoException;

import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import java.time.LocalDateTime;

public class TestRepo {

    // Repos (caisse)
    private final FactureRepository factureRepo =
            ApplicationContext.getBean(FactureRepository.class);
    private final ChargesRepository chargesRepo =
            ApplicationContext.getBean(ChargesRepository.class);
    private final RevenuesRepository revenusRepo =
            ApplicationContext.getBean(RevenuesRepository.class);
    private final SituationFinanciereRepository sitFinRepo =
            ApplicationContext.getBean(SituationFinanciereRepository.class);

    void insertProcess() throws DaoException {
        System.out.println("\n=== INSERT PROCESS ===");
        // TODO: Ajoute ici insert() quand vous avez les entities finales + relations
        // Exemple:
        // Facture f = Facture.builder()....build();
        // factureRepo.insert(f);
        System.out.println("Insert process: OK (à compléter selon entities).");
    }

    void updateProcess() throws DaoException {
        System.out.println("\n=== UPDATE PROCESS ===");
        // TODO: update d'un enregistrement existant
        System.out.println("Update process: OK (à compléter).");
    }

    void deleteProcess() throws DaoException {
        System.out.println("\n=== DELETE PROCESS ===");
        // TODO: deleteById(id)
        System.out.println("Delete process: OK (à compléter).");
    }

    void selectProcess() throws DaoException {
        System.out.println("\n=== SELECT PROCESS ===");

        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        System.out.println("Total factures (30j) = " + factureRepo.calculateTotalFactures(start, end));
        System.out.println("Total réglé   (30j) = " + factureRepo.calculateTotalRegle(start, end));
        System.out.println("Total non réglé(30j) = " + factureRepo.calculateTotalNonRegle(start, end));

        System.out.println("Total charges (30j) = " + chargesRepo.calculateTotalCharges(start, end));
        System.out.println("Total revenus (30j) = " + revenusRepo.calculateTotalRevenus(start, end));

        // si vous avez une méthode calcul SF, sinon laisse
        // System.out.println("Situation financière = " + sitFinRepo....);
        System.out.println("Select process: OK");
    }

    public static void main(String[] args) {
        try {
            TestRepo t = new TestRepo();
            t.insertProcess();
            t.updateProcess();
            t.deleteProcess();
            t.selectProcess();
            System.out.println("\n✅ TEST REPO terminé avec succès.");
        } catch (Exception e) {
            System.err.println("\n❌ TEST REPO échoué : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
