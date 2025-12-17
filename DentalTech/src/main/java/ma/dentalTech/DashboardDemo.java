package ma.dentalTech;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;

public class DashboardDemo {
    public static void main(String[] args) {
        DashboardController controller = (DashboardController) ApplicationContext.getBean("dashboardController");

        if (controller == null) {
            System.out.println("dashboardController introuvable (vérifie beans.properties).");
            return;
        }


        try {
            controller.showDashboard(1L);
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }
}
