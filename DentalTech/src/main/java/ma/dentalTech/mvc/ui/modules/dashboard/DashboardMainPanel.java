package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DashboardMainPanel extends JPanel {

    private final LibelleRole role;
    private final Long userId;
    private final DashboardController dashboardController;
    private final Consumer<String> navigate;

    public DashboardMainPanel(LibelleRole role,
                              Long userId,
                              DashboardController dashboardController,
                              Consumer<String> navigate) {

        this.role = (role != null) ? role : LibelleRole.SECRETAIRE;
        this.userId = (userId != null) ? userId : 1L;
        this.dashboardController = dashboardController;
        this.navigate = (navigate != null) ? navigate : (k -> {});

        setLayout(new BorderLayout());
        setOpaque(false);

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
