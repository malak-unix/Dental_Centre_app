package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    // ✅ boutons maquette
    private final DentalButton btnSearch = new DentalButton("Rechercher");
    private final DentalButton btnRefresh = new DentalButton("Actualiser");

    public PatientView(PatientController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        CardPanel card = new CardPanel(null, new BorderLayout(12, 12));
        add(card, BorderLayout.CENTER);

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTable(), BorderLayout.CENTER);

        wireActions();
        refresh();
    }

    private JComponent buildHeader() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Les patients");
        title.setFont(DentalTheme.H1);
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // gauche: label + champ
        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);

        JLabel lblNom = new JLabel("Nom:");
        lblNom.setFont(DentalTheme.textBold(12));
        lblNom.setForeground(DentalTheme.TEXT2);

        styleTextField(searchNom);

        left.add(lblNom, BorderLayout.WEST);
        left.add(searchNom, BorderLayout.CENTER);

        // droite: boutons
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

    private JComponent buildTable() {
        table.setRowHeight(30);
        table.setFont(DentalTheme.textFont(12));

        JTableHeader th = table.getTableHeader();
        th.setFont(DentalTheme.textBold(12));
        th.setReorderingAllowed(false);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sp.getViewport().setBackground(new Color(0xF3, 0xF3, 0xF3));

        // petit "header" Résultats (au lieu du TitledBorder Windows)
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setOpaque(false);

        JLabel results = new JLabel("Résultats");
        results.setFont(DentalTheme.textBold(12));
        results.setForeground(DentalTheme.TEXT2);

        JPanel resultsBar = new JPanel(new BorderLayout());
        resultsBar.setOpaque(false);
        resultsBar.setBorder(new EmptyBorder(6, 2, 6, 2));
        resultsBar.add(results, BorderLayout.WEST);

        CardPanel tableCard = new CardPanel(null, new BorderLayout());
        tableCard.add(resultsBar, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        wrap.add(tableCard, BorderLayout.CENTER);
        return wrap;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(DentalTheme.textFont(12));
        tf.setForeground(DentalTheme.TEXT2);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        tf.setPreferredSize(new Dimension(300, 36));
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
                List<PatientListDto> list = controller.rechercherParNom(nom.trim());
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
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
