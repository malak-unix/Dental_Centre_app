package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseDashboardControllerV2;
import ma.dentalTech.mvc.dto.caisse.CaisseChartDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardRequestDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseDashboardResponseDTO;
import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.caisse.table.CaisseFacturesTableModel;
import ma.dentalTech.mvc.ui.modules.caisse.table.FactureActionsColumn;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.mvc.dto.caisse.FacturePaiementDTO;
import ma.dentalTech.mvc.ui.modules.caisse.dialogs.FacturePaiementDialog;

import java.io.File;
import java.nio.file.Files;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CaisseDashboardPanel extends JPanel {

    private final LibelleRole role;
    private final Long currentUserId;
    private final CaisseDashboardControllerV2 controller;

    private final JComboBox<String> cbPeriode = new JComboBox<>(new String[]{"Ce mois ci", "Cette semaine", "Aujourd'hui", "Custom"});
    private final JComboBox<String> cbMedecin = new JComboBox<>(new String[]{"Tous"}); // pour plus tard
    private final JComboBox<String> cbStatut  = new JComboBox<>(new String[]{"Toutes", "PAYEE", "IMPAYEE"});
    private final JTextField txtSearch = new JTextField(18);
    private final DentalButton btnFilter = new DentalButton("Filter");

    private final JTextField txtDateDebut = new JTextField(10);
    private final JTextField txtDateFin   = new JTextField(10);

    private final JLabel vCaMois = valueLabel();
    private final JLabel vCharges = valueLabel();
    private final JLabel vBenefice = valueLabel();
    private final JLabel vImpayes = valueLabel();

    private final CaisseFacturesTableModel tableModel = new CaisseFacturesTableModel();
    private final JTable table = new JTable(tableModel);

    private final FactureControllerV2 factureController;

    private final JLabel vTotalFacturesCount = valueLabel();
    private final JLabel vTotalPaye = valueLabel();
    private final JLabel vTotalImpaye = valueLabel();
    private final JLabel vTotalGlobal = valueLabel();

    private final JPanel chartHolder = new JPanel(new BorderLayout());

    public CaisseDashboardPanel(LibelleRole role, Long currentUserId) {
        this.role = role;
        this.currentUserId = currentUserId;
        this.controller = (CaisseDashboardControllerV2) ApplicationContext.getBean("caisseDashboardControllerV2");
        FactureControllerV2 fc = null;
        try { fc = ApplicationContext.getBean(FactureControllerV2.class); } catch (Exception ignored) {}
        this.factureController = fc;

        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(buildTopFilters(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        wireActions();
        applyDefaultDates();
        refreshData();
    }

    private JComponent buildTopFilters() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel title = new JLabel("La Caisse");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);
        left.add(title);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        styleCombo(cbPeriode);
        styleCombo(cbMedecin);
        styleCombo(cbStatut);

        styleSearch(txtSearch);

        filters.add(cbPeriode);
        filters.add(cbMedecin);
        filters.add(cbStatut);
        filters.add(txtSearch);
        filters.add(btnFilter);

        top.add(left, BorderLayout.WEST);
        top.add(filters, BorderLayout.EAST);

        return top;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);

        body.add(buildKpisAndChart(), BorderLayout.NORTH);
        body.add(buildTableBlock(), BorderLayout.CENTER);
        body.add(buildBottomTotals(), BorderLayout.SOUTH);

        return body;
    }

    private JComponent buildKpisAndChart() {
        JPanel block = new JPanel(new BorderLayout(16, 16));
        block.setOpaque(false);

        JPanel kpis = new JPanel(new GridLayout(1, 4, 12, 12));
        kpis.setOpaque(false);

        kpis.add(kpiCard("CA du mois", vCaMois));
        kpis.add(kpiCard("Charges du mois", vCharges));
        kpis.add(kpiCard("Bénéfice", vBenefice));
        kpis.add(kpiCard("Impayés", vImpayes));

        CardPanel chartCard = new CardPanel("Revenus vs Charges (6 derniers mois)");
        chartCard.add(chartHolder, BorderLayout.CENTER);

        block.add(kpis, BorderLayout.NORTH);
        block.add(chartCard, BorderLayout.CENTER);

        return block;
    }

    private JComponent buildTableBlock() {
        CardPanel tableCard = new CardPanel("Liste des factures");

        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));
        tableCard.add(sp, BorderLayout.CENTER);

        FactureActionsColumn.install(table, this::onView, this::onPdf, this::onPay, this::onCancel);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    CaisseFactureRowDTO dto = tableModel.getRowAt(row);
                    if (dto != null) onView(dto);
                }
            }
        });

        return tableCard;
    }

    private JComponent buildBottomTotals() {
        JPanel bottom = new JPanel(new GridLayout(1, 4, 12, 12));
        bottom.setOpaque(false);

        bottom.add(kpiCard("Total Factures", vTotalFacturesCount));
        bottom.add(kpiCard("Total Payé", vTotalPaye));
        bottom.add(kpiCard("Total Impayé", vTotalImpaye));
        bottom.add(kpiCard("Total", vTotalGlobal));

        return bottom;
    }

    private CardPanel kpiCard(String title, JLabel value) {
        CardPanel c = new CardPanel(title);
        value.setFont(DentalTheme.titleFont(22));
        value.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(value, BorderLayout.WEST);

        c.add(center, BorderLayout.CENTER);
        return c;
    }

    private void wireActions() {
        btnFilter.addActionListener(e -> {
            applyDatesFromPeriode();
            refreshData();
        });
    }

    private void refreshData() {
        try {
            String statutUi = String.valueOf(cbStatut.getSelectedItem());
            String statut = "Toutes".equalsIgnoreCase(statutUi) ? "TOUTES" : statutUi;

            String search = txtSearch.getText();
            if ("Rechercher un client...".equalsIgnoreCase(search)) search = null;
            if (search != null && search.isBlank()) search = null;

            CaisseDashboardRequestDTO req = CaisseDashboardRequestDTO.builder()
                    .dateDebut(parseDate(txtDateDebut.getText()))
                    .dateFin(parseDate(txtDateFin.getText()))
                    .statut(statut)
                    .search(search)
                    .build();

            CaisseDashboardResponseDTO res = controller.getDashboard(req, role, currentUserId);

            double revenus = n(res.getTotalRevenus());
            double charges = n(res.getTotalCharges());
            double benefice = revenus - charges;

            vCaMois.setText(money(revenus) + " DH");
            vCharges.setText(money(charges) + " DH");
            vBenefice.setText(money(benefice) + " DH");

            List<CaisseFactureRowDTO> list = res.getFactures();
            long nbImpayees = list == null ? 0 : list.stream().filter(f -> isImpaye(f.getStatut())).count();
            vImpayes.setText(nbImpayees + " Factures");

            tableModel.setRows(list);

            vTotalFacturesCount.setText(String.valueOf(list == null ? 0 : list.size()));
            vTotalPaye.setText(money(n(res.getTotalRegle())) + " DH");
            vTotalImpaye.setText(money(n(res.getTotalNonRegle())) + " DH");
            vTotalGlobal.setText(money(n(res.getTotalFactures())) + " DH");

            renderChart(res);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur chargement caisse: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderChart(CaisseDashboardResponseDTO res) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            CaisseChartDTO chart = res.getChart();

            if (chart != null && chart.getLabels() != null && !chart.getLabels().isEmpty()) {
                List<String> labels = chart.getLabels();
                List<Double> revenus = chart.getRevenus();
                List<Double> charges = chart.getCharges();

                for (int i = 0; i < labels.size(); i++) {
                    String label = labels.get(i);
                    double r = (revenus != null && i < revenus.size() && revenus.get(i) != null) ? revenus.get(i) : 0.0;
                    double c = (charges != null && i < charges.size() && charges.get(i) != null) ? charges.get(i) : 0.0;
                    dataset.addValue(r, "Revenus", label);
                    dataset.addValue(c, "Charges", label);
                }
            } else {
                dataset.addValue(1, "Revenus", "M1");
                dataset.addValue(1, "Charges", "M1");
            }

            JFreeChart jchart = ChartFactory.createLineChart("", "Mois", "DH", dataset);

            chartHolder.removeAll();
            chartHolder.add(new ChartPanel(jchart), BorderLayout.CENTER);
            chartHolder.revalidate();
            chartHolder.repaint();

        } catch (Throwable t) {
            chartHolder.removeAll();
            chartHolder.add(new JLabel("⚠️ Installe JFreeChart (pom.xml) pour afficher le graphe"), BorderLayout.CENTER);
            chartHolder.revalidate();
            chartHolder.repaint();
        }
    }

    // ===== actions =====
    private void onView(CaisseFactureRowDTO dto) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Détail facture", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setContentPane(new CaisseFactureDetailPanel(dto));
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void onPdf(CaisseFactureRowDTO dto) {
        try {
            if (factureController == null) throw new IllegalStateException("factureControllerV2 non disponible");

            byte[] bytes = factureController.exportPdf(dto.getFactureId());
            if (bytes == null || bytes.length == 0) throw new IllegalStateException("PDF vide");

            String name = (dto.getNumeroFacture() == null || dto.getNumeroFacture().isBlank())
                    ? ("facture_" + dto.getFactureId() + ".pdf")
                    : (dto.getNumeroFacture().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf");

            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(name));
            int ok = fc.showSaveDialog(this);
            if (ok != JFileChooser.APPROVE_OPTION) return;

            Files.write(fc.getSelectedFile().toPath(), bytes);
            JOptionPane.showMessageDialog(this, "PDF enregistré.", "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur PDF: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPay(CaisseFactureRowDTO dto) {
        try {
            if (factureController == null) throw new IllegalStateException("factureControllerV2 non disponible");

            FacturePaiementDTO pay = FacturePaiementDialog.open(this);
            if (pay == null) return;

            factureController.payer(dto.getFactureId(), pay);

            // refresh dashboard
            refreshData();

            JOptionPane.showMessageDialog(this, "Paiement enregistré.", "OK", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur paiement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onCancel(CaisseFactureRowDTO dto) {
        int ok = JOptionPane.showConfirmDialog(this, "Annuler " + dto.getNumeroFacture() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Annulée (brancher service ensuite)", "OK", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===== helpers =====
    private void styleCombo(JComboBox<String> cb) {
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleSearch(JTextField tf) {
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setText("Rechercher un client...");
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("—");
        l.setFont(DentalTheme.textBold(16));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void applyDefaultDates() {
        LocalDate now = LocalDate.now();
        txtDateDebut.setText(now.withDayOfMonth(1).toString());
        txtDateFin.setText(now.toString());
    }

    private void applyDatesFromPeriode() {
        LocalDate now = LocalDate.now();
        String p = String.valueOf(cbPeriode.getSelectedItem());
        if ("Aujourd'hui".equalsIgnoreCase(p)) {
            txtDateDebut.setText(now.toString());
            txtDateFin.setText(now.toString());
        } else if ("Cette semaine".equalsIgnoreCase(p)) {
            LocalDate start = now.minusDays(6);
            txtDateDebut.setText(start.toString());
            txtDateFin.setText(now.toString());
        } else if ("Ce mois ci".equalsIgnoreCase(p)) {
            txtDateDebut.setText(now.withDayOfMonth(1).toString());
            txtDateFin.setText(now.toString());
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim());
    }

    private double n(Double v) { return v == null ? 0.0 : v; }

    private String money(Double v) {
        double x = v == null ? 0.0 : v;
        return String.format("%,.2f", x);
    }

    private boolean isImpaye(String s) {
        if (s == null) return false;
        return s.equalsIgnoreCase("IMPAYEE")
                || s.equalsIgnoreCase("NON_PAYEE")
                || s.equalsIgnoreCase("PARTIEL");
    }

    private boolean isPayee(String s) {
        return s != null && (s.equalsIgnoreCase("PAYEE"));
    }


}
