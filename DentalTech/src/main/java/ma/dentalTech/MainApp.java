package ma.dentalTech;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.ui.modules.dashboard.DashboardFrame;

import javax.swing.*;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Long currentUserId = 1L; // ✅ temporaire (plus tard via Auth)
                DashboardController controller =
                        (DashboardController) ApplicationContext.getBean("dashboardController");

                DashboardDTO dto = controller.getDashboardDTO(1L);
                DashboardFrame frame = new DashboardFrame(dto);
                frame.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur lancement", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
