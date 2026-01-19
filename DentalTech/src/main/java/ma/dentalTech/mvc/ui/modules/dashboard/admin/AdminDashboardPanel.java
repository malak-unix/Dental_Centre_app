package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.components.StatCard;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private final StatCard statUsers       = new StatCard("Utilisateurs", "0", "");
    private final StatCard statMedecins    = new StatCard("Medecins", "0", "");
    private final StatCard statSecretaires = new StatCard("Secretaires", "0", "");
    private final StatCard statRecette     = new StatCard("Recette du jour", "0 DH", "");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Nom", "Role", "Statut", "Derniere activite"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JPanel activitiesPanel = new JPanel();

    public AdminDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = (controller != null)
                ? controller
                : ApplicationContext.getBean(DashboardController.class);

        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});

        setLayout(new BorderLayout(15, 15));
        setBackground(DentalTheme.BG);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        activitiesPanel.setOpaque(false);
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));

        add(buildTopStats(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        reload();
    }

    private JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 15));
        p.setOpaque(false);
        p.add(statUsers);
        p.add(statMedecins);
        p.add(statSecretaires);
        p.add(statRecette);
        return p;
    }

    private JComponent buildBody() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        TitledBorder t = BorderFactory.createTitledBorder("Utilisateurs");
        t.setTitleFont(DentalTheme.textBold(13));
        sp.setBorder(t);
        root.add(sp, BorderLayout.CENTER);

        root.add(buildActivitiesCard(), BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        DentalButton bUsers = new DentalButton("Utilisateurs");
        bUsers.addActionListener(e -> navigate.accept("utilisateurs"));

        DentalButton bBackups = new DentalButton("Sauvegardes");
        bBackups.addActionListener(e -> navigate.accept("sauvegardes"));

        actions.add(bUsers);
        actions.add(bBackups);
        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildActivitiesCard() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(320, 10));

        TitledBorder t = BorderFactory.createTitledBorder("Activites recentes");
        t.setTitleFont(DentalTheme.textBold(13));
        right.setBorder(t);

        JScrollPane sp = new JScrollPane(activitiesPanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        right.add(sp, BorderLayout.CENTER);
        return right;
    }

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            AdminDashboardResponseDTO dto = dash.getAdmin();
            if (dto == null) return;

            statUsers.setValue(String.valueOf(nvl(dto.getNbUtilisateurs())));
            statRecette.setValue(nvl(dto.getRecetteDuJour()) + " DH");

            int nbMedecins = 0;
            int nbSecretaires = 0;

            model.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            List<UserSummaryDTO> users = dto.getUtilisateurs();
            if (users != null) {
                for (UserSummaryDTO u : users) {
                    String nomComplet = safe(u.getPrenom()) + " " + safe(u.getNom());
                    String role = (u.getRole() != null) ? u.getRole().name() : "-";
                    String last = (u.getDerniereActivite() != null) ? u.getDerniereActivite().format(fmt) : "-";
                    model.addRow(new Object[]{
                            nomComplet.trim(),
                            role,
                            safe(u.getStatut()),
                            last
                    });
                    if ("MEDECIN".equals(role)) nbMedecins++;
                    if ("SECRETAIRE".equals(role)) nbSecretaires++;
                }
            }

            statMedecins.setValue(String.valueOf(nbMedecins));
            statSecretaires.setValue(String.valueOf(nbSecretaires));

            activitiesPanel.removeAll();
            if (users != null && !users.isEmpty()) {
                int limit = Math.min(6, users.size());
                for (int i = 0; i < limit; i++) {
                    UserSummaryDTO u = users.get(i);
                    String line = safe(u.getPrenom()) + " " + safe(u.getNom());
                    String last = (u.getDerniereActivite() != null) ? u.getDerniereActivite().format(fmt) : "-";
                    JLabel row = new JLabel(line.trim() + " - " + last);
                    row.setFont(DentalTheme.textFont(12));
                    row.setForeground(DentalTheme.TEXT2);
                    row.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
                    activitiesPanel.add(row);
                }
            } else {
                JLabel empty = new JLabel("Aucune activite recente");
                empty.setFont(DentalTheme.textFont(12));
                empty.setForeground(DentalTheme.MUTED);
                activitiesPanel.add(empty);
            }
            activitiesPanel.revalidate();
            activitiesPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
    private static String safe(String s) { return (s != null) ? s : ""; }
}
