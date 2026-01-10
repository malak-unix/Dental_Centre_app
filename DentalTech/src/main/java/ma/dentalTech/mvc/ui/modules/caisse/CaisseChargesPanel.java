package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.caisse.api.ChargesControllerV2;
import ma.dentalTech.mvc.dto.caisse.ChargeCreateDTO;
import ma.dentalTech.mvc.dto.caisse.ChargeFilterDTO;
import ma.dentalTech.mvc.dto.caisse.ChargeItemDTO;
import ma.dentalTech.mvc.dto.caisse.ChargeUpdateDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.caisse.dialogs.ChargeEditDialog;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaisseChargesPanel extends JPanel {

    private final JTextField txtDateDebut = new JTextField(10);
    private final JTextField txtDateFin   = new JTextField(10);

    // NB: ton ChargeFilterDTO n’a pas “categorie”, donc on filtre côté UI
    private final JComboBox<String> cbCategorie = new JComboBox<>(
            new String[]{"Toutes", "Personnel", "Matériel dentaire", "Consommables", "Logiciels", "Maintenance"}
    );

    private final DentalButton btnFilter = new DentalButton("Filtrer");

    private final DentalButton btnAdd    = new DentalButton("+ Ajouter");
    private final DentalButton btnEdit   = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");

    private final ChargesTableModel model = new ChargesTableModel();
    private final JTable table = new JTable(model);

    private final JLabel vTotalCharges = totalValue();
    private final JPanel pieHolder = new JPanel(new BorderLayout());

    private final ChargesControllerV2 controller;

    public CaisseChargesPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(DentalTheme.BG);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(buildTop(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        applyDefaultDates();

        // Controller (DI)
        ChargesControllerV2 c = null;
        try {
            c = ApplicationContext.getBean(ChargesControllerV2.class);
        } catch (Exception ignored) {}
        this.controller = c;

        wire();

        // Chargement initial
        refreshData();
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

        // actions CRUD
        filters.add(btnAdd);
        filters.add(btnEdit);
        filters.add(btnDelete);

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
        btnFilter.addActionListener(e -> refreshData());

        btnAdd.addActionListener(e -> {
            if (!ensureController()) return;

            Optional<ChargeCreateDTO> dtoOpt = ChargeEditDialog.showCreate(this);
            if (dtoOpt.isEmpty()) return;

            try {
                controller.create(dtoOpt.get());
                refreshData();
            } catch (Exception ex) {
                showError("Erreur ajout charge", ex);
            }
        });

        btnEdit.addActionListener(e -> {
            if (!ensureController()) return;

            Long id = getSelectedChargeId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Sélectionne une charge dans le tableau.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                ChargeItemDTO existing = controller.findById(id);
                Optional<ChargeUpdateDTO> dtoOpt = ChargeEditDialog.showEdit(this, existing);
                if (dtoOpt.isEmpty()) return;

                controller.update(id, dtoOpt.get());
                refreshData();
            } catch (Exception ex) {
                showError("Erreur modification charge", ex);
            }
        });

        btnDelete.addActionListener(e -> {
            if (!ensureController()) return;

            Long id = getSelectedChargeId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Sélectionne une charge dans le tableau.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int ok = JOptionPane.showConfirmDialog(this, "Supprimer cette charge ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            try {
                controller.delete(id);
                refreshData();
            } catch (Exception ex) {
                showError("Erreur suppression charge", ex);
            }
        });
    }

    private void refreshData() {
        // Si controller pas dispo, on affiche un message au lieu du fake data (plus propre)
        if (controller == null) {
            disableCrudButtons();
            vTotalCharges.setText("Controller chargesControllerV2 non disponible");
            renderPie(0, 0, 0, 0, 0);
            model.setRows(new ArrayList<>());
            return;
        }

        enableCrudButtons();

        ChargeFilterDTO filter = new ChargeFilterDTO();
        try {
            filter.setDateDebut(LocalDate.parse(txtDateDebut.getText().trim()));
            filter.setDateFin(LocalDate.parse(txtDateFin.getText().trim()));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Dates invalides. Format attendu: YYYY-MM-DD", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<ChargeItemDTO> list = controller.list(filter);

            // Filtre catégorie (côté UI)
            String cat = String.valueOf(cbCategorie.getSelectedItem());
            if (cat != null && !cat.equalsIgnoreCase("Toutes")) {
                list = list.stream()
                        .filter(x -> matchCategorie(cat, x.getTitre()))
                        .toList();
            }

            model.setRows(list);

            // total (service total ne prend pas catégorie, donc on calcule après filtre)
            double total = list.stream()
                    .mapToDouble(x -> x.getMontant() == null ? 0.0 : x.getMontant().doubleValue())
                    .sum();
            vTotalCharges.setText(String.format("%,.2f DH", total));

            // pie chart : somme par catégorie
            double personnel = sumBy(list, "personnel");
            double materiel  = sumBy(list, "matériel", "materiel");
            double conso     = sumBy(list, "consomm", "conso");
            double logiciels = sumBy(list, "logiciel");
            double maintenance = sumBy(list, "mainten");

            renderPie(personnel, materiel, conso, logiciels, maintenance);

        } catch (Exception ex) {
            showError("Erreur chargement charges", ex);
        }
    }

    private boolean matchCategorie(String catUI, String titre) {
        if (titre == null) return false;
        String t = titre.toLowerCase();
        String c = catUI.toLowerCase();

        // “Matériel dentaire” vs “materiel”
        if (c.contains("mat") ) return t.contains("mat");
        if (c.contains("personnel")) return t.contains("personnel");
        if (c.contains("consomm")) return t.contains("consomm") || t.contains("conso");
        if (c.contains("logiciel")) return t.contains("logiciel");
        if (c.contains("maint")) return t.contains("maint");
        return t.contains(c);
    }

    private double sumBy(List<ChargeItemDTO> list, String... keys) {
        return list.stream()
                .filter(x -> {
                    if (x.getTitre() == null) return false;
                    String t = x.getTitre().toLowerCase();
                    for (String k : keys) if (t.contains(k)) return true;
                    return false;
                })
                .map(ChargeItemDTO::getMontant)
                .filter(m -> m != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    private Long getSelectedChargeId() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;

        int modelRow = table.convertRowIndexToModel(viewRow);
        ChargeItemDTO row = model.getRowAt(modelRow);
        return row == null ? null : row.getId();
    }


    private boolean ensureController() {
        if (controller != null) return true;
        JOptionPane.showMessageDialog(this,
                "chargesControllerV2 n'est pas enregistré dans ApplicationContext/beans.properties",
                "Configuration manquante",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void disableCrudButtons() {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void enableCrudButtons() {
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
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

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this,
                title + " : " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }
}
