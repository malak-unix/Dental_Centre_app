package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController dashboardController;
    private final Long userId;
    private final Consumer<String> navigate;

    private JLabel vUsers;
    private JLabel vAdmins;
    private JLabel vRecetteJour;
    private JLabel vActes;

    public AdminDashboardPanel(DashboardController dashboardController, Long userId, Consumer<String> navigate) {
        this.dashboardController = dashboardController;
        this.userId = userId;
        this.navigate = navigate;

        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Statistiques Globales");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);

        vUsers = new JLabel("—");
        vAdmins = new JLabel("—");
        vRecetteJour = new JLabel("—");
        vActes = new JLabel("—");

        kpis.add(kpi(vUsers, "Utilisateurs"));
        kpis.add(kpi(vAdmins, "Administrateurs"));
        kpis.add(kpi(vRecetteJour, "Recette du jour"));
        kpis.add(kpi(vActes, "Actes réalisés"));
        kpis.add(actionCard("Gestion Utilisateurs", () -> navigate.accept("utilisateurs")));

        main.add(kpis);
        main.add(Box.createVerticalStrut(18));

        // Zone actions rapides (admin)
        JPanel actions = new JPanel(new GridLayout(1, 3, 18, 18));
        actions.setOpaque(false);

        actions.add(actionCard("Référentiels", () -> navigate.accept("referentiels")));
        actions.add(actionCard("Sauvegardes", () -> navigate.accept("sauvegardes")));
        actions.add(actionCard("Accès Dossier Médical", () -> navigate.accept("dossier_medical"))); // à brancher dans MainFrame

        main.add(actions);

        // load real data
        reload();
    }

    private void reload() {
        try {
            DashboardDTO dto = dashboardController.getDashboardDTO(userId);
            AdminDashboardResponseDTO admin = dto != null ? dto.getAdmin() : null;

            int users = admin != null && admin.getNbUtilisateurs() != null ? admin.getNbUtilisateurs() : 0;
            int admins = admin != null && admin.getNbAdmins() != null ? admin.getNbAdmins() : 0;
            String recette = admin != null && admin.getRecetteDuJour() != null ? admin.getRecetteDuJour() + " DH" : "0 DH";
            int actes = admin != null && admin.getNbActesRealises() != null ? admin.getNbActesRealises() : 0;

            vUsers.setText(String.valueOf(users));
            vAdmins.setText(String.valueOf(admins));
            vRecetteJour.setText(recette);
            vActes.setText(String.valueOf(actes));

        } catch (ControllerException ex) {
            // si erreur, on reste stable
            vUsers.setText("0");
            vAdmins.setText("0");
            vRecetteJour.setText("0 DH");
            vActes.setText("0");
        }
    }

    private CardPanel kpi(JLabel valueLabel, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        valueLabel.setFont(DentalTheme.H2);
        valueLabel.setForeground(DentalTheme.TEXT);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.BASE);
        l.setForeground(DentalTheme.MUTED);

        c.add(valueLabel);
        c.add(Box.createVerticalStrut(6));
        c.add(l);
        return c;
    }

    private CardPanel actionCard(String text, Runnable action) {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout());
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.addActionListener(e -> action.run());
        c.add(b, BorderLayout.CENTER);
        return c;
    }
}
