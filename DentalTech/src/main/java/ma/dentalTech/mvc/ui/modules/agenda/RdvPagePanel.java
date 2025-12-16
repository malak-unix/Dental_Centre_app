package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class RdvPagePanel extends JPanel {

    private final RdvController controller;
    private final DefaultTableModel model;

    public RdvPagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (RdvController) ApplicationContext.getBean("rdv.controller");

        CardPanel card = new CardPanel("Rendez-vous");
        add(card, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        DentalButton all = new DentalButton("Tous");
        DentalButton today = new DentalButton("Aujourd'hui");
        DentalButton upcoming = new DentalButton("À venir");

        top.add(all);
        top.add(today);
        top.add(upcoming);

        card.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Patient", "Date", "Heure", "Motif", "Statut"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        card.add(new JScrollPane(table), BorderLayout.CENTER);

        all.addActionListener(e -> refresh(safe(() -> controller.getAll())));
        today.addActionListener(e -> refresh(safe(() -> controller.getByDate(LocalDate.now()))));
        upcoming.addActionListener(e -> refresh(safe(() -> controller.getUpcomingFromToday())));

        refresh(safe(() -> controller.getAll()));
    }

    private void refresh(List<RdvDto> list) {
        model.setRowCount(0);
        for (RdvDto r : list) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getPatientId(),
                    r.getDateRdv(),
                    r.getHeure(),
                    r.getMotif(),
                    r.getStatut()
            });
        }
    }

    private interface SupplierX<T> { T get() throws Exception; }

    private <T> T safe(SupplierX<T> s) {
        try {
            if (controller == null) throw new IllegalStateException("Bean rdv.controller introuvable (beans.properties)");
            return s.get();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            return (T) java.util.List.of();
        }
    }
}
