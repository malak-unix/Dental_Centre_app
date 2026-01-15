package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentAdminController;
import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

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

        JLabel title = new JLabel("Antécédents (Tous les patients)");
        title.setFont(DentalTheme.titleFont(18));
        title.setForeground(DentalTheme.TEXT2);

        JButton refresh = new JButton("Rafraîchir");
        refresh.addActionListener(e -> reload());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout());
        card.add(new JScrollPane(table), BorderLayout.CENTER);

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
