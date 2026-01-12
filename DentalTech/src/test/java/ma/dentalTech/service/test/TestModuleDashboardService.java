package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

public class TestModuleDashboardService {

    private final DashboardService dashboardService =
            ApplicationContext.getBean(DashboardService.class);

    public void process() throws ServiceException {
        System.out.println("\n==================================================");
        System.out.println("            TEST MODULE DASHBOARD");
        System.out.println("==================================================");

        Long userId = 1L; // ⚠️ adapte (user existant)

        DashboardDTO dto = dashboardService.getDashboard(userId);

        System.out.println("role     = " + dto.getRole());
        System.out.println("features = " + dto.getFeatures());

        if (dto.getSecretaire() != null) {
            System.out.println("[SECRETAIRE] recette=" + dto.getSecretaire().getRecetteDuJour());
        }
        if (dto.getMedecin() != null) {
            System.out.println("[MEDECIN] rdv=" + dto.getMedecin().getNbRdvDuJour());
        }
        if (dto.getAdmin() != null) {
            System.out.println("[ADMIN] users=" + dto.getAdmin().getNbUtilisateurs());
        }

        System.out.println("\n✅ FIN TEST MODULE DASHBOARD");
    }

    public static void main(String[] args) {
        try {
            new TestModuleDashboardService().process();
        } catch (ServiceException e) {
            System.err.println("\n❌ ServiceException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
