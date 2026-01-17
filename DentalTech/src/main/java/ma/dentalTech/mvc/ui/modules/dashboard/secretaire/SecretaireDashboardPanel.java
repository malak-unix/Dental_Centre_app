package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.controllers.modules.users.api.NotificationController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.patient.PatientFormDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SecretaireDashboardPanel extends JPanel {

    private final DashboardController controller;
    private final Long userId;
    private final Consumer<String> navigate;
    private final ListeAttenteController listeAttenteController;
    private final NotificationController notificationController;
    private final PatientController patientController;

    // Header
    private final JTextField searchField = new JTextField();

    // Revue du jour (cards)
    private final JLabel vPatients = valueLabel("0");
    private final JLabel vRecette  = valueLabel("0 DH");
    private final JLabel vRdv      = valueLabel("0");
    private final JLabel vAttente  = valueLabel("0");

    // File d'attente
    private final JPanel fileAttentePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
    private final JLabel fileAttenteEmptyLabel = new JLabel("Aucun patient en attente aujourd'hui");

    // Notifications (après file d’attente)
    private final JPanel notificationsPanel = new JPanel();
    private  JScrollPane notificationsScroll;
    private final TitledBorder notifBorder = BorderFactory.createTitledBorder("NOTIFICATIONS");

    // Activités récentes (bas gauche + bas droite)
    private final JPanel activitiesLeft = new JPanel();

    // RDV du jour
    private final DefaultTableModel rdvModel = new DefaultTableModel(
            new Object[]{"Heure", "Patient", "Statut"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable rdvTable = new JTable(rdvModel);
    private final JPanel rdvCardPanel = new JPanel(new CardLayout());
    private final JLabel rdvEmptyLabel = new JLabel("Aucun rendez-vous aujourd'hui");
    private final List<RdvDto> rdvDuJourCache = new ArrayList<>();

    public SecretaireDashboardPanel(DashboardController controller, Long userId, Consumer<String> navigate) {
        this.controller = (controller != null)
                ? controller
                : ApplicationContext.getBean(DashboardController.class);
        this.userId = userId;
        this.navigate = (navigate != null) ? navigate : (k -> {});
        this.listeAttenteController = getBeanOrNull(ListeAttenteController.class);
        this.notificationController = getBeanOrNull(NotificationController.class);
        this.patientController = getBeanOrNull(PatientController.class);

        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // IMPORTANT : init notifications AVANT buildMain()
        notificationsPanel.setOpaque(false);
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        notificationsScroll = new JScrollPane(notificationsPanel);
        notificationsScroll.setBorder(notifBorder);
        notificationsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notificationsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        notificationsScroll.getVerticalScrollBar().setUnitIncrement(16);
        notificationsScroll.setPreferredSize(new Dimension(10, 220));

        fileAttenteEmptyLabel.setForeground(DentalTheme.MUTED_TEXT);
        rdvEmptyLabel.setForeground(DentalTheme.MUTED_TEXT);
        rdvEmptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        rdvTable.setRowHeight(28);
        rdvTable.setFillsViewportHeight(true);
        rdvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rdvTable.getTableHeader().setReorderingAllowed(false);
        rdvTable.setFont(DentalTheme.textFont(12));
        rdvTable.getTableHeader().setFont(DentalTheme.textBold(12));
        rdvTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && rdvTable.getSelectedRow() >= 0) {
                    navigate.accept("rdv");
                }
            }
        });


        add(buildMain(), BorderLayout.CENTER);

        reload();
    }


    // ---------------- UI BUILD ----------------

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);

        // left brand (simple)
        JLabel brand = new JLabel("DENTAL CENTER");
        brand.setFont(DentalTheme.titleFont(18));
        brand.setForeground(DentalTheme.PRIMARY_DARK);

        // search bar
        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setOpaque(false);
        searchField.setPreferredSize(new Dimension(520, 36));
        searchField.setFont(DentalTheme.textFont(13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setText("Rechercher un patient, ...");

        JButton searchBtn = new JButton("Rechercher");
        styleHeaderButton(searchBtn);
        searchBtn.addActionListener(e -> navigate.accept("patients"));

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchBtn, BorderLayout.EAST);

        // right profile (simple)
        JPanel profile = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profile.setOpaque(false);
        JLabel role = new JLabel("Secretaire");
        role.setFont(DentalTheme.textBold(13));
        role.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel name = new JLabel("Mme Malak Achari");
        name.setFont(DentalTheme.textFont(12));
        name.setForeground(DentalTheme.MUTED_TEXT);

        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(role);
        t.add(name);

        JButton logout = new JButton("Deconnexion");
        styleHeaderButton(logout);
        logout.addActionListener(e -> navigate.accept("logout"));

        profile.add(t);
        profile.add(logout);

        header.add(brand, BorderLayout.WEST);
        header.add(searchBox, BorderLayout.CENTER);
        header.add(profile, BorderLayout.EAST);

        return header;
    }

    private JComponent buildMain() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        // Revue du Jour
        root.add(buildSectionTitle("Revue du Jour"));
        root.add(Box.createVerticalStrut(8));
        root.add(buildRevueDuJour());
        root.add(Box.createVerticalStrut(14));
        root.add(buildRdvDuJourSection());
        root.add(Box.createVerticalStrut(14));

        // File d’attente
        root.add(buildFileAttenteSection());
        root.add(Box.createVerticalStrut(14));

        // Notifications (après file)
        root.add(notificationsScroll);
        root.add(Box.createVerticalStrut(14));

        // Activités récentes (deux colonnes)
        root.add(buildActivitiesSection());

        return root;
    }

    private JComponent buildRevueDuJour() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(12, 12));

        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 12));
        cards.setOpaque(false);

        cards.add(statCard("Nb patients", vPatients, ""));
        cards.add(statCard("Recette du jour", vRecette, ""));
        cards.add(statCard("RDV du jour", vRdv, ""));
        cards.add(statCard("Patients en attente", vAttente, ""));

        DentalButton stats = new DentalButton("Voir +statistiques");
        stats.addActionListener(e -> navigate.accept("caisse"));
        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(stats);
        cards.add(btnWrap);

        container.add(cards, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildRdvDuJourSection() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("RDV du jour");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        rdvCardPanel.setOpaque(false);
        rdvCardPanel.removeAll();

        JScrollPane sp = new JScrollPane(rdvTable);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel emptyWrap = new JPanel(new GridBagLayout());
        emptyWrap.setOpaque(false);
        emptyWrap.add(rdvEmptyLabel);

        rdvCardPanel.add(sp, "TABLE");
        rdvCardPanel.add(emptyWrap, "EMPTY");

        container.add(title, BorderLayout.NORTH);
        container.add(rdvCardPanel, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildFileAttenteSection() {
        JPanel container = cardContainer();
        container.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("FILE D'ATTENTE");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        DentalButton addPatient = new DentalButton("+ Nouveau Patient");
        addPatient.addActionListener(e -> openAddFileAttenteDialog());

        top.add(title, BorderLayout.WEST);
        top.add(addPatient, BorderLayout.EAST);

        fileAttentePanel.setOpaque(false);
        JScrollPane sp = new JScrollPane(fileAttentePanel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(10, 170));

        container.add(top, BorderLayout.NORTH);
        container.add(sp, BorderLayout.CENTER);

        return container;
    }

    private JComponent buildActivitiesSection() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 14));
        row.setOpaque(false);

        // Left big
        JPanel leftWrap = cardContainer();
        leftWrap.setLayout(new BorderLayout(10, 10));
        JLabel t1 = new JLabel("Activités Récentes");
        t1.setFont(DentalTheme.titleFont(18));
        t1.setForeground(DentalTheme.PRIMARY_DARK);

        activitiesLeft.setOpaque(false);
        activitiesLeft.setLayout(new BoxLayout(activitiesLeft, BoxLayout.Y_AXIS));

        JScrollPane sp1 = new JScrollPane(activitiesLeft);
        sp1.setBorder(BorderFactory.createEmptyBorder());
        sp1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp1.getVerticalScrollBar().setUnitIncrement(16);
        sp1.setPreferredSize(new Dimension(10, 220));

        DentalButton more = new DentalButton("Voir +Activites");
        more.addActionListener(e -> navigate.accept("rdv"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(more);

        leftWrap.add(t1, BorderLayout.NORTH);
        leftWrap.add(sp1, BorderLayout.CENTER);
        leftWrap.add(bottom, BorderLayout.SOUTH);

        // Right compact
        JPanel rightWrap = cardContainer();
        rightWrap.setLayout(new BorderLayout(10, 10));
        JLabel t2 = new JLabel("Activités Récentes");
        t2.setFont(DentalTheme.titleFont(18));
        t2.setForeground(DentalTheme.PRIMARY_DARK);

        activitiesRight.setOpaque(false);
        activitiesRight.setLayout(new BoxLayout(activitiesRight, BoxLayout.Y_AXIS));

        JScrollPane sp2 = new JScrollPane(activitiesRight);
        sp2.setBorder(BorderFactory.createEmptyBorder());
        sp2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp2.getVerticalScrollBar().setUnitIncrement(16);
        sp2.setPreferredSize(new Dimension(10, 220));

        rightWrap.add(t2, BorderLayout.NORTH);
        rightWrap.add(sp2, BorderLayout.CENTER);

        row.add(leftWrap);
        row.add(rightWrap);
        return row;
    }

    private JComponent buildSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(DentalTheme.titleFont(22));
        l.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(l, BorderLayout.WEST);
        return wrap;
    }

    // ---------------- DATA LOAD ----------------

    private void reload() {
        try {
            DashboardDTO dash = controller.getDashboardDTO(userId);
            SecretaireDashboardResponseDTO dto = dash.getSecretaire();
            if (dto == null) return;

            // Stats
            vPatients.setText(String.valueOf(nvl(dto.getNbPatients())));
            vRecette.setText(nvl(dto.getRecetteDuJour()) + " DH");
            vRdv.setText(String.valueOf(nvl(dto.getNbRdvDuJour())));
            vAttente.setText(String.valueOf(nvl(dto.getNbEnAttente())));

            // File d'attente
            fileAttentePanel.removeAll();
            if (dto.getFileAttente() != null) {
                for (ListeAttenteDto p : dto.getFileAttente()) {
                    fileAttentePanel.add(buildPatientCard(p));
                }
            }
            fileAttentePanel.revalidate();
            fileAttentePanel.repaint();

            // Notifications (après file)
            notificationsPanel.removeAll();
            List<NotificationDTO> notif = dto.getNotifications();
            if (notif != null && !notif.isEmpty()) {
                for (NotificationDTO n : notif) {
                    notificationsPanel.add(buildNotificationCard(n));
                    notificationsPanel.add(Box.createVerticalStrut(8));
                }
            } else {
                JLabel empty = new JLabel("Aucune notification");
                empty.setForeground(DentalTheme.MUTED_TEXT);
                notificationsPanel.add(empty);
            }
            Integer unread = nvl(dto.getNbNotificationsNonLues());
            notifBorder.setTitle("NOTIFICATIONS" + (unread > 0 ? " (" + unread + " non lues)" : ""));
            notificationsScroll.repaint();
            notificationsPanel.revalidate();
            notificationsPanel.repaint();

            // Activités récentes (bas) = notifications
            activitiesLeft.removeAll();
            activitiesRight.removeAll();
            if (notif != null && !notif.isEmpty()) {
                // left: jusqu'à 6
                for (int i = 0; i < Math.min(6, notif.size()); i++) {
                    activitiesLeft.add(buildActivityRow(notif.get(i), true));
                    activitiesLeft.add(Box.createVerticalStrut(8));
                }
                // right: jusqu'à 4
                for (int i = 0; i < Math.min(4, notif.size()); i++) {
                    activitiesRight.add(buildActivityRow(notif.get(i), false));
                    activitiesRight.add(Box.createVerticalStrut(8));
                }
            } else {
                JLabel e1 = new JLabel("Aucune activité récente");
                e1.setForeground(DentalTheme.MUTED_TEXT);
                activitiesLeft.add(e1);

                JLabel e2 = new JLabel("Aucune activité récente");
                e2.setForeground(DentalTheme.MUTED_TEXT);
                activitiesRight.add(e2);
            }
            activitiesLeft.revalidate();
            activitiesLeft.repaint();
            activitiesRight.revalidate();
            activitiesRight.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------------- COMPONENTS ----------------

    private JPanel cardContainer() {
        JPanel c = new JPanel();
        c.setBackground(DentalTheme.CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        c.setOpaque(true);
        return c;
    }

    private JComponent statCard(String title, JLabel value, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setOpaque(true);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel ic = null;
        if (icon != null && !icon.isBlank()) {
            ic = new JLabel(icon);
            ic.setFont(ic.getFont().deriveFont(22f));
        }

        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.textFont(12));
        t.setForeground(DentalTheme.MUTED_TEXT);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(t);
        center.add(Box.createVerticalStrut(6));
        center.add(value);

        if (ic != null) {
            card.add(ic, BorderLayout.WEST);
        }
        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildPatientCard(ListeAttenteDto p) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(190, 120));
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String label = firstNonBlank(
                safe(p.getPatientNom()),
                safe(p.getNom()),
                "Patient"
        );

        JLabel name = new JLabel(label);
        name.setFont(DentalTheme.textBold(14));
        name.setForeground(DentalTheme.PRIMARY_DARK);

        DentalButton dossier = new DentalButton("Dossier");
        dossier.addActionListener(e -> navigate.accept("dossiers"));

        card.add(name, BorderLayout.CENTER);
        card.add(dossier, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildNotificationCard(NotificationDTO n) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel ic = new JLabel(iconFor(n.getSource()));
        ic.setFont(ic.getFont().deriveFont(18f));
        ic.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(firstNonBlank(safe(n.getTitre()), "Notification"));
        title.setFont(DentalTheme.textBold(14));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel msg = new JLabel("<html><div style='width:520px;'>" + escapeHtml(safe(n.getMessage())) + "</div></html>");
        msg.setForeground(DentalTheme.TEXT);

        center.add(title);
        center.add(Box.createVerticalStrut(4));
        center.add(msg);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel time = new JLabel(timeAgo(n.getDate()));
        time.setForeground(DentalTheme.MUTED_TEXT);

        JLabel status = new JLabel(n.isLue() ? "Lue" : "Non lue");
        status.setForeground(n.isLue() ? DentalTheme.MUTED_TEXT : DentalTheme.PRIMARY_DARK);
        status.setFont(DentalTheme.textBold(12));

        right.add(time);
        right.add(Box.createVerticalStrut(6));
        right.add(status);

        card.add(ic, BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JComponent buildActivityRow(NotificationDTO n, boolean big) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel dot = new JLabel(big ? "✅" : "📌");
        dot.setFont(dot.getFont().deriveFont(16f));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        String line = firstNonBlank(safe(n.getTitre()), safe(n.getMessage()), "Activité");
        JLabel txt = new JLabel("<html><div style='width:" + (big ? "520" : "360") + "px;'>" + escapeHtml(line) + "</div></html>");
        txt.setForeground(DentalTheme.TEXT);
        txt.setFont(DentalTheme.textFont(big ? 13 : 12));

        JLabel time = new JLabel(timeAgo(n.getDate()));
        time.setForeground(DentalTheme.MUTED_TEXT);
        time.setFont(DentalTheme.textFont(11));

        center.add(txt);
        center.add(Box.createVerticalStrut(2));
        center.add(time);

        row.add(dot, BorderLayout.WEST);
        row.add(center, BorderLayout.CENTER);
        return row;
    }

    private static JLabel valueLabel(String s) {
        JLabel l = new JLabel(s);
        l.setFont(DentalTheme.titleFont(18));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void styleHeaderButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        b.setBackground(DentalTheme.CARD);
    }

    private static <T> T getBeanOrNull(Class<T> type) {
        try {
            return ApplicationContext.getBean(type);
        } catch (Exception e) {
            return null;
        }
    }

    // ---------------- HELPERS ----------------

    private String iconFor(String src) {
        if (src == null) return "🔔";
        switch (src.toUpperCase()) {
            case "AGENDA": return "📅";
            case "CAISSE": return "💰";
            case "DOSSIER": return "📁";
            case "SYSTEM": return "⚙️";
            default: return "🔔";
        }
    }

    private String timeAgo(LocalDateTime dt) {
        if (dt == null) return "";
        Duration d = Duration.between(dt, LocalDateTime.now());
        long minutes = Math.max(0, d.toMinutes());
        if (minutes < 60) return "il y a " + minutes + " min";
        long hours = minutes / 60;
        if (hours < 24) return "il y a " + hours + " h";
        long days = hours / 24;
        return "il y a " + days + " j";
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }

    private static int nvl(Integer v) { return (v != null) ? v : 0; }
    private static String nvl(Object v) { return (v != null) ? String.valueOf(v) : "0"; }
}
