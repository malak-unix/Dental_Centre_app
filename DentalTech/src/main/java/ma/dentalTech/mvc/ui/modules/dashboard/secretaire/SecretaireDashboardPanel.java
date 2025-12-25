package ma.dentalTech.mvc.ui.modules.dashboard.secretaire;

import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class SecretaireDashboardPanel extends JPanel {

    public SecretaireDashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Revue du Jour");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main, BorderLayout.CENTER);

        // KPIs
        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);
        kpis.add(kpi("1286", "Nb patients"));
        kpis.add(kpi("1200 DH", "Recette du jour"));
        kpis.add(kpi("32", "RDV du jour"));
        kpis.add(kpi("8", "Patients en attente"));
        kpis.add(actionCard("Voir +statistiques"));
        main.add(kpis);

        main.add(Box.createVerticalStrut(18));

        // File d’attente (scroll horizontal)
        CardPanel waitCard = new CardPanel();
        waitCard.setLayout(new BorderLayout(10, 10));
        JLabel wt = new JLabel("File d’Attente");
        wt.setFont(DentalTheme.H2);
        waitCard.add(wt, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        row.setOpaque(false);
        for (int i = 0; i < 7; i++) {
            row.add(patientChip("Patient " + (i + 1)));
        }

        JScrollPane sp = new JScrollPane(row);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        waitCard.add(sp, BorderLayout.CENTER);

        main.add(waitCard);

        main.add(Box.createVerticalStrut(18));

        // Activités + Notifications
        JPanel bottom = new JPanel(new GridLayout(1, 2, 18, 18));
        bottom.setOpaque(false);
        bottom.add(activitiesCard("Activités Récentes"));
        bottom.add(notifCard("Notifications / Alertes"));
        main.add(bottom);
    }

    private CardPanel kpi(String value, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        JLabel v = new JLabel(value);
        v.setFont(DentalTheme.H2);
        v.setForeground(DentalTheme.TEXT);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.BASE);
        l.setForeground(DentalTheme.MUTED);

        c.add(v);
        c.add(Box.createVerticalStrut(6));
        c.add(l);
        return c;
    }

    private CardPanel actionCard(String text) {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout());
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        c.add(b, BorderLayout.CENTER);
        return c;
    }

    private JPanel patientChip(String name) {
        CardPanel chip = new CardPanel();
        chip.setPreferredSize(new Dimension(170, 70));
        chip.setLayout(new BorderLayout(8, 0));
        JLabel n = new JLabel(name);
        n.setFont(DentalTheme.BASE_BOLD);
        chip.add(n, BorderLayout.CENTER);

        JButton dossier = new JButton("Dossier");
        dossier.setFocusPainted(false);
        chip.add(dossier, BorderLayout.EAST);
        return chip;
    }

    private CardPanel activitiesCard(String title) {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));
        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("RDV confirmé avec Samin Paradness  •  il y a 1h");
        model.addElement("RDV confirmé avec Daimon Ativa     •  il y a 1h");
        model.addElement("Dossier mis à jour Driss Gafar      •  il y a 3h");
        model.addElement("Cas ajouté pour Bat Skemp           •  il y a 5h");

        JList<String> list = new JList<>(model);
        list.setFont(DentalTheme.BASE);
        list.setBorder(null);
        c.add(new JScrollPane(list), BorderLayout.CENTER);

        return c;
    }

    private CardPanel notifCard(String title) {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(DentalTheme.H2);

        JLabel badge = new JLabel("  3  ");
        badge.setOpaque(true);
        badge.setBackground(new Color(0x1F4C5B));
        badge.setForeground(Color.WHITE);

        head.add(t, BorderLayout.WEST);
        head.add(badge, BorderLayout.EAST);

        c.add(head, BorderLayout.NORTH);

        JTextArea area = new JTextArea(
                "• Alerte : Factures impayées (2)\n" +
                        "• Notification : RDV déplacé\n" +
                        "• Alerte : Patient en attente > 20 min\n"
        );
        area.setFont(DentalTheme.BASE);
        area.setOpaque(false);
        area.setEditable(false);
        c.add(area, BorderLayout.CENTER);

        return c;
    }
}
