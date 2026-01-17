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
    private final JButton btnRefresh = new JButton("Rafraichir");
    private final JButton btnAdd = new JButton("Ajouter");
    private final JButton btnEdit = new JButton("Modifier");
    private final JButton btnDelete = new JButton("Supprimer");

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

        JLabel title = new JLabel("Medicaments");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        search.setPreferredSize(new Dimension(320, 34));
        right.add(search);
        right.add(btnSearch);
        right.add(btnRefresh);
        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);

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

        btnAdd.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> {
            Medicament m = selectedMedicament();
            if (m != null) openForm(m);
        });
        btnDelete.addActionListener(e -> deleteSelected());
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

    private Medicament selectedMedicament() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selectionne une ligne d'abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object idObj = model.getValueAt(row, 0);
        Long id = idObj == null ? null : Long.valueOf(idObj.toString());
        if (id == null) return null;
        return controller.getById(id);
    }

    private void deleteSelected() {
        Medicament m = selectedMedicament();
        if (m == null) return;

        int ok = JOptionPane.showConfirmDialog(this, "Supprimer le medicament #" + m.getId() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        controller.deleteById(m.getId());
        refresh();
    }

    private void openForm(Medicament initial) {
        boolean isEdit = initial != null && initial.getId() != null;

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), isEdit ? "Modifier medicament" : "Ajouter medicament", Dialog.ModalityType.APPLICATION_MODAL);
        d.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JTextField nomF = new JTextField();
        JTextField laboF = new JTextField();
        JTextField typeF = new JTextField();
        JComboBox<FormeMedicament> formeF = new JComboBox<>(FormeMedicament.values());
        JCheckBox rembF = new JCheckBox("Remboursable");
        JTextField prixF = new JTextField();

        if (isEdit) {
            nomF.setText(safe(initial.getNom()));
            laboF.setText(safe(initial.getLaboratoire()));
            typeF.setText(safe(initial.getType()));
            formeF.setSelectedItem(initial.getForme());
            rembF.setSelected(initial.isRemboursable());
            prixF.setText(initial.getPrixUnitaire() != null ? String.valueOf(initial.getPrixUnitaire()) : "");
        }

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(new JLabel("Nom *"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(nomF, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Laboratoire"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(laboF, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Type"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(typeF, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Forme"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(formeF, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0;
        form.add(new JLabel("Prix (DH)"), c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(prixF, c);

        c.gridx = 1; c.gridy++;
        c.weightx = 1.0;
        form.add(rembF, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        JButton save = new JButton("Enregistrer");
        actions.add(cancel);
        actions.add(save);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            String nom = nomF.getText() == null ? "" : nomF.getText().trim();
            if (nom.isBlank()) {
                JOptionPane.showMessageDialog(d, "Nom obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Medicament m = isEdit ? initial : new Medicament();
            m.setNom(nom);
            m.setLaboratoire(laboF.getText());
            m.setType(typeF.getText());
            m.setForme((FormeMedicament) formeF.getSelectedItem());
            m.setRemboursable(rembF.isSelected());
            if (prixF.getText() != null && !prixF.getText().isBlank()) {
                m.setPrixUnitaire(Double.parseDouble(prixF.getText().trim()));
            } else {
                m.setPrixUnitaire(0.0);
            }

            if (isEdit) controller.update(m);
            else controller.create(m);

            d.dispose();
            refresh();
        });

        d.add(form, BorderLayout.CENTER);
        d.add(actions, BorderLayout.SOUTH);
        d.pack();
        d.setSize(520, 360);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
