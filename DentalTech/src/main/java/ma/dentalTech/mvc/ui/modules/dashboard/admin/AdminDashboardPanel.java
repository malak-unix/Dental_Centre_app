package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {

    public AdminDashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Statistiques Globales");
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
        kpis.add(kpi("24", "Utilisateurs"));
        kpis.add(kpi("4", "Administrateurs"));
        kpis.add(kpi("1200 DH", "Recette du jour"));
        kpis.add(kpi("15", "Actes réalisés"));
        kpis.add(actionCard("Voir +statistiques"));
        main.add(kpis);

        main.add(Box.createVerticalStrut(18));

        // Table + carte référentiel
        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 18));
        grid.setOpaque(false);

        grid.add(usersTableCard());
        grid.add(referentielCard());

        main.add(grid);
    }

    private CardPanel kpi(String value, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        JLabel v = new JLabel(value);
        v.setFont(DentalTheme.H2);
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

    private CardPanel usersTableCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Utilisateurs");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        String[] cols = {"Rôle", "Nom", "Statut", "Dernière activité"};
        Object[][] data = {
                {"Admin", "Adam Idrissi", "Actif", "il y a 2h"},
                {"Secrétaire", "Mimi Malak", "Actif", "il y a 1h"},
                {"Médecin", "Mimne Aicha", "À venir", "il y a 20 min"},
                {"Secrétaire", "Etharcin Loubia", "À venir", "il y a 1 min"}
        };

        JTable table = new JTable(new DefaultTableModel(data, cols));
        table.setRowHeight(28);
        c.add(new JScrollPane(table), BorderLayout.CENTER);

        return c;
    }

    private CardPanel referentielCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Données Référentielles");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        JTextArea area = new JTextArea(
                "Driss Gafar\n\n" +
                        "• Dents : sain / en traitement / urgent\n" +
                        "• Actions : +Dossier, Supprimer\n"
        );
        area.setOpaque(false);
        area.setEditable(false);
        area.setFont(DentalTheme.BASE);

        c.add(area, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(new JButton("+ Dossier"));
        actions.add(new JButton("Supprimer"));
        c.add(actions, BorderLayout.SOUTH);

        return c;
    }
}
