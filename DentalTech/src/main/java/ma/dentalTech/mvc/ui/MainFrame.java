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
import ma.dentalTech.mvc.ui.modules.patient.PatientView;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;

import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeListUI;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier.DossierMedicalListUI;

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
        header.logoutButton().addActionListener(e -> doLogout());
        shell.header().add(header, BorderLayout.CENTER);

        // Sidebar (gauche)
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

        dispose();

        SwingUtilities.invokeLater(() -> {
            ma.dentalTech.mvc.ui.modules.auth.LoginFrame lf =
                    new ma.dentalTech.mvc.ui.modules.auth.LoginFrame();
            lf.setVisible(true);
        });
    }

    private void buildPages() {

        // Dashboard selon rôle
        addPage("dashboard", buildDashboardByRole());

        // Secrétaire
        addPage("patients", buildPatientPage());
        addPage("rdv", new RdvPagePanel());
        addPage("dossiers", buildDossiersPage());
        addPage("caisse", new CaisseMainPanel(role, userId));
        addPage("agenda_med", buildAgendaPage());

        addPage("liste_attente", new ListeAttentePagePanel());

        // Médecin
        addPage("consultations", buildConsultationsPage());
        addPage("ordonnances", buildOrdonnancesPage());
        addPage("certificats", buildCertificatsPage());
        addPage("situation_fin", buildSituationFinPage());
        addPage("actes", buildActesPage());


        // Admin
        addPage("utilisateurs", buildUsersPage());
        addPage("referentiels", new ma.dentalTech.mvc.ui.modules.admin.ReferentielsPanel());
        addPage("medicaments", buildMedicamentsPage());
        addPage("antecedents", buildAntecedentsAdminPage());
        addPage("sauvegardes", new ma.dentalTech.mvc.ui.modules.admin.SauvegardesPanel());
        addPage("roles", new ma.dentalTech.mvc.ui.modules.admin.RolesPanel());
    }

    private JComponent buildMedicamentsPage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.MedicamentController.class);
        if (controller == null) return buildPlaceholder("❌ MedicamentController introuvable (ApplicationContext)");
        return new ma.dentalTech.mvc.ui.modules.admin.MedicamentsPanel(controller);
    }


    private JComponent buildAntecedentsAdminPage() {
        var ctrl = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentAdminController.class);
        if (ctrl == null) return buildPlaceholder("❌ AntecedentAdminController introuvable");
        return new ma.dentalTech.mvc.ui.modules.admin.AntecedentsAdminPanel(ctrl);
    }


    private JComponent buildUsersPage() {
        var controller = (ma.dentalTech.mvc.controllers.modules.users.api.UserManagementController)
                ApplicationContext.getBean("userManagementController");

        if (controller == null) {
            return buildPlaceholder("❌ UserManagementController introuvable");
        }
        return new ma.dentalTech.mvc.ui.modules.users.UserManagementPanel(controller);
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

    private JComponent buildConsultationsPage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController.class);
        if (controller == null) return buildPlaceholder("❌ ConsultationController introuvable");
        return new ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation.ConsultationPagePanel(controller, userId, fullName);
    }

    private JComponent buildOrdonnancesPage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController.class);
        if (controller == null) return buildPlaceholder("❌ OrdonnanceController introuvable");
        return new ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance.OrdonnanceListUI(controller, userId);
    }

    private JComponent buildCertificatsPage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController.class);
        if (controller == null) return buildPlaceholder("❌ CertificatController introuvable");
        return new ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat.CertificatListUI(controller, userId);
    }

    /**
     * ✅ FIX IMPORTANT :
     * Le constructeur de SituationFinanciereListUI dans ton projet est :
     * SituationFinanciereListUI(SituationFinanciereController controller, Long medecinId, String username)
     */
    private JComponent buildSituationFinPage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController.class);
        if (controller == null) {
            return buildPlaceholder("❌ SituationFinanciereController introuvable (ApplicationContext)");
        }
        Long medecinId = userId;       // médecin connecté
        String username = fullName;    // nom affiché
        return new ma.dentalTech.mvc.ui.modules.dossierMedicale.situationFinanciere.SituationFinanciereListUI(controller, medecinId, username);
    }

    private JComponent buildListeAttentePage() {
        var controller = ApplicationContext.getBean(ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController.class);
        if (controller == null) {
            // fallback propre
            return buildPlaceholder("❌ ListeAttenteController introuvable (ApplicationContext)");
        }
        return new ListeAttentePagePanel();
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
        if (controller == null) return buildPlaceholder("❌ DossierMedicalController introuvable");

        // Pour médecin : filtre par medecinId = userId
        // Pour secrétaire/admin : tous dossiers (medecinId = null)
        Long medecinId = (role == LibelleRole.MEDECIN) ? userId : null;

        return new DossierMedicalListUI(controller, medecinId, fullName);
    }
}
