package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.mvc.dto.caisse.ChargeItemDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.caisse.table.ChargesTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CaisseChargesPanel extends JPanel {

    private final JTextField txtDateDebut = new JTextField(10);
    private final JTextField txtDateFin   = new JTextField(10);
    private final JComboBox<String> cbCategorie = new JComboBox<>(new String[]{"Toutes", "Personnel", "Matériel dentaire", "Consommables", "Logiciels", "Maintenance"});
    private final DentalButton btnFilter = new DentalButton("Filtrer");

    private final ChargesTableModel model = new ChargesTableModel();
    private final JTable table = new JTable(model);

    private final JLabel vTotalCharges = totalValue();
    private final JPanel pieHolder = new JPanel(new BorderLayout());

    public CaisseChargesPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(buildTop(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        applyDefaultDates();
        wire();

        // TODO: remplace par controller/service
        loadFakeData();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Gestion des Charges");
        title.setFont(DentalTheme.titleFont(20));
        title.setForeground(DentalTheme.PRIMARY_DARK);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);

        filters.add(new JLabel("Date début"));
        styleField(txtDateDebut);
        filters.add(txtDateDebut);

        filters.add(new JLabel("Date fin"));
        styleField(txtDateFin);
        filters.add(txtDateFin);

        styleCombo(cbCategorie);
        filters.add(cbCategorie);

        filters.add(btnFilter);

        top.add(title, BorderLayout.WEST);
        top.add(filters, BorderLayout.CENTER);
        return top;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 16));
        body.setOpaque(false);

        CardPanel left = new CardPanel("Liste des charges");
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true));
        left.add(sp, BorderLayout.CENTER);

        CardPanel right = new CardPanel("Répartition des charges");
        right.add(pieHolder, BorderLayout.CENTER);

        body.add(left);
        body.add(right);
        return body;
    }

    private JComponent buildBottom() {
        CardPanel bottom = new CardPanel("Toutes les charges");
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(vTotalCharges, BorderLayout.WEST);
        bottom.add(p, BorderLayout.CENTER);
        return bottom;
    }

    private JLabel totalValue() {
        JLabel l = new JLabel("—");
        l.setFont(DentalTheme.titleFont(18));
        l.setForeground(DentalTheme.PRIMARY_DARK);
        return l;
    }

    private void wire() {
        btnFilter.addActionListener(e -> {
            // TODO: brancher service + filtre (ChargeFilterDTO)
            loadFakeData();
        });
    }

    private void applyDefaultDates() {
        LocalDate now = LocalDate.now();
        txtDateDebut.setText(now.withDayOfMonth(1).toString());
        txtDateFin.setText(now.toString());
    }

    private void renderPie(double personnel, double materiel, double conso, double logiciels, double maintenance) {
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("Charges de personnel", personnel);
        ds.setValue("Matériel dentaire", materiel);
        ds.setValue("Consommables", conso);
        ds.setValue("Logiciels", logiciels);
        ds.setValue("Maintenance", maintenance);

        JFreeChart chart = ChartFactory.createPieChart("", ds, true, true, false);

        pieHolder.removeAll();
        pieHolder.add(new ChartPanel(chart), BorderLayout.CENTER);
        pieHolder.revalidate();
        pieHolder.repaint();
    }

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
        List<ChargeItemDTO> list = List.of(
                ChargeItemDTO.builder().id(1L).cabinetId(1L).titre("Personnel").description("Assistante").montant(new BigDecimal("22000")).dateCharge(LocalDateTime.now().minusDays(8)).build(),
                ChargeItemDTO.builder().id(2L).cabinetId(1L).titre("Matériel dentaire").description("Équipements").montant(new BigDecimal("20000")).dateCharge(LocalDateTime.now().minusDays(6)).build(),
                ChargeItemDTO.builder().id(3L).cabinetId(1L).titre("Consommables").description("Gants / Masques").montant(new BigDecimal("8000")).dateCharge(LocalDateTime.now().minusDays(4)).build(),
                ChargeItemDTO.builder().id(4L).cabinetId(1L).titre("Logiciels").description("Licence").montant(new BigDecimal("5900")).dateCharge(LocalDateTime.now().minusDays(3)).build(),
                ChargeItemDTO.builder().id(5L).cabinetId(1L).titre("Maintenance").description("Clim").montant(new BigDecimal("2900")).dateCharge(LocalDateTime.now().minusDays(2)).build()
        );

        model.setRows(list);

        double total = list.stream().mapToDouble(x -> x.getMontant() == null ? 0.0 : x.getMontant().doubleValue()).sum();
        vTotalCharges.setText(String.format("%,.2f DH", total));

        // mêmes % que ta maquette
        renderPie(64.5, 17.6, 8.8, 5.9, 2.9);
    }
}
