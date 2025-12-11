package ma.dentalTech.tests.ModuleCaisse;

import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.ChargesRepositoryJdbcImpl;

import java.time.LocalDateTime;
import java.util.List;

public class TestChargesRepository {

    public static void main(String[] args) {
        System.out.println("=== TEST ChargesRepository ===");

        ChargesRepository repo = new ChargesRepositoryJdbcImpl();

        try {
            // Test findAll()
            List<Charges> toutesLesCharges = repo.findAll();
            System.out.println("Nombre de charges trouvées : " + toutesLesCharges.size());

            if (!toutesLesCharges.isEmpty()) {
                Charges c = toutesLesCharges.get(0);
                System.out.println("Première charge : " + c);
            }

            // Test findByDateBetween() + total
            LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.now();

            List<Charges> chargesPeriode = repo.findByDateBetween(start, end);
            Double totalCharges = repo.calculateTotalCharges(start, end);

            System.out.println("Charges dans la période : " + chargesPeriode.size());
            System.out.println("Total charges           : " + safe(totalCharges));

        } catch (Exception e) {
            System.out.println("Erreur pendant le test de ChargesRepository : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST ChargesRepository ===");
    }

    private static double safe(Double d) {
        return d != null ? d : 0.0;
    }
}
