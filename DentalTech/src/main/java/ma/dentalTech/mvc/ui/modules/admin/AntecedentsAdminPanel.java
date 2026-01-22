package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentAdminController;
import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AntecedentsAdminPanel extends JPanel {

    private final AntecedentAdminController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Nom", "Catégorie", "Risque", "Description"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public AntecedentsAdminPanel(AntecedentAdminController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(12, 12));
        setOpaque(false);
        UiStyles.styleTable(table);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        JLabel title = new JLabel("Antécédents (Tous les patients)");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);

        JButton refresh = new JButton("Rafraichir");
        UiStyles.styleSecondaryButton(refresh);
        refresh.addActionListener(e -> reload());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(sp, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        List<AntecedentAdminRowDTO> rows = controller.getAll();
        for (AntecedentAdminRowDTO r : rows) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getPatientNomComplet(),
                    r.getNom(),
                    r.getCategorie(),
                    r.getNiveauDeRisque(),
                    r.getDescription()
            });
        }
    }
}



