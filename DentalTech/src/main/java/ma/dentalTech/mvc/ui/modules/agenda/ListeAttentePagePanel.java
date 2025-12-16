package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListeAttentePagePanel extends JPanel {

    private final ListeAttenteController controller;
    private final DefaultTableModel model;

    public ListeAttentePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (ListeAttenteController) ApplicationContext.getBean("listeAttente.controller");

        CardPanel card = new CardPanel("Liste d'attente");
        add(card, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        DentalButton refresh = new DentalButton("Rafraîchir");
        top.add(refresh);

        card.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nom"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> load());
        load();
    }

    private void load() {
        try {
            if (controller == null) throw new IllegalStateException("Bean listeAttente.controller introuvable (beans.properties)");
            List<ListeAttenteDto> list = controller.getAll();

            model.setRowCount(0);
            for (ListeAttenteDto l : list) {
                model.addRow(new Object[]{l.getId(), l.getNom()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Liste d'attente", JOptionPane.ERROR_MESSAGE);
        }
    }
}
