package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.NavButton;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardView extends JPanel {

    private final DashboardPanel shell;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final List<NavButton> navButtons = new ArrayList<>();

    public DashboardView() {
        setLayout(new BorderLayout());
        setOpaque(false);

        shell = new DashboardPanel();
        add(shell, BorderLayout.CENTER);

        // Cards (écrans rôle)
        cards.setOpaque(false);
        cards.add(new SecretaireDashboardPanel(), "SECRETAIRE");
        cards.add(new AdminDashboardPanel(), "ADMIN");
        cards.add(new MedecinDashboardPanel(), "MEDECIN");

        shell.setContent(cards);

        buildHeader();
        buildSidebarForRole("SECRETAIRE");
        cardLayout.show(cards, "SECRETAIRE");
    }

    private void buildHeader() {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel("DENTAL CENTER");
        logo.setFont(DentalTheme.H2);
        logo.setForeground(DentalTheme.TEXT);
        left.add(logo);

        JTextField search = new JTextField("Rechercher ...");
        search.setPreferredSize(new Dimension(420, 36));
        left.add(search);

        shell.header().add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JComboBox<String> roleSwitch = new JComboBox<>(new String[]{"SECRETAIRE", "ADMIN", "MEDECIN"});
        roleSwitch.setPreferredSize(new Dimension(140, 34));
        roleSwitch.addActionListener(e -> {
            String role = (String) roleSwitch.getSelectedItem();
            buildSidebarForRole(role);
            cardLayout.show(cards, role);
        });

        JLabel user = new JLabel("Utilisateur");
        user.setFont(DentalTheme.BASE_BOLD);

        right.add(roleSwitch);
        right.add(user);

        shell.header().add(right, BorderLayout.EAST);
    }

    private void buildSidebarForRole(String role) {
        JPanel sb = shell.sidebar();
        sb.removeAll();
        navButtons.clear();

        // Espace haut (comme maquette)
        sb.add(Box.createVerticalStrut(10));

        if ("SECRETAIRE".equals(role)) {
            addNav(sb, "Dashboard", () -> cardLayout.show(cards, "SECRETAIRE"));
            addNav(sb, "Les patients", () -> {});
            addNav(sb, "Rendez-vous", () -> {});
            addNav(sb, "La caisse", () -> {});
        } else if ("ADMIN".equals(role)) {
            addNav(sb, "Dashboard", () -> cardLayout.show(cards, "ADMIN"));
            addNav(sb, "Utilisateurs", () -> {});
            addNav(sb, "Référentiels", () -> {});
            addNav(sb, "Sauvegardes", () -> {});
        } else {
            addNav(sb, "Dashboard", () -> cardLayout.show(cards, "MEDECIN"));
            addNav(sb, "Mes patients", () -> {});
            addNav(sb, "Mes consultations", () -> {});
            addNav(sb, "Les planning", () -> {});
            addNav(sb, "Ordonnances", () -> {});
        }

        // sélection visuelle du 1er
        if (!navButtons.isEmpty()) navButtons.get(0).setSelectedStyle(true);

        sb.add(Box.createVerticalGlue());
        sb.revalidate();
        sb.repaint();
    }

    private void addNav(JPanel sb, String label, Runnable action) {
        NavButton b = new NavButton(label);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        b.addActionListener(e -> {
            navButtons.forEach(x -> x.setSelectedStyle(false));
            b.setSelectedStyle(true);
            action.run();
        });

        navButtons.add(b);
        sb.add(b);
        sb.add(Box.createVerticalStrut(12));
    }
}
