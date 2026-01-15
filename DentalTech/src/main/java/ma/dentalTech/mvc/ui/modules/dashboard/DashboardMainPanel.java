package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;


public class DashboardMainPanel extends JPanel {

    private final LibelleRole role;
    private final Long userId;
    private final Consumer<String> navigate;

    private final DashboardController dashboardController =
            ApplicationContext.getBean(DashboardController.class);

    public DashboardMainPanel(LibelleRole role,
                              Long userId,
                              DashboardController dashboardController,
                              Consumer<String> navigate) {

        this.role = (role != null) ? role : LibelleRole.SECRETAIRE;
        this.userId = (userId != null) ? userId : 1L;
        this.navigate = (navigate != null) ? navigate : (k -> {});


        setLayout(new BorderLayout());
        setOpaque(false);

        // DEBUG pour vérifier que controller n'est pas null
        System.out.println("dashboardController = " + dashboardController);
        System.out.println("role = " + role + " userId = " + userId);


        add(buildDashboardByRole(), BorderLayout.CENTER);
    }

    private JComponent buildDashboardByRole() {
        return switch (role) {
            case SECRETAIRE -> new SecretaireDashboardPanel(dashboardController, userId, navigate);
            case MEDECIN    -> new MedecinDashboardPanel(dashboardController, userId, navigate);
            case ADMIN      -> new AdminDashboardPanel(dashboardController, userId, navigate);
        };
    }
}
