package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.ui.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class DashboardView extends JPanel {

    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    public DashboardView(DashboardDTO dto) {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(dto), BorderLayout.CENTER);
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(DentalTheme.BEIGE); // beige maquette
        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("DENTAL CENTER");
        logo.setFont(DentalTheme.titleFont(16));
        logo.setForeground(DentalTheme.PRIMARY_DARK);

        side.add(logo);
        side.add(Box.createVerticalStrut(14));

        side.add(new NavButton("Dashboard", true));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Les patients", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Rendez-vous", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("La caisse", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Stock", false));
        side.add(Box.createVerticalStrut(8));
        side.add(new NavButton("Agenda", false));

        side.add(Box.createVerticalGlue());
        return side;
    }

    private JComponent buildMain(DashboardDTO dto) {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);

        main.add(buildTopbar(dto), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);

        DashboardFeaturesDTO f = dto.getFeatures();

        if (f != null && f.isVoirRdvEtFileAttente()) grid.add(cardRdv(dto));
        if (f != null && f.isVoirRdvEtFileAttente()) grid.add(cardFile(dto));
        if (f != null && f.isVoirCaisse()) grid.add(cardCaisse(dto.getCaisseDuJour()));

        if (f != null && f.isVoirNotifications()) grid.add(cardNotif(dto));
        if (f != null && f.isVoirConsultationsEtActes()) grid.add(cardMedecin(dto));
        if (f != null && f.isVoirStatsAdmin()) grid.add(cardAdmin(dto));

        main.add(grid, BorderLayout.CENTER);

        return main;
    }

    private JComponent buildTopbar(DashboardDTO dto) {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JLabel title = new JLabel("Dashboard " + dto.getRole());
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JTextField search = new JTextField("Rechercher un client ...");
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JButton nouveau = new DentalButton("+ Nouveau");

        top.add(title, BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);
        top.add(nouveau, BorderLayout.EAST);
        return top;
    }

    private JComponent cardRdv(DashboardDTO d) {
        CardPanel c = new CardPanel("LISTE DE RENDEZ-VOUS");
        JPanel body = new JPanel(new GridLayout(1, 3, 10, 10));
        body.setOpaque(false);

        body.add(emptyMiniCard());
        body.add(emptyMiniCard());
        body.add(emptyMiniCard());

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardFile(DashboardDTO d) {
        CardPanel c = new CardPanel("FILE D’ATTENTE");
        JPanel body = new JPanel(new GridLayout(1, 3, 10, 10));
        body.setOpaque(false);

        body.add(emptyMiniCard());
        body.add(emptyMiniCard());
        body.add(emptyMiniCard());

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardCaisse(CaisseDashboardDTO caisse) {
        CardPanel c = new CardPanel("CAISSE (AUJOURD'HUI)");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        double revenus = caisse == null ? 0 : n(caisse.getTotalRevenus());
        double charges = caisse == null ? 0 : n(caisse.getTotalCharges());
        double solde = revenus - charges; // ✅ pas besoin de getSolde()

        body.add(line("Total factures", caisse == null ? "0" : DF.format(n(caisse.getTotalFactures()))));
        body.add(line("Total réglé", caisse == null ? "0" : DF.format(n(caisse.getTotalRegle()))));
        body.add(line("Total non réglé", caisse == null ? "0" : DF.format(n(caisse.getTotalNonRegle()))));
        body.add(Box.createVerticalStrut(8));
        body.add(line("Revenus", DF.format(revenus)));
        body.add(line("Charges", DF.format(charges)));
        body.add(line("Solde", DF.format(solde)));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardNotif(DashboardDTO d) {
        CardPanel c = new CardPanel("ALERTES / NOTIFICATIONS");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("Non lues", String.valueOf(i(d.getNombreNotificationsNonLues()))));
        body.add(line("Alertes importantes", String.valueOf(i(d.getNombreAlertesImportantes()))));
        body.add(line("Système", String.valueOf(i(d.getNombreNotificationsSysteme()))));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardMedecin(DashboardDTO d) {
        CardPanel c = new CardPanel("MEDECIN");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("Consultations terminées", String.valueOf(i(d.getNombreConsultationsTerminees()))));
        body.add(line("Consultations en cours", String.valueOf(i(d.getNombreConsultationsEnCours()))));
        body.add(line("Actes du jour", String.valueOf(i(d.getNombreActesRealisesDuJour()))));
        body.add(line("Montant actes", DF.format(n(d.getMontantTotalActesDuJour()))));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JComponent cardAdmin(DashboardDTO d) {
        CardPanel c = new CardPanel("ADMIN - STATISTIQUES");
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(line("Utilisateurs", String.valueOf(i(d.getNombreUtilisateursTotal()))));
        body.add(line("Patients", String.valueOf(i(d.getNombrePatientsTotal()))));
        body.add(line("Dossiers actifs", String.valueOf(i(d.getNombreDossiersActifs()))));

        c.add(body, BorderLayout.CENTER);
        return c;
    }

    private JPanel emptyMiniCard() {
        JPanel p = new JPanel();
        p.setBackground(DentalTheme.BG);
        p.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));
        p.setPreferredSize(new Dimension(120, 70));
        return p;
    }

    private JPanel line(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.textFont(12));
        l.setForeground(DentalTheme.MUTED);

        JLabel v = new JLabel(value);
        v.setFont(DentalTheme.textBold(12));
        v.setForeground(DentalTheme.PRIMARY_DARK);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private int i(Integer v){ return v == null ? 0 : v; }
    private double n(Double v){ return v == null ? 0.0 : v; }
}
