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

        // IDs seeds (adapte si besoin)
        Long userSecretaire = 2L; // Malak
        Long userMedecin = 5L;    // Dr Jihane
        Long userAdmin = 1L;      // Admin

        testUser(userSecretaire);
        testUser(userMedecin);
        testUser(userAdmin);
    }

    private void testUser(Long userId) throws ServiceException {
        System.out.println("\n--- Dashboard for userId=" + userId + " ---");

        DashboardDTO dto = dashboardService.getDashboard(userId);

        System.out.println("role     = " + dto.getRole());
        System.out.println("features = " + dto.getFeatures());

        if (dto.getSecretaire() != null) {
            System.out.println("[SECRETAIRE] recetteDuJour=" + dto.getSecretaire().getRecetteDuJour());
            System.out.println("[SECRETAIRE] rdvDuJour=" + dto.getSecretaire().getNbRdvDuJour());
        }
        if (dto.getMedecin() != null) {
            System.out.println("[MEDECIN] rdvDuJour=" + dto.getMedecin().getNbRdvDuJour());
            System.out.println("[MEDECIN] patientCourant=" + dto.getMedecin().getPatientEnCours());
        }
        if (dto.getAdmin() != null) {
            System.out.println("[ADMIN] nbUsers=" + dto.getAdmin().getNbUtilisateurs());
            System.out.println("[ADMIN] nbAdmins=" + dto.getAdmin().getNbAdmins());
        }
    }

    public static void main(String[] args) {
        try {
            new TestModuleDashboardService().process();
            System.out.println("\n✅ TestModuleDashboardService OK");
        } catch (Exception e) {
            System.err.println("\n❌ TestModuleDashboardService FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
