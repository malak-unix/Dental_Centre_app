package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import javax.swing.*;

public class test {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DashboardService service = ApplicationContext.getBean(DashboardService.class);
                DashboardDTO dto = service.getDashboard(1L); // userId existant

                JFrame f = new JFrame("DentalTech - Dashboard");
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.setSize(1200, 750);
                f.setLocationRelativeTo(null);

                f.setContentPane(new DashboardView(dto));
                f.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
