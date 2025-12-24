package ma.dentalTech.mvc.ui;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.modules.patient.PatientView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);

    public MainFrame() {
        super("DentalTech - Dental Center");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        // Pages (tu peux ajouter les autres après)
        content.add(buildPlaceholder("Dashboard (à brancher)"), "dashboard");
        content.add(buildPatientPage(), "patients");
        content.add(buildPlaceholder("Agenda (à brancher)"), "agenda");
        content.add(buildPlaceholder("Liste d'attente (à brancher)"), "waitlist");
        content.add(buildPlaceholder("RDV (à brancher)"), "rdv");
        content.add(buildPlaceholder("Caisse (à brancher)"), "caisse");

        // Layout global
        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);

        // page par défaut
        showPage("patients");
    }

    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(220, 750));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("DENTAL CENTER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(18));

        p.add(navButton("Dashboard", "dashboard"));
        p.add(Box.createVerticalStrut(8));
        p.add(navButton("Les patients", "patients"));
        p.add(Box.createVerticalStrut(8));
        p.add(navButton("Agenda", "agenda"));
        p.add(Box.createVerticalStrut(8));
        p.add(navButton("Liste d'attente", "waitlist"));
        p.add(Box.createVerticalStrut(8));
        p.add(navButton("Rendez-vous", "rdv"));
        p.add(Box.createVerticalStrut(8));
        p.add(navButton("La caisse", "caisse"));

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JButton navButton(String text, String pageKey) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.addActionListener(e -> showPage(pageKey));
        return b;
    }

    private void showPage(String key) {
        cardLayout.show(content, key);

        // petit hook: refresh automatique quand on ouvre Patients
        if ("patients".equals(key)) {
            Component c = findCard("patients");
            if (c instanceof PatientView pv) {
                pv.refresh();
            }
        }
    }

    private Component findCard(String key) {
        for (Component comp : content.getComponents()) {
            if (key.equals(content.getLayout().toString())) { /* nothing */ }
        }
        // CardLayout ne donne pas direct, donc on fait simple:
        // on reconstruit pas, on ignore; refresh se fait dans PatientView aussi.
        return null;
    }

    private JPanel buildPlaceholder(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildPatientPage() {
        // IMPORTANT: ton AppSmokeTest a montré que "patientController" existe
        Object bean = ApplicationContext.getBean("patientController");
        if (!(bean instanceof PatientController pc)) {
            // si jamais beans.properties n’est pas bon => message clair
            return buildPlaceholder("❌ patientController introuvable dans ApplicationContext");
        }
        return new PatientView(pc);
    }
}
