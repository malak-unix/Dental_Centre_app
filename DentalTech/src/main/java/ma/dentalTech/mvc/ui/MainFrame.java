package ma.dentalTech.mvc.ui;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.NavButton;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaHomePanel;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    private final Map<String, JComponent> pages = new LinkedHashMap<>();
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();

    private AgendaHomePanel agendaHome;
    private PatientView patientView;

    public MainFrame() {
        super("DentalTech - Dental Center");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(DentalTheme.BG2);

        JPanel sidebar = buildSidebar();
        sidebar.setBackground(DentalTheme.BG2);

        content.setBackground(DentalTheme.BG2);

        // Pages
        addPage("dashboard", buildPlaceholder("Dashboard (à brancher)"));
        addPage("patients", buildPatientPage());
        addPage("agenda", buildAgendaPage());
        addPage("caisse", buildPlaceholder("Caisse (à brancher)"));

        root.add(sidebar, BorderLayout.WEST);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        centerWrap.add(content, BorderLayout.CENTER);

        root.add(centerWrap, BorderLayout.CENTER);

        setContentPane(root);

        // page par défaut
        showPage("agenda:SEMAINE");
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

        // ✅ LOGO (resources/assets/logo.png)
        JLabel logo = new JLabel(loadIcon("/assets/logo.png", 190, 95));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(logo);
        p.add(Box.createVerticalStrut(8));

        // titre / rôle
        JLabel title = new JLabel("DENTAL CENTER");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel("Secrétaire");
        role.setFont(DentalTheme.textFont(12));
        role.setForeground(DentalTheme.MUTED);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(2));
        p.add(role);
        p.add(Box.createVerticalStrut(16));

        // bloc nav (style card)
        CardPanel navCard = new CardPanel((String) null);
        navCard.setLayout(new BoxLayout(navCard, BoxLayout.Y_AXIS));
        navCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        navCard.add(makeNav("Dashboard", "dashboard"));
        navCard.add(Box.createVerticalStrut(8));
        navCard.add(makeNav("Les patients", "patients"));
        navCard.add(Box.createVerticalStrut(8));
        navCard.add(makeNav("Rendez-vous", "agenda:RDV"));
        navCard.add(Box.createVerticalStrut(8));
        navCard.add(makeNav("Agenda", "agenda:SEMAINE"));
        navCard.add(Box.createVerticalStrut(8));
        navCard.add(makeNav("Liste d'attente", "agenda:LISTE"));
        navCard.add(Box.createVerticalStrut(8));
        navCard.add(makeNav("La caisse", "caisse"));

        p.add(navCard);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private NavButton makeNav(String text, String route) {
        NavButton b = new NavButton(text, false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addActionListener(e -> showPage(route));
        navButtons.put(route, b); // ✅ clé = route complète
        return b;
    }

    private void showPage(String route) {

        String base = route;
        String sub = null;

        if (route.contains(":")) {
            String[] parts = route.split(":", 2);
            base = parts[0];
            sub = parts[1];
        }

        cardLayout.show(content, base);

        // ✅ active le bouton exact selon la route (agenda:RDV etc)
        for (Map.Entry<String, NavButton> e : navButtons.entrySet()) {
            e.getValue().setActive(e.getKey().equals(route));
        }

        // refresh patient si nécessaire
        if ("patients".equals(base) && patientView != null) {
            patientView.refresh();
        }

        // ouvrir la sous-page agenda si besoin
        if ("agenda".equals(base) && sub != null && agendaHome != null) {
            agendaHome.open(sub);
        }
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
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

        agendaHome = new AgendaHomePanel();
        wrap.add(agendaHome, BorderLayout.CENTER);

        return wrap;
    }
}
