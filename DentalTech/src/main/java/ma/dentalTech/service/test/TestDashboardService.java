package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

public class TestDashboardService {

    private final DashboardService dashboardService =
            ApplicationContext.getBean(DashboardService.class);

    void process(Long userId) throws Exception {
        System.out.println("\n=== DASHBOARD SERVICE ===");
        DashboardDTO dto = dashboardService.getDashboard(userId);
        System.out.println("Role = " + dto.getRole());
        System.out.println("Date = " + dto.getDateJour());
        System.out.println("Features = " + dto.getFeatures());
        System.out.println("CaisseDuJour = " + dto.getCaisseDuJour());
    }

    public static void main(String[] args) {
        try {
            new TestDashboardService().process(1L); // mets un userId existant
            System.out.println("\n✅ Test dashboard service terminé.");
        } catch (Exception e) {
            System.err.println("\n❌ Test dashboard service échoué : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
