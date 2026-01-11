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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AgendaSemainePagePanel extends JPanel {

    private final AgendaAppService agendaApp;

    private final JTextField medecinIdField = new JTextField();
    private final JTextField dateField = new JTextField();
    private final JLabel weekLabel = new JLabel("Semaine du --/--/---- au --/--/----");


    private final Map<DayOfWeek, JPanel> dayColumns = new EnumMap<>(DayOfWeek.class);

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AgendaSemainePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        agendaApp = ApplicationContext.getBean(AgendaAppService.class);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(12, 12));
        add(card, BorderLayout.CENTER);

        card.add(buildTopBar(), BorderLayout.NORTH);
        card.add(buildWeekGrid(), BorderLayout.CENTER);

        medecinIdField.setText("1");
        dateField.setText(LocalDate.now().toString());

        loadSemaine();
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(8, 10, 8, 10);
        c.anchor = GridBagConstraints.WEST;

        // ===== 1) Gros titre AGENDA (à gauche)
        JLabel agendaTitle = new JLabel("AGENDA");
        agendaTitle.setFont(new Font("Serif", Font.BOLD, 40)); // ajuste si tu veux
        agendaTitle.setForeground(DentalTheme.TEXT2);

        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        top.add(agendaTitle, c);

        // ===== 2) Semaine du ... (au milieu)
        weekLabel.setFont(DentalTheme.textFont(14));
        weekLabel.setForeground(DentalTheme.TEXT2);

        c.gridx = 1;
        c.weightx = 1;                 // prend l'espace restant
        c.fill = GridBagConstraints.HORIZONTAL;
        top.add(weekLabel, c);

        // ===== 3) Champs + bouton (à droite)
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);

        JLabel medLbl = new JLabel("Médecin ID:");
        medLbl.setFont(DentalTheme.textBold(12));
        filters.add(medLbl);

        medecinIdField.setColumns(8); // PLUS LARGE (important)
        medecinIdField.setPreferredSize(new Dimension(110, 30));
        filters.add(medecinIdField);

        JLabel dateLbl = new JLabel("Date:");
        dateLbl.setFont(DentalTheme.textBold(12));
        filters.add(dateLbl);

        dateField.setColumns(12);     // PLUS LARGE (important)
        dateField.setPreferredSize(new Dimension(150, 30));
        filters.add(dateField);

        DentalButton load = new DentalButton("Charger");
        load.setPreferredSize(new Dimension(150, 38));
        load.addActionListener(e -> loadSemaine());
        filters.add(load);

        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        top.add(filters, c);

        return top;
    }

    private JComponent buildWeekGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 5, 14, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 14, 14, 14));

        grid.add(makeDayColumn(DayOfWeek.MONDAY, "LUNDI"));
        grid.add(makeDayColumn(DayOfWeek.TUESDAY, "MARDI"));
        grid.add(makeDayColumn(DayOfWeek.WEDNESDAY, "MERCREDI"));
        grid.add(makeDayColumn(DayOfWeek.THURSDAY, "JEUDI"));
        grid.add(makeDayColumn(DayOfWeek.FRIDAY, "VENDREDI"));

        return grid;
    }

    private JComponent makeDayColumn(DayOfWeek day, String label) {
        JPanel colWrap = new JPanel(new BorderLayout(8, 8));
        colWrap.setOpaque(false);

        // header like maquette
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel dayLabel = new JLabel(label, SwingConstants.CENTER);
        dayLabel.setOpaque(true);
        dayLabel.setBackground(new Color(0xF7, 0xF2, 0xEC));
        dayLabel.setForeground(DentalTheme.TEXT2);
        dayLabel.setFont(DentalTheme.textBold(12));
        dayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        header.add(dayLabel, BorderLayout.CENTER);

        // body cards
        JPanel body = new JPanel();
        body.setOpaque(true);
        body.setBackground(new Color(0xF7, 0xF2, 0xEC));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(new Color(0xF7, 0xF2, 0xEC));
        sp.setOpaque(false);

        dayColumns.put(day, body);

        colWrap.add(header, BorderLayout.NORTH);
        colWrap.add(sp, BorderLayout.CENTER);
        return colWrap;
    }

    private void loadSemaine() {
        try {
            if (agendaApp == null) throw new IllegalStateException("Bean AgendaAppService introuvable");

            Long medecinId = Long.valueOf(medecinIdField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            LocalDate monday = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate friday = monday.plusDays(4);

            weekLabel.setText("Semaine du " + DF.format(monday) + " au " + DF.format(friday));

            // clear columns
            for (JPanel p : dayColumns.values()) {
                p.removeAll();
            }

            AgendaMensuelDto dto = agendaApp.consulterAgendaSemaine(medecinId, date);

            List<RdvDto> rdvs = dto != null ? dto.getRdvsSemaine() : null;
            if (rdvs == null) rdvs = new ArrayList<>();

            for (RdvDto r : rdvs) {
                if (r.getDateRdv() == null) continue;

                DayOfWeek dow = r.getDateRdv().getDayOfWeek();
                JPanel target = dayColumns.get(dow);
                if (target == null) continue; // ignore weekend

                String patient = (r.getPatientNom() != null && !r.getPatientNom().isBlank())
                        ? r.getPatientNom()
                        : ("Patient #" + r.getPatientId());

                String time = (r.getHeure() != null ? r.getHeure().toString() : "--:--");
                String status = (r.getStatut() != null ? r.getStatut().toString() : "");

                RdvCardPanel card = new RdvCardPanel(patient, time, status);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);

                target.add(card);
                target.add(Box.createVerticalStrut(10));
            }

            // refresh
            for (JPanel p : dayColumns.values()) {
                p.revalidate();
                p.repaint();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda Semaine", JOptionPane.ERROR_MESSAGE);
        }
    }
}
