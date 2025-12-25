package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import ma.dentalTech.service.modules.agenda.api.AgendaAppService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Page "Semaine" (maquette).
 * Elle appelle AgendaAppService.consulterAgendaSemaine(...)
 * et affiche : jours de la semaine + rdvs de la semaine.
 */
public class AgendaSemainePagePanel extends JPanel {

    private final AgendaAppService agendaApp;

    private final DefaultTableModel joursModel;
    private final DefaultTableModel rdvModel;

    private final JTextField medecinIdField;
    private final JTextField dateField;

    public AgendaSemainePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        // ✅ On récupère le bean "agendaAppService" (déjà dans ApplicationContext)
        agendaApp = ApplicationContext.getBean(AgendaAppService.class);

        CardPanel card = new CardPanel("Agenda - Semaine");
        add(card, BorderLayout.CENTER);

        // ===== Top filters
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        top.add(new JLabel("Médecin ID:"));
        medecinIdField = new JTextField("1", 6);
        top.add(medecinIdField);

        top.add(new JLabel("Date (yyyy-mm-dd):"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        top.add(dateField);

        DentalButton load = new DentalButton("Charger");
        top.add(load);

        card.add(top, BorderLayout.NORTH);

        // ===== Center split: jours / rdvs
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);

        CardPanel joursCard = new CardPanel("Jours (semaine)");
        CardPanel rdvCard = new CardPanel("RDV (semaine)");

        center.add(joursCard);
        center.add(rdvCard);

        // Table jours
        joursModel = new DefaultTableModel(new Object[]{"Date", "Début", "Fin", "Etat", "Commentaire"}, 0);
        JTable joursTable = new JTable(joursModel);
        joursTable.setRowHeight(26);
        joursTable.setFont(DentalTheme.textFont(12));
        joursTable.getTableHeader().setFont(DentalTheme.textBold(12));
        joursCard.add(new JScrollPane(joursTable), BorderLayout.CENTER);

        // Table rdvs
        rdvModel = new DefaultTableModel(new Object[]{"ID", "Patient", "Date", "Heure", "Motif", "Type", "Statut"}, 0);
        JTable rdvTable = new JTable(rdvModel);
        rdvTable.setRowHeight(26);
        rdvTable.setFont(DentalTheme.textFont(12));
        rdvTable.getTableHeader().setFont(DentalTheme.textBold(12));
        rdvCard.add(new JScrollPane(rdvTable), BorderLayout.CENTER);

        card.add(center, BorderLayout.CENTER);

        // Events
        load.addActionListener(e -> loadSemaine());

        // initial load
        loadSemaine();
    }

    private void loadSemaine() {
        try {
            if (agendaApp == null) throw new IllegalStateException("Bean AgendaAppService introuvable (agendaAppService)");

            Long medecinId = Long.valueOf(medecinIdField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            AgendaMensuelDto dto = agendaApp.consulterAgendaSemaine(medecinId, date);

            // ✅ remplir jours
            joursModel.setRowCount(0);
            List<DetailJourneeDto> jours = dto.getJoursSemaine();
            if (jours != null) {
                for (DetailJourneeDto d : jours) {
                    joursModel.addRow(new Object[]{
                            d.getDateJour(),
                            d.getHeureDebutTravail(),
                            d.getHeureFinTravail(),
                            d.getEtatJour(),
                            d.getCommentaire()
                    });
                }
            }

            // ✅ remplir rdvs
            rdvModel.setRowCount(0);
            List<RdvDto> rdvs = dto.getRdvsSemaine();
            if (rdvs != null) {
                for (RdvDto r : rdvs) {
                    rdvModel.addRow(new Object[]{
                            r.getId(),
                            r.getPatientId(),
                            r.getDateRdv(),
                            r.getHeure(),
                            r.getMotif(),
                            r.getTypeRdv(),
                            r.getStatut()
                    });
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda Semaine", JOptionPane.ERROR_MESSAGE);
        }
    }
}
