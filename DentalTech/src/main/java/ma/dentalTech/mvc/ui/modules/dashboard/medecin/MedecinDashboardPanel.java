package ma.dentalTech.mvc.ui.modules.dashboard.medecin;

import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedecinDashboardPanel extends JPanel {

    public MedecinDashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 18));

        JLabel title = new JLabel("Dashboard");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(1, 2, 18, 18));
        main.setOpaque(false);
        add(main, BorderLayout.CENTER);

        main.add(rdvCard());
        main.add(currentPatientCard());
    }

    private CardPanel rdvCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Rendez-vous du Jour");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        String[] cols = {"Heure", "Patient", "Motif", "Statut"};
        Object[][] data = {
                {"09:00", "Sumit Estève", "Blanchiment dentaire", "Arrivé"},
                {"10:00", "Eve Leptot Lamiss", "Douleur aiguë", "À venir"},
                {"10:20", "Sarm Enlibais", "Blanchiment dentaire", "À venir"},
                {"11:20", "Troqué Carifice", "Douleur aiguë", "Arrivé"},
                {"12:00", "Bayé Dorachat", "Contrôle", "Réalisé"}
        };

        JTable table = new JTable(new DefaultTableModel(data, cols));
        table.setRowHeight(28);

        c.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        footer.setOpaque(false);
        footer.add(new JLabel("Aujourd’hui : 15 RDV   |   Actes réalisés : 15   |   Recettes : 1,200 DH"));
        c.add(footer, BorderLayout.SOUTH);

        return c;
    }

    private CardPanel currentPatientCard() {
        CardPanel c = new CardPanel();
        c.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Client En cours");
        t.setFont(DentalTheme.H2);
        c.add(t, BorderLayout.NORTH);

        JTextArea info = new JTextArea("Driss Gafar\n\nActions :\n• Dossier\n• Consultation\n• Radio\n• Ordonnance\n");
        info.setOpaque(false);
        info.setEditable(false);
        info.setFont(DentalTheme.BASE);
        c.add(info, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);
        actions.add(new JButton("+ Dossier"));
        actions.add(new JButton("+ Consultation"));
        actions.add(new JButton("+ Radio"));
        actions.add(new JButton("+ Ordonnance"));
        c.add(actions, BorderLayout.SOUTH);

        return c;
    }
}
