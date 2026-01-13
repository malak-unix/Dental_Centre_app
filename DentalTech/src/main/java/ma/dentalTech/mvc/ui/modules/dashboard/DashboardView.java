package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DashboardView extends JPanel {

    public DashboardView(LibelleRole role, Long userId, Consumer<String> navigate) {
        setOpaque(false);
        setLayout(new BorderLayout());

        DashboardController dashboardController =
                ApplicationContext.getBean(DashboardController.class);

        add(new DashboardMainPanel(role, userId, dashboardController, navigate), BorderLayout.CENTER);
    }
}
