package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardMainPanel extends JPanel {

    private final DashboardController controller;
    private final Long currentUserId;
    private final LibelleRole role;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final SecretaireDashboardPanel secretairePanel = new SecretaireDashboardPanel();
    private final AdminDashboardPanel adminPanel = new AdminDashboardPanel();
    private final MedecinDashboardPanel medecinPanel = new MedecinDashboardPanel();

    public DashboardMainPanel(LibelleRole role, Long currentUserId) {
        this.role = role;
        this.currentUserId = currentUserId;

        Object bean = ApplicationContext.getBean("dashboardController");
        if (!(bean instanceof DashboardController c)) {
            throw new IllegalStateException("dashboardController introuvable dans ApplicationContext");
        }
        this.controller = c;

        setLayout(new BorderLayout());
        setBackground(DentalTheme.BG);

        cards.setOpaque(false);
        cards.add(secretairePanel, "SECRETAIRE");
        cards.add(adminPanel, "ADMIN");
        cards.add(medecinPanel, "MEDECIN");

        add(cards, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        try {
            DashboardDTO dto = controller.getDashboardDTO(currentUserId);

            // 1) rôle à utiliser : priorité au param role, sinon dto.role, sinon fallback
            String r = (role != null) ? role.name() : (dto != null ? dto.getRole() : null);
            if (r == null || r.isBlank()) r = inferRole(dto);

            // 2) remplir panels si data dispo
            if (dto != null) {
                if (dto.getSecretaire() != null) secretairePanel.setData(dto.getSecretaire());
                if (dto.getAdmin() != null) adminPanel.setData(dto.getAdmin());
                if (dto.getMedecin() != null) medecinPanel.setData(dto.getMedecin());
            }

            cardLayout.show(cards, r);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur Dashboard: " + ex.getMessage(),
                    "Dashboard", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String inferRole(DashboardDTO dto) {
        if (dto == null) return "SECRETAIRE";
        if (dto.getAdmin() != null) return "ADMIN";
        if (dto.getMedecin() != null) return "MEDECIN";
        return "SECRETAIRE";
    }
}
