package ma.dentalTech.mvc.ui;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.NavButton;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaHomePanel;
import ma.dentalTech.mvc.ui.modules.caisse.CaisseMainPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.DashboardMainPanel;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    private final Map<String, JComponent> pages = new LinkedHashMap<>();
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();

    private PatientView patientView;

    private final LibelleRole role;
    private final Long currentUserId;

    /** ✅ Mode demo (tant que LoginPanel n’est pas prêt) */
    public MainFrame() {
        this(LibelleRole.SECRETAIRE, 1L);
    }

    /** ✅ Mode normal : Login → role/user → MainFrame */
    public MainFrame(LibelleRole role, Long currentUserId) {
        super("DentalTech - Dental Center");

        this.role = (role == null ? LibelleRole.SECRETAIRE : role);
        this.currentUserId = (currentUserId == null ? 1L : currentUserId);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DentalTheme.BG2);

        JPanel sidebar = buildSidebar();
        sidebar.setBackground(DentalTheme.BG2);

        content.setBackground(DentalTheme.BG2);

        // =========================
        // Pages (selon rôle)
        // =========================
        addPage("dashboard", new DashboardMainPanel(this.role, this.currentUserId));

        if (this.role == LibelleRole.SECRETAIRE) {
            addPage("patients", buildPatientPage());
            addPage("rdv", buildPlaceholder("Rendez-vous (à brancher)"));
            addPage("agenda", buildAgendaPage());
            addPage("waitlist", buildPlaceholder("Liste d'attente (à brancher)"));
            addPage("caisse", new CaisseMainPanel(this.role, this.currentUserId));
            addPage("stock", buildPlaceholder("Stock (à brancher)"));
        }

        if (this.role == LibelleRole.MEDECIN) {
            addPage("myPatients", buildPlaceholder("Mes patients (à brancher)"));
            addPage("consultations", buildPlaceholder("Mes consultations (à brancher)"));
            addPage("ordonnances", buildPlaceholder("Ordonnances (à brancher)"));
            addPage("dossiers", buildPlaceholder("Les dossiers (à brancher)"));
            addPage("certificats", buildPlaceholder("Certificats (à brancher)"));
            addPage("situation", buildPlaceholder("Situation financière (à brancher)"));
        }

        if (this.role == LibelleRole.ADMIN) {
            addPage("users", buildPlaceholder("Utilisateurs (à brancher)"));
            addPage("referentiels", buildPlaceholder("Référentiels (à brancher)"));
            addPage("sauvegardes", buildPlaceholder("Sauvegardes & sécurité (à brancher)"));
        }

        root.add(sidebar, BorderLayout.WEST);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        centerWrap.add(content, BorderLayout.CENTER);

        root.add(centerWrap, BorderLayout.CENTER);
        setContentPane(root);

        // page par défaut
        showPage("dashboard");
    }

    private void addPage(String key, JComponent page) {
        pages.put(key, page);
        content.add(page, key);
    }

    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(240, 780));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        p.setOpaque(false);

        JLabel title = new JLabel("DENTAL CENTER");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(roleToLabel(role));
        roleLabel.setFont(DentalTheme.textFont(12));
        roleLabel.setForeground(DentalTheme.MUTED);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(2));
        p.add(roleLabel);
        p.add(Box.createVerticalStrut(16));

        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Commun
        navCard.add(makeNav("Dashboard", "dashboard"));
        navCard.add(Box.createVerticalStrut(8));

        // Secrétaire
        if (role == LibelleRole.SECRETAIRE) {
            navCard.add(makeNav("Les patients", "patients"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Rendez-vous", "rdv"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Agenda", "agenda"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Liste d'attente", "waitlist"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("La caisse", "caisse"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Stock", "stock"));
        }

        // Médecin
        if (role == LibelleRole.MEDECIN) {
            navCard.add(makeNav("Mes patients", "myPatients"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Mes consultations", "consultations"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Ordonnances", "ordonnances"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Les dossiers", "dossiers"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Certificats", "certificats"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Situation financière", "situation"));
        }

        // Admin
        if (role == LibelleRole.ADMIN) {
            navCard.add(makeNav("Utilisateurs", "users"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Référentiels", "referentiels"));
            navCard.add(Box.createVerticalStrut(8));
            navCard.add(makeNav("Sauvegardes", "sauvegardes"));
        }

        p.add(navCard);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private static String roleToLabel(LibelleRole role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case MEDECIN -> "Médecin";
            default -> "Secrétaire";
        };
    }

    private NavButton makeNav(String text, String pageKey) {
        NavButton b = new NavButton(text, false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addActionListener(e -> showPage(pageKey));
        navButtons.put(pageKey, b);
        return b;
    }

    private void showPage(String key) {
        cardLayout.show(content, key);

        for (Map.Entry<String, NavButton> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(key));
        }

        if ("patients".equals(key) && patientView != null) {
            patientView.refresh();
        }
    }

    private JComponent buildPlaceholder(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(DentalTheme.titleFont(18));
        l.setForeground(DentalTheme.TEXT2);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());
        card.add(l, BorderLayout.CENTER);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildPatientPage() {
        Object bean = ApplicationContext.getBean("patientController");
        if (!(bean instanceof PatientController pc)) {
            return buildPlaceholder("❌ patientController introuvable dans ApplicationContext");
        }
        patientView = new PatientView(pc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(patientView, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildAgendaPage() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(new AgendaHomePanel(), BorderLayout.CENTER);
        return wrap;
    }
}
