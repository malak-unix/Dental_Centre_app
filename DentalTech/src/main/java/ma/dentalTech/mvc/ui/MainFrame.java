package ma.dentalTech.mvc.ui;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.*;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaHomePanel;
import ma.dentalTech.mvc.ui.modules.agenda.ListeAttentePagePanel;
import ma.dentalTech.mvc.ui.modules.agenda.RdvPagePanel;
import ma.dentalTech.mvc.ui.modules.caisse.CaisseMainPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final Map<String, JComponent> pages = new LinkedHashMap<>();

    private PatientView patientView;

    private final LibelleRole role;
    private final Long userId;
    private final String fullName;

    private final AppShellPanel shell = new AppShellPanel();
    private SidebarCommonPanel sidebar;
    private final AppHeaderPanel header = new AppHeaderPanel();

    public MainFrame() {
        this(LibelleRole.SECRETAIRE, 1L, "Utilisateur");
    }

    public MainFrame(LibelleRole role, Long userId, String fullName) {
        super("DentalTech - Dental Center");

        this.role = (role != null) ? role : LibelleRole.SECRETAIRE;
        this.userId = (userId != null) ? userId : 1L;

        String name = (fullName == null) ? "" : fullName.trim();
        this.fullName = name.isBlank() ? "Utilisateur" : name;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setLocationRelativeTo(null);

        setContentPane(shell);

        // Header (haut)
        header.setUser(this.fullName, RoleMenuConfig.roleLabel(this.role));
        header.logoutButton().addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Déconnexion (à brancher)")
        );
        shell.header().add(header, BorderLayout.CENTER);

        // Sidebar (gauche) + navigation
        sidebar = new SidebarCommonPanel(this.role, this.fullName, this::showPage);
        shell.sidebar().add(sidebar, BorderLayout.CENTER);

        // Zone centrale
        cards.setOpaque(false);
        shell.setContent(cards);

        // Pages
        buildPages();

        // Page par défaut
        showPage("dashboard");
    }

    private void buildPages() {
        // Dashboard selon rôle (réel)
        addPage("dashboard", buildDashboardByRole());

        // Secrétaire (réel)
        addPage("patients", buildPatientPage());
        addPage("rdv", new RdvPagePanel());
        addPage("caisse", new CaisseMainPanel(role, userId));
        addPage("agenda_med", buildAgendaPage());

        // Liste d’attente (si vous voulez la brancher plus tard dans le menu)
        addPage("liste_attente", new ListeAttentePagePanel());

        // Placeholders (modules collègues / à brancher plus tard)
        addPage("stock", buildPlaceholder("Stock (module en cours)"));

        // Médecin (placeholders)
        addPage("consultations", buildPlaceholder("Mes consultations (à brancher)"));
        addPage("ordonnances", buildPlaceholder("Ordonnances (à brancher)"));
        addPage("certificats", buildPlaceholder("Certificats (à brancher)"));
        addPage("situation_fin", buildPlaceholder("Situation financière (à brancher)"));

        // Admin (placeholders)
        addPage("utilisateurs", buildPlaceholder("Utilisateurs (à brancher)"));
        addPage("referentiels", buildPlaceholder("Référentiels (à brancher)"));
        addPage("sauvegardes", buildPlaceholder("Sauvegardes (à brancher)"));
        addPage("roles", buildPlaceholder("Rôles (à brancher)"));
    }

    private JComponent buildDashboardByRole() {
        return switch (role) {
            case SECRETAIRE -> new SecretaireDashboardPanel();
            case MEDECIN -> new MedecinDashboardPanel();
            case ADMIN -> new AdminDashboardPanel();
        };
    }

    private void addPage(String key, JComponent page) {
        pages.put(key, page);
        cards.add(page, key);
    }

    private void showPage(String key) {
        if (!pages.containsKey(key)) {
            JOptionPane.showMessageDialog(this, "Page non disponible: " + key);
            return;
        }

        cardLayout.show(cards, key);

        if (sidebar != null) {
            sidebar.setActive(key);
        }

        if ("patients".equals(key) && patientView != null) {
            patientView.refresh();
        }
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
}
