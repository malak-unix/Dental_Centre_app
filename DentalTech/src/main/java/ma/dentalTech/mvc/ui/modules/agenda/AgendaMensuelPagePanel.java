package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AgendaMensuelPagePanel extends JPanel {

    private final AgendaController controller;

    private final DefaultTableModel agendaModel;
    private final DefaultTableModel jourModel;

    private Long selectedAgendaId;

    public AgendaMensuelPagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (AgendaController) ApplicationContext.getBean("agenda.controller");

        // Top
        CardPanel top = new CardPanel("Agendas mensuels");
        add(top, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        DentalButton refresh = new DentalButton("Rafraîchir");
        actions.add(refresh);
        top.add(actions, BorderLayout.CENTER);

        // Center split
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);

        // Left: agendas
        CardPanel agendasCard = new CardPanel("Liste des agendas");
        center.add(agendasCard);

        agendaModel = new DefaultTableModel(new Object[]{"ID", "Medecin", "Mois", "Année"}, 0);
        JTable agendaTable = new JTable(agendaModel);
        agendaTable.setRowHeight(26);
        agendaTable.setFont(DentalTheme.textFont(12));
        agendaTable.getTableHeader().setFont(DentalTheme.textBold(12));
        agendasCard.add(new JScrollPane(agendaTable), BorderLayout.CENTER);

        // Right: jours
        CardPanel joursCard = new CardPanel("Détails journées");
        center.add(joursCard);

        jourModel = new DefaultTableModel(new Object[]{"ID", "Date", "Début", "Fin", "Etat", "Commentaire"}, 0);
        JTable jourTable = new JTable(jourModel);
        jourTable.setRowHeight(26);
        jourTable.setFont(DentalTheme.textFont(12));
        jourTable.getTableHeader().setFont(DentalTheme.textBold(12));
        joursCard.add(new JScrollPane(jourTable), BorderLayout.CENTER);

        // Events
        refresh.addActionListener(e -> loadAgendas());

        agendaTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = agendaTable.getSelectedRow();
            if (row >= 0) {
                Object idObj = agendaModel.getValueAt(row, 0);
                selectedAgendaId = idObj == null ? null : Long.valueOf(idObj.toString());
                loadJours(selectedAgendaId);
            }
        });

        // Initial
        loadAgendas();
    }

    private void loadAgendas() {
        try {
            if (controller == null) throw new IllegalStateException("Bean agenda.controller introuvable (beans.properties)");
            List<AgendaMensuelDto> list = controller.getAllAgendas();

            agendaModel.setRowCount(0);
            for (AgendaMensuelDto a : list) {
                agendaModel.addRow(new Object[]{a.getId(), a.getMedecinId(), a.getMois(), a.getAnnee()});
            }
            jourModel.setRowCount(0);
            selectedAgendaId = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJours(Long agendaId) {
        try {
            if (agendaId == null) {
                jourModel.setRowCount(0);
                return;
            }
            List<DetailJourneeDto> jours = controller.getDetailJourneesByAgendaId(agendaId);

            jourModel.setRowCount(0);
            for (DetailJourneeDto d : jours) {
                jourModel.addRow(new Object[]{
                        d.getId(),
                        d.getDateJour(),
                        d.getHeureDebutTravail(),
                        d.getHeureFinTravail(),
                        d.getEtatJour(),
                        d.getCommentaire()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Détail journée", JOptionPane.ERROR_MESSAGE);
        }
    }
}
