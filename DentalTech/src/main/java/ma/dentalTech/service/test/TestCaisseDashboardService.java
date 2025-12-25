package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestCaisseDashboardService {

    private final CaisseDashboardService service =
            ApplicationContext.getBean(CaisseDashboardService.class);

    void process() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

        System.out.println("\n=== CAISSE DASHBOARD SERVICE ===");
        System.out.println("Total factures = " + service.totalFactures(start, end));
        System.out.println("Total réglé    = " + service.totalRegle(start, end));
        System.out.println("Total non réglé= " + service.totalNonRegle(start, end));
        System.out.println("Total revenus  = " + service.totalRevenus(start, end));
        System.out.println("Total charges  = " + service.totalCharges(start, end));
        System.out.println("Solde          = " + service.solde(start, end));
    }

    public static void main(String[] args) {
        try {
            new TestCaisseDashboardService().process();
            System.out.println("\n Test service caisse terminé.");
        } catch (Exception e) {
            System.err.println("\n Test service caisse échoué : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
