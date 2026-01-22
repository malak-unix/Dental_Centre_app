package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.dto.caisse.FacturePaiementDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.caisse.dialogs.FacturePaiementDialog;
import ma.dentalTech.mvc.ui.modules.caisse.table.CaisseFacturesTableModel;
import ma.dentalTech.mvc.ui.modules.caisse.table.FactureActionsColumn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CaisseFacturesPanel extends JPanel {

    private final JTextField txtDateDebut = new JTextField(10);
    private final JTextField txtDateFin   = new JTextField(10);
    private final JComboBox<String> cbMedecin = new JComboBox<>(new String[]{"Tous"});
    private final DentalButton btnFilter = new DentalButton("Filtrer");

    private final CaisseFacturesTableModel model = new CaisseFacturesTableModel();
    private final JTable table = new JTable(model);
    private final JLabel emptyLabel = new JLabel("Aucune facture.");

    private final JLabel vTotalFactures = kpiValue();
    private final JLabel vTotalPaye = kpiValue();
    private final JLabel vTotalImpaye = kpiValue();
    private final JLabel vTotal = kpiValue();

    private final FactureControllerV2 controller;

    public CaisseFacturesPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        FactureControllerV2 c = null;
        try { c = ApplicationContext.getBean(FactureControllerV2.class); } catch (Exception ignored) {}
        controller = c;

        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottomTotals(), BorderLayout.SOUTH);

        applyDefaultDates();
        wire();

        refreshData();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("La Caisse");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        styleInput(txtDateDebut);
        styleInput(txtDateFin);

        filters.add(new JLabel("Date début"));
        filters.add(txtDateDebut);
        filters.add(new JLabel("Date fin"));
        filters.add(txtDateFin);

        styleCombo(cbMedecin);
        filters.add(cbMedecin);

        filters.add(btnFilter);

        top.add(title, BorderLayout.WEST);
        top.add(filters, BorderLayout.EAST);
        return top;
    }

    private JComponent buildCenter() {
        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("Liste des factures");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(255, 245, 225));
        table.setFont(DentalTheme.textFont(13));
        table.getTableHeader().setFont(DentalTheme.textBold(13));

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(10, 200));
        sp.setMinimumSize(new Dimension(10, 180));
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        emptyLabel.setFont(DentalTheme.textFont(12));
        emptyLabel.setForeground(DentalTheme.MUTED);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        card.add(header, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(emptyLabel, BorderLayout.SOUTH);

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
        bottom.add(kpi("Total Paye", vTotalPaye));
        bottom.add(kpi("Total Impaye", vTotalImpaye));
        bottom.add(kpi("Total", vTotal));

        return bottom;
    }
    private JPanel kpi(String label, JLabel value) {
        CardPanel c = new CardPanel(null);

        JLabel t = new JLabel(label);
        t.setFont(DentalTheme.titleFont(14));
        t.setForeground(DentalTheme.TEXT2);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(t, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(4, 0, 0, 0));
        center.add(value, BorderLayout.WEST);

        c.setLayout(new BorderLayout());
        c.add(header, BorderLayout.NORTH);
        c.add(center, BorderLayout.CENTER);
        return c;
    }
    private JLabel kpiValue() {
        JLabel l = new JLabel("�");
        l.setFont(DentalTheme.titleFont(12));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void wire() {
        btnFilter.addActionListener(e -> refreshData());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow < 0) return;
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    CaisseFactureRowDTO dto = model.getRowAt(modelRow);
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

    private void refreshData() {
        if (controller == null) {
            model.setRows(List.of());
            emptyLabel.setVisible(true);
            vTotalFactures.setText("0");
            vTotalPaye.setText("—");
            vTotalImpaye.setText("—");
            vTotal.setText("—");
            JOptionPane.showMessageDialog(this,
                    "factureControllerV2 non disponible.",
                    "Controller manquant",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate d1 = LocalDate.parse(txtDateDebut.getText().trim());
            LocalDate d2 = LocalDate.parse(txtDateFin.getText().trim());

            LocalDateTime start = d1.atStartOfDay();
            LocalDateTime end = d2.atTime(LocalTime.MAX);

            List<CaisseFactureRowDTO> list = controller.listBetween(start, end);
            model.setRows(list);
            emptyLabel.setVisible(list == null || list.isEmpty());

            double total = (list == null) ? 0.0 : list.stream().mapToDouble(x -> n(x.getTotalFacture())).sum();
            double paye  = (list == null) ? 0.0 : list.stream().mapToDouble(x -> n(x.getTotalPaye())).sum();
            double imp   = (list == null) ? 0.0 : list.stream().mapToDouble(x -> n(x.getReste())).sum();

            vTotalFactures.setText(String.valueOf(list == null ? 0 : list.size()));
            vTotalPaye.setText(money(paye) + " DH");
            vTotalImpaye.setText(money(imp) + " DH");
            vTotal.setText(money(total) + " DH");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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
        try {
            byte[] bytes = controller.exportPdf(dto.getFactureId());
            if (bytes == null || bytes.length == 0) throw new IllegalStateException("PDF vide");

            String name = (dto.getNumeroFacture() == null || dto.getNumeroFacture().isBlank())
                    ? ("facture_" + dto.getFactureId() + ".pdf")
                    : (dto.getNumeroFacture().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf");

            savePdf(bytes, name);

        } catch (Exception ex) {
            showError("Erreur export PDF", ex);
        }
    }

    private void onPay(CaisseFactureRowDTO dto) {
        try {
            FacturePaiementDTO pay = FacturePaiementDialog.open(this);
            if (pay == null) return;

            controller.payer(dto.getFactureId(), pay);
            refreshData();

            JOptionPane.showMessageDialog(this, "Paiement enregistré.", "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Erreur paiement", ex);
        }
    }

    private void onCancel(CaisseFactureRowDTO dto) {
        JOptionPane.showMessageDialog(this,
                "Annulation non implémentée dans ce sprint.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Utils =====

    private void savePdf(byte[] bytes, String defaultName) throws Exception {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(defaultName));

        int ok = fc.showSaveDialog(this);
        if (ok != JFileChooser.APPROVE_OPTION) return;

        File out = fc.getSelectedFile();
        Files.write(out.toPath(), bytes);

        JOptionPane.showMessageDialog(this, "PDF enregistré: " + out.getAbsolutePath(), "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    private double n(java.math.BigDecimal bd) { return bd == null ? 0.0 : bd.doubleValue(); }

    private String money(double v) {
        return String.format(java.util.Locale.US, "%,.2f", v).replace(',', ' ');
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, title + " : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void styleInput(JTextField tf) {
        tf.setFont(DentalTheme.textFont(13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(DentalTheme.textFont(13));
        cb.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));
        cb.setBackground(Color.WHITE);
    }
}







