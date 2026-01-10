package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.caisse.table.FactureActionsColumn;
import ma.dentalTech.mvc.ui.modules.caisse.table.CaisseFacturesTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CaisseFacturesPanel extends JPanel {

    private final JTextField txtDateDebut = new JTextField(10);
    private final JTextField txtDateFin   = new JTextField(10);
    private final JComboBox<String> cbMedecin = new JComboBox<>(new String[]{"Tous"});
    private final DentalButton btnFilter = new DentalButton("Filtrer");

    private final CaisseFacturesTableModel model = new CaisseFacturesTableModel();
    private final JTable table = new JTable(model);

    private final JLabel vTotalFactures = kpiValue();
    private final JLabel vTotalPaye = kpiValue();
    private final JLabel vTotalImpaye = kpiValue();
    private final JLabel vTotal = kpiValue();

    public CaisseFacturesPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottomTotals(), BorderLayout.SOUTH);

        applyDefaultDates();
        wire();

        // TODO: remplace par controller/service
        loadFakeData();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("La Caisse");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        filters.add(new JLabel("Date début"));
        styleField(txtDateDebut);
        filters.add(txtDateDebut);

        filters.add(new JLabel("Date fin"));
        styleField(txtDateFin);
        filters.add(txtDateFin);

        styleCombo(cbMedecin);
        filters.add(cbMedecin);

        filters.add(btnFilter);

        top.add(title, BorderLayout.WEST);
        top.add(filters, BorderLayout.EAST);
        return top;
    }

    private JComponent buildCenter() {
        CardPanel card = new CardPanel("Liste des factures");

        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));
        card.add(sp, BorderLayout.CENTER);

        // mêmes actions que dashboard
        FactureActionsColumn.install(
                table,
                this::onView,
                this::onPdf,
                this::onPay,
                this::onCancel
        );

        return card;
    }

    private JComponent buildBottomTotals() {
        JPanel bottom = new JPanel(new GridLayout(1, 4, 12, 12));
        bottom.setOpaque(false);

        bottom.add(kpi("Total Factures", vTotalFactures));
        bottom.add(kpi("Total Payé", vTotalPaye));
        bottom.add(kpi("Total Impayé", vTotalImpaye));
        bottom.add(kpi("Total", vTotal));

        return bottom;
    }

    private CardPanel kpi(String title, JLabel value) {
        CardPanel c = new CardPanel(title);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(value, BorderLayout.WEST);
        c.add(center, BorderLayout.CENTER);
        return c;
    }

    private JLabel kpiValue() {
        JLabel l = new JLabel("—");
        l.setFont(DentalTheme.titleFont(18));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void wire() {
        btnFilter.addActionListener(e -> {
            // TODO: brancher service: rechercher factures entre dates + médecin
            loadFakeData();
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    CaisseFactureRowDTO dto = model.getRowAt(row);
                    if (dto != null) onView(dto);
                }
            }
        });
    }

    private void applyDefaultDates() {
        LocalDate now = LocalDate.now();
        txtDateDebut.setText(now.withDayOfMonth(1).toString());
        txtDateFin.setText(now.toString());
    }

    // ===== Actions =====

    private void onView(CaisseFactureRowDTO dto) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Détail facture", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setContentPane(new CaisseFactureDetailPanel(dto));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void onPdf(CaisseFactureRowDTO dto) {
        JOptionPane.showMessageDialog(this, "PDF: " + dto.getNumeroFacture(), "PDF", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onPay(CaisseFactureRowDTO dto) {
        JOptionPane.showMessageDialog(this, "Paiement: " + dto.getNumeroFacture(), "Paiement", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onCancel(CaisseFactureRowDTO dto) {
        int ok = JOptionPane.showConfirmDialog(this, "Annuler " + dto.getNumeroFacture() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Annulée (brancher service ensuite)", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===== styles =====

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleField(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void loadFakeData() {
        List<CaisseFactureRowDTO> list = List.of(
                CaisseFactureRowDTO.builder().factureId(1L).numeroFacture("#T00564").patientNom("Sumit Estève").medecinNom("Dr. Ebaï").dateEmission(LocalDate.now().minusDays(2)).montant(300.0).statut("IMPAYEE").canView(true).canPrint(true).canPay(true).canCancel(true).build(),
                CaisseFactureRowDTO.builder().factureId(2L).numeroFacture("#T00355").patientNom("Toupey Carificle").medecinNom("Dr. El Idrissi").dateEmission(LocalDate.now().minusDays(5)).montant(300.0).statut("PAYEE").canView(true).canPrint(true).canPay(false).canCancel(false).build(),
                CaisseFactureRowDTO.builder().factureId(3L).numeroFacture("#T00362").patientNom("Sami Enlibais").medecinNom("Dr. El Idrissi").dateEmission(LocalDate.now().minusDays(8)).montant(300.0).statut("IMPAYEE").canView(true).canPrint(true).canPay(true).canCancel(true).build()
        );

        model.setRows(list);

        double total = list.stream().mapToDouble(f -> f.getMontant() == null ? 0.0 : f.getMontant()).sum();
        double paye = list.stream().filter(f -> "PAYEE".equalsIgnoreCase(f.getStatut())).mapToDouble(f -> f.getMontant() == null ? 0.0 : f.getMontant()).sum();
        double impaye = list.stream().filter(f -> "IMPAYEE".equalsIgnoreCase(f.getStatut())).mapToDouble(f -> f.getMontant() == null ? 0.0 : f.getMontant()).sum();

        vTotalFactures.setText(String.valueOf(list.size()));
        vTotalPaye.setText(String.format("%,.2f DH", paye));
        vTotalImpaye.setText(String.format("%,.2f DH", impaye));
        vTotal.setText(String.format("%,.2f DH", total));
    }
}
