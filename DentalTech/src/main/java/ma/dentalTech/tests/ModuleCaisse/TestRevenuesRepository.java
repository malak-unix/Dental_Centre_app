package ma.dentalTech.tests.ModuleCaisse;

import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.RevenuesRepositoryJdbcImpl;

import java.time.LocalDateTime;
import java.util.List;

public class TestRevenuesRepository {

    public static void main(String[] args) {
        System.out.println("=== TEST RevenuesRepository ===");

        RevenuesRepository repo = new RevenuesRepositoryJdbcImpl();

        try {
            // Test findAll()
            List<Revenues> tousLesRevenus = repo.findAll();
            System.out.println("Nombre de revenus trouvés : " + tousLesRevenus.size());

            if (!tousLesRevenus.isEmpty()) {
                Revenues r = tousLesRevenus.get(0);
                System.out.println("Premier revenu : " + r);
            }

            // Test findByDateBetween() + total
            LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.now();

            List<Revenues> revenusPeriode = repo.findByDateBetween(start, end);
            Double totalRevenus = repo.calculateTotalOtherRevenue(start, end);

            System.out.println("Revenus dans la période : " + revenusPeriode.size());
            System.out.println("Total revenus           : " + safe(totalRevenus));

        } catch (Exception e) {
            System.out.println("Erreur pendant le test de RevenuesRepository : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST RevenuesRepository ===");
    }

    private static double safe(Double d) {
        return d != null ? d : 0.0;
    }
}
