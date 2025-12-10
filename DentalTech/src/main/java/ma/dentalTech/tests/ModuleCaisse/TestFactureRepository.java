package ma.dentalTech.tests.ModuleCaisse;

import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.FactureRepositoryJdbcImpl;

import java.time.LocalDateTime;
import java.util.List;

public class TestFactureRepository {

    public static void main(String[] args) {
        System.out.println("=== TEST FactureRepository ===");

        FactureRepository repo = new FactureRepositoryJdbcImpl();

        try {
            // Test findAll()
            List<Facture> toutesLesFactures = repo.findAll();
            System.out.println("Nombre de factures trouvées : " + toutesLesFactures.size());

            if (!toutesLesFactures.isEmpty()) {
                Facture f = toutesLesFactures.get(0);
                System.out.println("Première facture : " + f);

                // Test findById()
                Facture fById = repo.findById(f.getId());
                System.out.println("Facture par ID (" + f.getId() + ") : " + fById);
            }

            // Test des méthodes de statistiques
            LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.now();

            Double totalFactures = repo.calculateTotalFactures(start, end);
            Double totalRegle = repo.calculateTotalRegle(start, end);
            Double totalNonRegle = repo.calculateTotalNonRegle(start, end);

            System.out.println("Total factures      : " + safe(totalFactures));
            System.out.println("Total réglé         : " + safe(totalRegle));
            System.out.println("Total non réglé     : " + safe(totalNonRegle));

        } catch (Exception e) {
            System.out.println("Erreur pendant le test de FactureRepository : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST FactureRepository ===");
    }

    private static double safe(Double d) {
        return d != null ? d : 0.0;
    }
}
