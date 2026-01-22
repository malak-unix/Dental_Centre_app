package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CaisseFactureDetailPanel extends JPanel {

    private final CaisseFactureRowDTO facture;

    public CaisseFactureDetailPanel(CaisseFactureRowDTO facture) {
        this.facture = facture;

        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);

        JLabel title = new JLabel("Détail de la facture " + safe(facture.getNumeroFacture()));
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JLabel sub = new JLabel(safe(facture.getPatientNom()));
        sub.setFont(DentalTheme.textBold(14));
        sub.setForeground(DentalTheme.TEXT);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(sub);

        h.add(left, BorderLayout.WEST);
        return h;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);

        CardPanel lignes = new CardPanel(null);
        lignes.setBackground(DentalTheme.CARD);
        lignes.setBorder(new EmptyBorder(10, 10, 10, 10));
        lignes.setOpaque(false);
        lignes.setLayout(new BorderLayout(8, 8));

        JLabel lignesTitle = new JLabel("Designation");
        lignesTitle.setFont(DentalTheme.titleFont(18));
        lignesTitle.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel lignesHeader = new JPanel(new BorderLayout());
        lignesHeader.setOpaque(false);
        lignesHeader.add(lignesTitle, BorderLayout.WEST);

        JTable t = new JTable(new DefaultTableModel(
                new Object[][]{
                        {"Consultation de controle", "1", money(facture.getMontant())}
                },
                new Object[]{"Acte", "Quantite", "Montant"}
        ));
        t.setRowHeight(28);
        UiStyles.styleTable(t);

        JScrollPane lignesSp = new JScrollPane(t);
        lignesSp.setBorder(BorderFactory.createEmptyBorder());
        lignesSp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        lignesSp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        lignes.add(lignesHeader, BorderLayout.NORTH);
        lignes.add(lignesSp, BorderLayout.CENTER);

        CardPanel totals = new CardPanel(null);
        totals.setBackground(DentalTheme.CARD);
        totals.setBorder(new EmptyBorder(10, 10, 10, 10));
        totals.setOpaque(false);
        totals.setLayout(new BorderLayout(8, 8));
        totals.setPreferredSize(new Dimension(260, 10));

        JLabel totalsTitle = new JLabel("Totaux");
        totalsTitle.setFont(DentalTheme.titleFont(18));
        totalsTitle.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel totalsHeader = new JPanel(new BorderLayout());
        totalsHeader.setOpaque(false);
        totalsHeader.add(totalsTitle, BorderLayout.WEST);

        totals.add(totalsHeader, BorderLayout.NORTH);
        totals.add(buildTotalsBlock(), BorderLayout.CENTER);

        body.add(lignes, BorderLayout.CENTER);
        body.add(totals, BorderLayout.EAST);

        return body;
    }

    private JComponent buildTotalsBlock() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(line("Total HT", money(facture.getMontant())));
        p.add(line("TVA", "0.00 DH"));
        p.add(Box.createVerticalStrut(10));
        p.add(lineBold("Total TTC", money(facture.getMontant())));
        p.add(Box.createVerticalStrut(14));
        p.add(line("Statut", safe(facture.getStatut())));
        p.add(line("Médecin", safe(facture.getMedecinNom())));

        return p;
    }

    private JPanel line(String k, String v) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lk = new JLabel(k + " : ");
        lk.setFont(DentalTheme.textFont(12));
        lk.setForeground(DentalTheme.TEXT);

        JLabel lv = new JLabel(v);
        lv.setFont(DentalTheme.textBold(12));
        lv.setForeground(DentalTheme.PRIMARY_DARK);

        row.add(lk, BorderLayout.WEST);
        row.add(lv, BorderLayout.EAST);
        row.setBorder(new EmptyBorder(6, 4, 6, 4));
        return row;
    }

    private JPanel lineBold(String k, String v) {
        JPanel row = line(k, v);
        row.setBorder(new EmptyBorder(10, 4, 10, 4));
        return row;
    }

    private String safe(Object v) { return v == null ? "" : String.valueOf(v); }

    private String money(Double v) {
        double x = v == null ? 0.0 : v;
        return String.format("%,.2f DH", x);
    }
}
