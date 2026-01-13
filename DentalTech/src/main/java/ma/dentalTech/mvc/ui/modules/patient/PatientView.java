package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientView extends JPanel {

    private final PatientController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom complet", "Téléphone"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);

    private final JTextField searchNom = new JTextField();
    private final DentalButton btnSearch = new DentalButton("Rechercher");
    private final DentalButton btnRefresh = new DentalButton("Actualiser");

    private final DentalButton btnAdd = new DentalButton("Ajouter");
    private final DentalButton btnEdit = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");

    // ✅ NOUVEAU : Antécédents
    private final DentalButton btnAntecedents = new DentalButton("Antécédents");

    public PatientView(PatientController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        add(card, BorderLayout.CENTER);

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTableCard(), BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);

        wireActions();
        refresh();
    }

    private JComponent buildHeader() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Les patients");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bar = new JPanel(new BorderLayout(12, 8));
        bar.setOpaque(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);
        JLabel lNom = new JLabel("Nom:");
        lNom.setFont(DentalTheme.textBold(12));
        lNom.setForeground(DentalTheme.TEXT2);

        searchNom.setPreferredSize(new Dimension(400, 34));
        searchNom.setFont(DentalTheme.textFont(12));

        left.add(lNom, BorderLayout.WEST);
        left.add(searchNom, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnSearch);
        right.add(btnRefresh);

        bar.add(left, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        top.add(title);
        top.add(Box.createVerticalStrut(10));
        top.add(bar);

        return top;
    }

    private JComponent buildTableCard() {
        CardPanel results = new CardPanel("Résultats");
        results.setLayout(new BorderLayout());

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        results.add(sp, BorderLayout.CENTER);
        return results;
    }

    private JComponent buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        actions.add(btnAdd);
        actions.add(btnEdit);

        // ✅ bouton Antécédents avant Supprimer (plus logique)
        actions.add(btnAntecedents);

        actions.add(btnDelete);

        return actions;
    }

    private void wireActions() {
        btnRefresh.addActionListener(e -> refresh());
        btnSearch.addActionListener(e -> doSearch());

        btnAdd.addActionListener(e -> {
            PatientFormDialog dlg = new PatientFormDialog(SwingUtilities.getWindowAncestor(this),
                    "Ajouter un patient", null);
            dlg.setVisible(true);

            if (!dlg.isConfirmed()) return;

            try {
                controller.creer(dlg.getDto());
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnEdit.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            try {
                PatientFormDto current = controller.consulter(id);
                PatientFormDialog dlg = new PatientFormDialog(SwingUtilities.getWindowAncestor(this),
                        "Modifier patient #" + id, current);
                dlg.setVisible(true);

                if (!dlg.isConfirmed()) return;

                controller.modifier(id, dlg.getDto());
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // ✅ NOUVEAU : ouvrir Antécédents du patient sélectionné
        btnAntecedents.addActionListener(e -> openAntecedents());

        // bonus: double clic sur la ligne => ouvrir antécédents
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openAntecedents();
            }
        });

        btnDelete.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            int ok = JOptionPane.showConfirmDialog(this,
                    "Supprimer le patient #" + id + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (ok != JOptionPane.YES_OPTION) return;

            try {
                controller.supprimer(id);
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    private void openAntecedents() {
        Long id = selectedId();
        if (id == null) return;

        int row = table.getSelectedRow();
        String nomComplet = String.valueOf(model.getValueAt(row, 1));

        AntecedentManagerDialog dlg = new AntecedentManagerDialog(
                SwingUtilities.getWindowAncestor(this),
                id,
                nomComplet
        );
        dlg.setVisible(true);
    }

    private void doSearch() {
        String nom = searchNom.getText();
        if (nom == null || nom.isBlank()) {
            refresh();
            return;
        }
        try {
            List<PatientListDto> list = controller.rechercherParNom(nom.trim());
            loadTable(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void refresh() {
        try {
            List<PatientListDto> list = controller.lister();
            loadTable(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadTable(List<PatientListDto> list) {
        model.setRowCount(0);
        if (list == null) return;
        for (PatientListDto p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNomComplet(),
                    p.getTelephone()
            });
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne d’abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
