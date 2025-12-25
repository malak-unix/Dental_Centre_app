package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel(DashboardDTO dto) {
        setLayout(new BorderLayout());

        if (dto == null || dto.getRole() == null) {
            add(new JLabel("Dashboard indisponible"), BorderLayout.CENTER);
            return;
        }

        switch (dto.getRole()) {
            case "SECRETAIRE" -> add(new SecretaireDashboardPanel(dto.getSecretaire()), BorderLayout.CENTER);
            case "MEDECIN" -> add(new MedecinDashboardPanel(dto.getMedecin()), BorderLayout.CENTER);
            case "ADMIN" -> add(new AdminDashboardPanel(dto.getAdmin()), BorderLayout.CENTER);
            default -> add(new JLabel("Rôle non supporté : " + dto.getRole()), BorderLayout.CENTER);
        }
    }
}
