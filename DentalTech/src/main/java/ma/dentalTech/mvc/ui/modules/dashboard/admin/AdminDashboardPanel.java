package ma.dentalTech.mvc.ui.modules.dashboard.admin;

import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private JLabel vUsers, vAdmins, vRecette, vActes;

    private DefaultTableModel usersModel;

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

        JPanel kpis = new JPanel(new GridLayout(1, 5, 18, 18));
        kpis.setOpaque(false);

        vUsers = new JLabel("—");
        vAdmins = new JLabel("—");
        vRecette = new JLabel("—");
        vActes = new JLabel("—");

        kpis.add(kpi(vUsers, "Utilisateurs"));
        kpis.add(kpi(vAdmins, "Administrateurs"));
        kpis.add(kpi(vRecette, "Recette du jour"));
        kpis.add(kpi(vActes, "Actes réalisés"));
        kpis.add(actionCard("Voir +statistiques"));
        main.add(kpis);

        main.add(Box.createVerticalStrut(18));

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 18));
        grid.setOpaque(false);
        grid.add(usersTableCard());
        grid.add(referentielCard());
        main.add(grid);

        setData(null);
    }

    public void setData(AdminDashboardResponseDTO dto) {
        int nbUsers = dto != null && dto.getNbUtilisateurs() != null ? dto.getNbUtilisateurs() : 0;
        int nbAdmins = dto != null && dto.getNbAdmins() != null ? dto.getNbAdmins() : 0;
        int nbActes = dto != null && dto.getNbActesRealises() != null ? dto.getNbActesRealises() : 0;
        BigDecimal recette = dto != null ? dto.getRecetteDuJour() : null;

        vUsers.setText(String.valueOf(nbUsers));
        vAdmins.setText(String.valueOf(nbAdmins));
        vActes.setText(String.valueOf(nbActes));
        vRecette.setText(recette == null ? "0 DH" : recette.stripTrailingZeros().toPlainString() + " DH");

        // table
        usersModel.setRowCount(0);
        List<UserSummaryDTO> list = dto != null ? dto.getUtilisateurs() : null;
        if (list != null && !list.isEmpty()) {
            for (UserSummaryDTO u : list) {
                usersModel.addRow(new Object[]{
                        u.getRole() != null ? u.getRole().name() : "",
                        (u.getPrenom() != null ? u.getPrenom() : "") + " " + (u.getNom() != null ? u.getNom() : ""),
                        u.getStatut() != null ? u.getStatut() : (u.isActif() ? "Actif" : "Désactivé"),
                        human(u.getDerniereActivite())
                });
            }
        } else {
            usersModel.addRow(new Object[]{"", "Aucun utilisateur", "", ""});
        }
    }

    private CardPanel kpi(JLabel valueLabel, String label) {
        CardPanel c = new CardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        valueLabel.setFont(DentalTheme.H2);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.BASE);
        l.setForeground(DentalTheme.MUTED);

        c.add(valueLabel);
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
        usersModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(usersModel);
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

        JTextArea area = new JTextArea("À brancher selon le module référentiels.");
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

    private String human(LocalDateTime dt) {
        if (dt == null) return "";
        Duration d = Duration.between(dt, LocalDateTime.now());
        long min = d.toMinutes();
        if (min < 60) return "il y a " + min + " min";
        long h = min / 60;
        if (h < 24) return "il y a " + h + " h";
        long j = h / 24;
        return "il y a " + j + " j";
    }
}
