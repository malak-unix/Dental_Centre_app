package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.service.modules.agenda.api.AgendaAppService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class AgendaSemainePagePanel extends JPanel {

    private final AgendaAppService agendaApp;

    private final JTextField medecinIdField = new JTextField("1", 6);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JLabel semaineLabel = new JLabel();

    private final Map<DayOfWeek, JPanel> dayColumns = new EnumMap<>(DayOfWeek.class);

    private static final DateTimeFormatter FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AgendaSemainePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        agendaApp = ApplicationContext.getBean(AgendaAppService.class);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(12, 12));
        add(card, BorderLayout.CENTER);

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildWeekGrid(), BorderLayout.CENTER);

        loadSemaine();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(8, 10, 0, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 8);
        c.gridy = 0;

        // AGENDA (à gauche)
        JLabel agendaTitle = new JLabel("AGENDA");
        agendaTitle.setFont(DentalTheme.H1);
        agendaTitle.setForeground(DentalTheme.TEXT2);

        c.gridx = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        header.add(agendaTitle, c);

        // Label semaine (au milieu, ne se chevauche plus)
        semaineLabel.setFont(DentalTheme.textFont(13));
        semaineLabel.setForeground(DentalTheme.TEXT2);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        header.add(semaineLabel, c);

        // Médecin ID
        JLabel medLbl = new JLabel("Médecin ID:");
        medLbl.setFont(DentalTheme.textBold(12));
        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        header.add(medLbl, c);

        c.gridx = 3;
        header.add(medecinIdField, c);

        // Date
        JLabel dateLbl = new JLabel("Date:");
        dateLbl.setFont(DentalTheme.textBold(12));
        c.gridx = 4;
        header.add(dateLbl, c);

        c.gridx = 5;
        header.add(dateField, c);

        // Charger
        DentalButton load = new DentalButton("Charger");
        load.addActionListener(e -> loadSemaine());

        c.gridx = 6;
        c.insets = new Insets(0, 10, 0, 0);
        header.add(load, c);

        return header;
    }

    private JComponent buildWeekGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 5, 14, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        addDay(grid, DayOfWeek.MONDAY, "LUNDI");
        addDay(grid, DayOfWeek.TUESDAY, "MARDI");
        addDay(grid, DayOfWeek.WEDNESDAY, "MERCREDI");
        addDay(grid, DayOfWeek.THURSDAY, "JEUDI");
        addDay(grid, DayOfWeek.FRIDAY, "VENDREDI");

        return grid;
    }

    private void addDay(JPanel grid, DayOfWeek dow, String title) {
        JPanel col = new JPanel(new BorderLayout(8, 8));
        col.setOpaque(false);

        // Header "jour" (style maquette : bouton/onglet)
        JButton dayHeader = new JButton(title);
        dayHeader.setFocusPainted(false);
        dayHeader.setFont(DentalTheme.textBold(12));
        dayHeader.setForeground(DentalTheme.TEXT2);
        dayHeader.setBackground(DentalTheme.BEIGE_LIGHT);
        dayHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        dayHeader.setContentAreaFilled(true);

        col.add(dayHeader, BorderLayout.NORTH);

        // Zone cartes RDV
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true));
        sp.getViewport().setBackground(DentalTheme.PANEL);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(true);

        col.add(sp, BorderLayout.CENTER);

        dayColumns.put(dow, list);
        grid.add(col);
    }

    private void loadSemaine() {
        try {
            if (agendaApp == null) throw new IllegalStateException("Bean AgendaAppService introuvable");

            Long medecinId = Long.valueOf(medecinIdField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate friday = monday.plusDays(4);
            semaineLabel.setText("Semaine du " + FR.format(monday) + " au " + FR.format(friday));

            // nettoyer colonnes
            for (JPanel p : dayColumns.values()) {
                p.removeAll();
            }

            AgendaMensuelDto dto = agendaApp.consulterAgendaSemaine(medecinId, date);
            List<RdvDto> rdvs = dto.getRdvsSemaine();
            if (rdvs == null) rdvs = Collections.emptyList();

            // grouper par date -> dayOfWeek
            for (RdvDto r : rdvs) {
                if (r.getDateRdv() == null) continue;
                DayOfWeek dow = r.getDateRdv().getDayOfWeek();
                JPanel container = dayColumns.get(dow);
                if (container == null) continue; // ignore samedi/dimanche

                String patient = (r.getPatientNom() != null && !r.getPatientNom().isBlank())
                        ? r.getPatientNom()
                        : ("#" + r.getPatientId());

                String time = (r.getHeure() != null) ? r.getHeure().toString() : "";
                String status = (r.getStatut() != null) ? r.getStatut().toString() : "";

                RdvCardPanel card = new RdvCardPanel(patient, time, status);
                container.add(card);
                container.add(Box.createVerticalStrut(10));
            }

            // refresh UI
            for (JPanel p : dayColumns.values()) {
                p.revalidate();
                p.repaint();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda Semaine", JOptionPane.ERROR_MESSAGE);
        }
    }
}
