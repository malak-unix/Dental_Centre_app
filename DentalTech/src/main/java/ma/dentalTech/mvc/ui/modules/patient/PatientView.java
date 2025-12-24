package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.patient.PatientListDto;

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
    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnRefresh = new JButton("Actualiser");

    public PatientView(PatientController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        wireActions();
        refresh(); // chargement initial
    }

    private JComponent buildHeader() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Les patients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bar = new JPanel(new BorderLayout(8, 8));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(new JLabel("Nom:"), BorderLayout.WEST);
        left.add(searchNom, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(btnSearch);
        right.add(btnRefresh);

        bar.add(left, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        top.add(title);
        top.add(Box.createVerticalStrut(10));
        top.add(bar);

        return top;
    }

    private JComponent buildTable() {
        table.setRowHeight(28);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Résultats"));
        return sp;
    }

    private void wireActions() {
        btnRefresh.addActionListener(e -> refresh());

        btnSearch.addActionListener(e -> {
            String nom = searchNom.getText();
            if (nom == null || nom.isBlank()) {
                refresh();
                return;
            }
            try {
                List<PatientListDto> list = controller.rechercherParNom(nom);
                loadTable(list);
            } catch (Exception ex) {
                showError(ex);
            }
        });
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

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
