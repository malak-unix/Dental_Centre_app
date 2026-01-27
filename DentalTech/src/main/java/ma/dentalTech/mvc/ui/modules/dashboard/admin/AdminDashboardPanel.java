package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.common.ActivityDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.mvc.ui.common.components.StatCardPro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private final StatCardPro statUsers       = new StatCardPro("Utilisateurs", "0", "");
    private final StatCardPro statMedecins    = new StatCardPro("Medecins", "0", "");
    private final StatCardPro statSecretaires = new StatCardPro("Secretaires", "0", "");
    private final StatCardPro statRecette     = new StatCardPro("Recette du jour", "0 DH", "");

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
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        activitiesPanel.setOpaque(false);
        activitiesPanel.setLayout(new BoxLayout(activitiesPanel, BoxLayout.Y_AXIS));

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        card.add(buildTopStats(), BorderLayout.NORTH);
        card.add(buildBody(), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        reload();
    }

    private JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 18, 18));
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
        UiStyles.styleTable(table);
        table.setRowHeight(34);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(10, 220));
        sp.setMinimumSize(new Dimension(10, 200));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        CardPanel tableCard = new CardPanel((String) null);
        tableCard.setBackground(DentalTheme.CARD);
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableCard.setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Utilisateurs");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        tableCard.add(header, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);
        root.add(tableCard, BorderLayout.CENTER);

        root.add(buildActivitiesCard(), BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        DentalButton bUsers = new DentalButton("Utilisateurs");
        UiStyles.stylePrimaryButton(bUsers);
        bUsers.addActionListener(e -> navigate.accept("utilisateurs"));

        DentalButton bBackups = new DentalButton("Sauvegardes");
        UiStyles.stylePrimaryButton(bBackups);
        bBackups.addActionListener(e -> navigate.accept("sauvegardes"));

        actions.add(bUsers);
        actions.add(bBackups);
        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private JComponent buildActivitiesCard() {
        CardPanel right = new CardPanel((String) null);
        right.setBackground(DentalTheme.CARD);
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setLayout(new BorderLayout(8, 8));
        right.setPreferredSize(new Dimension(320, 10));

        JScrollPane sp = new JScrollPane(activitiesPanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setPreferredSize(new Dimension(10, 220));

        JLabel title = new JLabel("Activites recentes");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        right.add(header, BorderLayout.NORTH);
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
            List<ActivityDTO> activities = dto.getActivities();
            if (activities != null && !activities.isEmpty()) {
                for (ActivityDTO a : activities) {
                    activitiesPanel.add(buildActivityRow(a, fmt));
                    activitiesPanel.add(Box.createVerticalStrut(8));
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

    private JComponent buildActivityRow(ActivityDTO a, DateTimeFormatter fmt) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setOpaque(false);

        String msg = safe(a.getMessage());
        String last = (a.getDate() != null) ? a.getDate().format(fmt) : "-";

        JTextArea txt = new JTextArea(msg);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setFont(DentalTheme.textFont(12));
        txt.setForeground(DentalTheme.TEXT2);

        JLabel time = new JLabel(last);
        time.setFont(DentalTheme.textFont(11));
        time.setForeground(DentalTheme.MUTED_TEXT);

        row.add(txt, BorderLayout.CENTER);
        row.add(time, BorderLayout.SOUTH);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return row;
    }
}
