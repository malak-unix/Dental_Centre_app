package ma.dentalTech.mvc.ui;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.*;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaHomePanel;
import ma.dentalTech.mvc.ui.modules.agenda.ListeAttentePagePanel;
import ma.dentalTech.mvc.ui.modules.agenda.RdvPagePanel;
import ma.dentalTech.mvc.ui.modules.caisse.CaisseMainPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.DashboardMainPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.admin.AdminDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.medecin.MedecinDashboardPanel;
import ma.dentalTech.mvc.ui.modules.dashboard.secretaire.SecretaireDashboardPanel;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeListUI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier.DossierMedicalListUI;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    //jihane
    private void openUsersManagement() {
        // ✅ autoriser seulement ADMIN
        if (role != LibelleRole.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Accès refusé : ADMIN uniquement",
                    "Sécurité",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object bean = ApplicationContext.getBean("userManagementController");
        if (!(bean instanceof ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController ctrl)) {
            JOptionPane.showMessageDialog(this,
                    "userManagementController introuvable dans ApplicationContext",
                    "Wiring",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ma.dentalTech.mvc.ui.modules.users.UserManagementFrame f =
                new ma.dentalTech.mvc.ui.modules.users.UserManagementFrame(ctrl);
        f.setVisible(true);
    }

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

    private void doLogout() {
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vous déconnecter ?",
                "Déconnexion",
                JOptionPane.YES_NO_OPTION
        );

        if (ok != JOptionPane.YES_OPTION) return;

        // Fermer la fenêtre principale
        dispose();

        // Revenir à l’écran de connexion
        SwingUtilities.invokeLater(() -> {
            ma.dentalTech.mvc.ui.modules.auth.LoginFrame lf =
                    new ma.dentalTech.mvc.ui.modules.auth.LoginFrame();
            lf.setVisible(true);
        });
    }


    private void buildPages() {
        // Dashboard selon rôle (réel)
        addPage("dashboard", buildDashboardByRole());

        // Secrétaire (réel)
        addPage("patients", buildPatientPage());
        addPage("rdv", new RdvPagePanel());
        addPage("dossiers", buildDossiersPage());
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
        addPage("actes", buildActesPage());


        // Admin (placeholders)
        addPage("utilisateurs", buildPlaceholder("Utilisateurs (à brancher)"));
        addPage("referentiels", buildPlaceholder("Référentiels (à brancher)"));
        addPage("sauvegardes", buildPlaceholder("Sauvegardes (à brancher)"));
        addPage("roles", buildPlaceholder("Rôles (à brancher)"));
    }

    }
    private JComponent buildDashboardByRole() {
        var dashboardController =
                ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController.class);

        return new DashboardMainPanel(role, userId, dashboardController, this::showPage);

    }

    private JComponent buildActesPage() {
        ActeController controller = ApplicationContext.getBean(ActeController.class);
        if (controller == null) {
            return buildPlaceholder("❌ ActeController introuvable (ApplicationContext)");
        }
        return new ActeListUI(controller, fullName);
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

    private JComponent buildDossiersPage() {
        DossierMedicalController controller = ApplicationContext.getBean(DossierMedicalController.class);

        // Pour médecin : on filtre par medecinId = userId
        // Pour secrétaire/admin : on affiche tous les dossiers (medecinId = null)
        Long medecinId = (role != null && role.name().equals("MEDECIN")) ? userId : null;

        // username = ce qui est affiché comme auteur (créé par / modifié par)
        return new DossierMedicalListUI(controller, medecinId, fullName);
    }

}
