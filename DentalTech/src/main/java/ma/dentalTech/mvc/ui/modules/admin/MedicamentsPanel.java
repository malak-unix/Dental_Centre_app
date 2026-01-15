package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.MedicamentController;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicamentsPanel extends JPanel {

    private final MedicamentController controller;

    private final JTextField search = new JTextField();
    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnRefresh = new JButton("Rafraîchir");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Laboratoire", "Type", "Forme", "Remboursable", "Prix (DH)"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public MedicamentsPanel(MedicamentController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        wireActions();
        refresh();
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        JLabel title = new JLabel("Médicaments");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        search.setPreferredSize(new Dimension(320, 34));
        right.add(search);
        right.add(btnSearch);
        right.add(btnRefresh);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JComponent buildTableCard() {
        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout());
        card.setOpaque(false);

        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void wireActions() {
        btnRefresh.addActionListener(e -> refresh());

        btnSearch.addActionListener(e -> {
            String k = search.getText();
            List<Medicament> list = controller.searchByNom(k);
            fill(list);
        });

        search.addActionListener(e -> btnSearch.doClick());
    }

    public void refresh() {
        List<Medicament> list = controller.getAll();
        fill(list);
    }

    private void fill(List<Medicament> list) {
        model.setRowCount(0);
        if (list == null) return;

        for (Medicament m : list) {
            model.addRow(new Object[]{
                    m.getId(),
                    safe(m.getNom()),
                    safe(m.getLaboratoire()),
                    safe(m.getType()),
                    formeText(m.getForme()),
                    m.isRemboursable() ? "Oui" : "Non",
                    m.getPrixUnitaire() != null ? String.format("%.2f", m.getPrixUnitaire()) : ""
            });
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String formeText(FormeMedicament f) {
        return f == null ? "" : f.name();
    }
}
