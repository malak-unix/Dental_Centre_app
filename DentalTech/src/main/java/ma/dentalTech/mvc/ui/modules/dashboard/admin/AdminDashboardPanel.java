package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.components.StatCard;
import ma.dentalTech.mvc.ui.common.components.TeethChartPanel;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;

    private final StatCard statUsers   = new StatCard("Utilisateurs", "0", "👥");
    private final StatCard statAdmins  = new StatCard("Administrateurs", "0", "🛡");
    private final StatCard statRecette = new StatCard("Recette du jour", "0 DH", "💰");
    private final StatCard statActes   = new StatCard("Actes réalisés", "0", "✅");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Nom", "Rôle", "Statut", "Dernière activité"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // --- Référentiels (UI) : alimenté dans reload() ---
    private final JLabel refMedecinName = new JLabel("—");
    private final DentalButton btnDossier = new DentalButton("+ Dossier");
    private final DentalButton btnSupprimer = new DentalButton("Supprimer");

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

        add(buildTopStats(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        reload();
    }

    private JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 15));
        p.setOpaque(false);
        p.add(statUsers);
        p.add(statAdmins);
        p.add(statRecette);
        p.add(statActes);
        return p;
    }

    private JComponent buildBody() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setOpaque(false);

        // ====== Centre: table users ======
        JTable table = new JTable(model);
        table.setRowHeight(34);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Utilisateurs"));
        root.add(sp, BorderLayout.CENTER);

        // ====== Droite: Données référentielles (comme maquette) ======
        root.add(buildReferentielsCard(), BorderLayout.EAST);

        // ====== Bas: actions ======
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

    private JComponent buildReferentielsCard() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(360, 10));
        right.setBorder(BorderFactory.createTitledBorder("Données Référentielles"));

        // Header : avatar + nom médecin
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        header.setOpaque(false);
        JLabel avatar = new JLabel("👨‍⚕️");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        refMedecinName.setFont(DentalTheme.textBold(14));
        refMedecinName.setForeground(DentalTheme.TEXT2);

        header.add(avatar);
        header.add(refMedecinName);

        right.add(header, BorderLayout.NORTH);

        // Centre : schéma dents + légende
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);

        center.add(new TeethChartPanel(), BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        legend.setOpaque(false);
        legend.add(dot("Sain", new Color(0x1FAF47)));
        legend.add(dot("En traitement", new Color(0x2A5BD7)));
        legend.add(dot("Problème", new Color(0xB98900)));

        center.add(legend, BorderLayout.SOUTH);
        right.add(center, BorderLayout.CENTER);

        // Actions (boutons style caisse)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        actions.setOpaque(false);

        btnDossier.addActionListener(e -> navigate.accept("dossiers"));
        btnSupprimer.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Action Supprimer (à brancher)",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        actions.add(btnDossier);
        actions.add(btnSupprimer);

        DentalButton btnRef = new DentalButton("Voir Référentiels");
        btnRef.addActionListener(e -> navigate.accept("referentiels"));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(actions, BorderLayout.NORTH);
        south.add(btnRef, BorderLayout.SOUTH);

        right.add(south, BorderLayout.SOUTH);

        return right;
    }

    private static JComponent dot(String text, Color c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(c);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel label = new JLabel(text);
        label.setFont(DentalTheme.textFont(12));
        label.setForeground(DentalTheme.TEXT2);

        p.add(dot);
        p.add(label);
        return p;
    }

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            AdminDashboardResponseDTO dto = dash.getAdmin();
            if (dto == null) return;

            statUsers.setValue(String.valueOf(nvl(dto.getNbUtilisateurs())));
            statAdmins.setValue(String.valueOf(nvl(dto.getNbAdmins())));
            statActes.setValue(String.valueOf(nvl(dto.getNbActesRealises())));
            statRecette.setValue(nvl(dto.getRecetteDuJour()) + " DH");

            // table
            model.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Référentiels : choisir un médecin à afficher (premier utilisateur MEDECIN)
            String medName = "—";

            if (dto.getUtilisateurs() != null) {
                for (UserSummaryDTO u : dto.getUtilisateurs()) {
                    String nomComplet = safe(u.getPrenom()) + " " + safe(u.getNom());
                    String last = (u.getDerniereActivite() != null) ? u.getDerniereActivite().format(fmt) : "-";
                    model.addRow(new Object[]{
                            nomComplet.trim(),
                            (u.getRole() != null) ? u.getRole().name() : "-",
                            safe(u.getStatut()),
                            last
                    });

                    if ("—".equals(medName) && u.getRole() != null && "MEDECIN".equals(u.getRole().name())) {
                        medName = ("Dr. " + nomComplet).trim();
                    }
                }
            }

            refMedecinName.setText(medName);

        } catch (Exception ex) {
            // au lieu de planter l'UI
            ex.printStackTrace();
        }
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
    private static String safe(String s) { return (s != null) ? s : ""; }
}
