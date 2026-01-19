package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.DetailJourneeDto;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AgendaSemainePagePanel extends JPanel {

    private final AgendaAppService agendaApp;
    private final AgendaController agendaController;
    private final RdvController rdvController;

    private final JTextField medecinIdField = new JTextField();
    private final JTextField dateField = new JTextField();
    private final JLabel weekLabel = new JLabel("Semaine du --/--/---- au --/--/----");
    private final JLabel medecinLabel = new JLabel("Agenda de : Dr --");

    private final Map<DayOfWeek, DayColumnPanel> dayColumns = new EnumMap<>(DayOfWeek.class);
    private final Map<DayOfWeek, JLabel> dayHeaders = new EnumMap<>(DayOfWeek.class);
    private final Map<DayOfWeek, JPanel> dayWraps = new EnumMap<>(DayOfWeek.class);

    private final Map<DayOfWeek, DetailJourneeDto> currentDetailsByDay = new EnumMap<>(DayOfWeek.class);
    private final Map<DayOfWeek, List<RdvDto>> currentRdvsByDay = new EnumMap<>(DayOfWeek.class);
    private final Map<DayOfWeek, LocalDate> currentDatesByDay = new EnumMap<>(DayOfWeek.class);

    private final DefaultListModel<RdvDto> rdvDayModel = new DefaultListModel<>();
    private final DefaultListModel<PlageHoraire> plageModel = new DefaultListModel<>();
    private final JList<RdvDto> rdvList = new JList<>(rdvDayModel);
    private final JList<PlageHoraire> plageList = new JList<>(plageModel);
    private final JPanel rdvCard = new JPanel(new CardLayout());
    private final JPanel plageCard = new JPanel(new CardLayout());
    private final JLabel rdvEmptyLabel = new JLabel("Aucun RDV");
    private final JLabel plageEmptyLabel = new JLabel("Aucune plage");
    private final JLabel selectedDayLabel = new JLabel("Selectionnez une journee");
    private final JLabel selectedRdvLabel = new JLabel("Aucun RDV selectionne");

    private final DentalButton btnProgram = new DentalButton("Programmer");
    private final DentalButton btnDelete = new DentalButton("Supprimer");

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(19, 0);
    private static final int SLOT_MINUTES = 30;
    private static final int SLOT_HEIGHT = 36;

    private Long medecinId = 1L;
    private Long selectedRdvId = null;
    private Long selectedDetailId = null;
    private LocalDate selectedDate = null;
    private DetailJourneeDto selectedDetail = null;
    private DayOfWeek selectedDay = null;
    private Long selectedPlageId = null;
    private PlageHoraire selectedPlage = null;
    private AppointmentBlockPanel selectedAppointmentBlock = null;

    public AgendaSemainePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        agendaApp = ApplicationContext.getBean(AgendaAppService.class);
        agendaController = ApplicationContext.getBean(AgendaController.class);
        rdvController = ApplicationContext.getBean(RdvController.class);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(12, 12));
        add(card, BorderLayout.CENTER);

        card.add(buildTopBar(), BorderLayout.NORTH);
        card.add(buildCenterArea(), BorderLayout.CENTER);
        card.add(buildFooterActions(), BorderLayout.SOUTH);

        medecinIdField.setText("1");
        dateField.setText(LocalDate.now().toString());
        updateMedecinLabel(medecinId);
        updateButtonsState();

        loadSemaine();
    }

    public void setMedecinId(Long id) {
        if (id == null || id <= 0) return;
        this.medecinId = id;
        medecinIdField.setText(String.valueOf(id));
        updateMedecinLabel(id);
    }

    public void reload() {
        String t = dateField.getText() == null ? "" : dateField.getText().trim();
        if (t.isBlank()) dateField.setText(LocalDate.now().toString());
        loadSemaine();
    }

    public void setDate(LocalDate date) {
        if (date == null) date = LocalDate.now();
        dateField.setText(date.toString());
        loadSemaine();
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel agendaTitle = new JLabel("AGENDA");
        agendaTitle.setFont(DentalTheme.titleFont(30));
        agendaTitle.setForeground(DentalTheme.TEXT2);

        medecinLabel.setFont(DentalTheme.textBold(12));
        medecinLabel.setForeground(DentalTheme.MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(agendaTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(medecinLabel);

        weekLabel.setFont(DentalTheme.textFont(14));
        weekLabel.setForeground(DentalTheme.TEXT2);
        weekLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        nav.setOpaque(false);

        JLabel dateLbl = new JLabel("Date:");
        dateLbl.setFont(DentalTheme.textBold(12));
        nav.add(dateLbl);

        DentalButton prev = new DentalButton("<<");
        DentalButton next = new DentalButton(">>");
        DentalButton today = new DentalButton("Aujourd'hui");
        Dimension navBtn = new Dimension(54, 34);
        prev.setPreferredSize(navBtn);
        next.setPreferredSize(navBtn);
        today.setPreferredSize(new Dimension(120, 34));

        dateField.setColumns(10);
        dateField.setPreferredSize(new Dimension(130, 30));
        dateField.setHorizontalAlignment(SwingConstants.CENTER);
        nav.add(prev);
        nav.add(dateField);
        nav.add(next);
        nav.add(today);

        top.add(left, BorderLayout.WEST);
        top.add(weekLabel, BorderLayout.CENTER);
        top.add(nav, BorderLayout.EAST);

        prev.addActionListener(e -> {
            shiftWeek(-7);
            loadSemaine();
        });
        next.addActionListener(e -> {
            shiftWeek(7);
            loadSemaine();
        });
        today.addActionListener(e -> {
            dateField.setText(LocalDate.now().toString());
            loadSemaine();
        });
        return top;
    }

    private JComponent buildWeekGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 5, 14, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 14, 14, 14));

        grid.add(makeDayColumn(DayOfWeek.MONDAY, "LU"));
        grid.add(makeDayColumn(DayOfWeek.TUESDAY, "MA"));
        grid.add(makeDayColumn(DayOfWeek.WEDNESDAY, "ME"));
        grid.add(makeDayColumn(DayOfWeek.THURSDAY, "JE"));
        grid.add(makeDayColumn(DayOfWeek.FRIDAY, "VE"));

        JScrollPane sp = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        return sp;
    }

    private JComponent buildCenterArea() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(buildWeekGrid(), BorderLayout.CENTER);
        JComponent details = buildDayDetailsPanel();
        details.setPreferredSize(new Dimension(320, 10));
        details.setMinimumSize(new Dimension(320, 10));
        center.add(details, BorderLayout.EAST);
        return center;
    }

    private JComponent buildDayDetailsPanel() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Details du jour");
        title.setFont(DentalTheme.textBold(13));
        title.setForeground(DentalTheme.TEXT2);
        selectedDayLabel.setFont(DentalTheme.textFont(12));
        selectedDayLabel.setForeground(DentalTheme.MUTED);
        selectedRdvLabel.setFont(DentalTheme.textFont(12));
        selectedRdvLabel.setForeground(DentalTheme.MUTED);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(selectedDayLabel);
        header.add(Box.createVerticalStrut(2));
        header.add(selectedRdvLabel);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(header);
        right.add(Box.createVerticalStrut(8));

        rdvList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String txt = value == null ? "" :
                    (value.getHeure() + " - " + (value.getMotif() == null ? "" : value.getMotif()) +
                     " [" + (value.getStatut() == null ? "" : value.getStatut()) + "]");
            JLabel l = new JLabel(txt);
            l.setOpaque(true);
            l.setBackground(isSelected ? new Color(0xE8, 0xD9, 0xCC) : DentalTheme.CARD);
            l.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return l;
        });
        rdvList.addListSelectionListener(e -> {
            RdvDto r = rdvList.getSelectedValue();
            selectedRdvId = r == null ? null : r.getId();
            updateSelectedRdv(r);
        });

        plageList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String txt = value == null ? "" :
                    (value.getHeureDebut() + " - " + value.getHeureFin() +
                     (Boolean.TRUE.equals(value.getDisponible()) ? " (libre)" : " (occupee)"));
            JLabel l = new JLabel(txt);
            l.setOpaque(true);
            l.setBackground(isSelected ? new Color(0xE8, 0xD9, 0xCC) : DentalTheme.CARD);
            l.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            return l;
        });
        plageList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            PlageHoraire p = plageList.getSelectedValue();
            selectedPlage = p;
            selectedPlageId = (p == null ? null : p.getId());
            updateButtonsState();
        });

        JScrollPane spRdv = new JScrollPane(rdvList);
        spRdv.setBorder(BorderFactory.createTitledBorder("RDV du jour"));
        spRdv.getViewport().setBackground(DentalTheme.CARD);
        spRdv.setPreferredSize(new Dimension(10, 150));
        spRdv.setMinimumSize(new Dimension(10, 150));

        JPanel rdvEmpty = new JPanel(new GridBagLayout());
        rdvEmpty.setOpaque(false);
        rdvEmptyLabel.setForeground(DentalTheme.MUTED);
        rdvEmpty.add(rdvEmptyLabel);
        rdvCard.removeAll();
        rdvCard.add(spRdv, "LIST");
        rdvCard.add(rdvEmpty, "EMPTY");

        JScrollPane spPlage = new JScrollPane(plageList);
        spPlage.setBorder(BorderFactory.createTitledBorder("Plages horaires"));
        spPlage.getViewport().setBackground(DentalTheme.CARD);
        spPlage.setPreferredSize(new Dimension(10, 150));
        spPlage.setMinimumSize(new Dimension(10, 150));

        JPanel plageEmpty = new JPanel(new GridBagLayout());
        plageEmpty.setOpaque(false);
        plageEmptyLabel.setForeground(DentalTheme.MUTED);
        plageEmpty.add(plageEmptyLabel);
        plageCard.removeAll();
        plageCard.add(spPlage, "LIST");
        plageCard.add(plageEmpty, "EMPTY");

        rdvCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        plageCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel lists = new JPanel();
        lists.setOpaque(false);
        lists.setLayout(new BoxLayout(lists, BoxLayout.Y_AXIS));
        lists.setAlignmentX(Component.LEFT_ALIGNMENT);
        lists.add(rdvCard);
        lists.add(Box.createVerticalStrut(8));
        lists.add(plageCard);

        right.add(lists);
        right.add(Box.createVerticalStrut(8));
        return right;
    }

    private JComponent buildFooterActions() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        footer.setOpaque(false);
        btnProgram.addActionListener(e -> onProgrammer());
        btnDelete.addActionListener(e -> onDelete());
        footer.add(btnProgram);
        footer.add(btnDelete);
        return footer;
    }

    private JComponent makeDayColumn(DayOfWeek day, String label) {
        JPanel colWrap = new JPanel(new BorderLayout(8, 8));
        colWrap.setOpaque(false);
        colWrap.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        colWrap.setMinimumSize(new Dimension(170, 10));
        colWrap.setPreferredSize(new Dimension(190, 10));

        JLabel dayLabel = new JLabel(label, SwingConstants.CENTER);
        dayLabel.setOpaque(true);
        dayLabel.setBackground(DentalTheme.CARD);
        dayLabel.setForeground(DentalTheme.TEXT2);
        dayLabel.setFont(DentalTheme.textBold(13));
        dayLabel.setMinimumSize(new Dimension(170, 54));
        dayLabel.setPreferredSize(new Dimension(190, 54));
        dayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        DayColumnPanel body = new DayColumnPanel();
        body.setOpaque(true);
        body.setBackground(DentalTheme.CARD);
        body.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        body.setMinimumSize(new Dimension(170, dayHeight()));
        body.setPreferredSize(new Dimension(190, dayHeight()));

        dayColumns.put(day, body);
        dayHeaders.put(day, dayLabel);
        dayWraps.put(day, colWrap);

        java.awt.event.MouseAdapter dayClick = new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                selectDay(day);
                if (e.getClickCount() >= 2) {
                    onProgrammer();
                }
            }
        };
        dayLabel.addMouseListener(dayClick);
        body.addDayClickListener(dayClick);
        colWrap.addMouseListener(dayClick);

        colWrap.add(dayLabel, BorderLayout.NORTH);
        colWrap.add(body, BorderLayout.CENTER);
        return colWrap;
    }

    private void loadSemaine() {
        try {
            if (agendaApp == null) throw new IllegalStateException("Bean AgendaAppService introuvable");

            Long mId;
            try {
                mId = Long.valueOf(medecinIdField.getText().trim());
            } catch (Exception ex) {
                mId = this.medecinId != null ? this.medecinId : 1L;
                medecinIdField.setText(String.valueOf(mId));
            }

            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText().trim());
            } catch (Exception ex) {
                date = LocalDate.now();
                dateField.setText(date.toString());
            }

            LocalDate monday = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate friday = monday.plusDays(4);

            weekLabel.setText("Semaine du " + DF.format(monday) + " au " + DF.format(friday));

            ensureWeekSetup(mId, monday);

            for (DayColumnPanel p : dayColumns.values()) {
                p.clearAppointments();
            }
            currentDetailsByDay.clear();
            currentRdvsByDay.clear();
            currentDatesByDay.clear();
            selectedRdvId = null;
            selectedAppointmentBlock = null;
            selectedPlage = null;
            selectedPlageId = null;
            currentDatesByDay.put(DayOfWeek.MONDAY, monday);
            currentDatesByDay.put(DayOfWeek.TUESDAY, monday.plusDays(1));
            currentDatesByDay.put(DayOfWeek.WEDNESDAY, monday.plusDays(2));
            currentDatesByDay.put(DayOfWeek.THURSDAY, monday.plusDays(3));
            currentDatesByDay.put(DayOfWeek.FRIDAY, friday);

            AgendaMensuelDto dto = agendaApp.consulterAgendaSemaine(mId, date);

            List<DetailJourneeDto> jours = dto != null ? dto.getJoursSemaine() : null;
            List<RdvDto> rdvs = dto != null ? dto.getRdvsSemaine() : null;
            if (jours == null) jours = new ArrayList<>();
            if (rdvs == null) rdvs = new ArrayList<>();

            for (DetailJourneeDto dj : jours) {
                if (dj.getDateJour() == null) continue;
                DayOfWeek dow = dj.getDateJour().getDayOfWeek();
                currentDetailsByDay.put(dow, dj);
                currentRdvsByDay.put(dow, new ArrayList<>());
            }

            for (RdvDto r : rdvs) {
                if (r.getDateRdv() == null) continue;
                DayOfWeek dow = r.getDateRdv().getDayOfWeek();
                currentRdvsByDay.computeIfAbsent(dow, k -> new ArrayList<>()).add(r);
            }

            for (Map.Entry<DayOfWeek, LocalDate> entry : currentDatesByDay.entrySet()) {
                DayOfWeek day = entry.getKey();
                LocalDate d = entry.getValue();
                if (d == null) continue;
                if (!currentDetailsByDay.containsKey(day)) {
                    DetailJourneeDto dj = ensureDetailForDate(mId, d);
                    if (dj != null) currentDetailsByDay.put(day, dj);
                }
                currentRdvsByDay.computeIfAbsent(day, k -> new ArrayList<>());
            }

            for (DayOfWeek day : dayColumns.keySet()) {
                List<RdvDto> list = currentRdvsByDay.getOrDefault(day, List.of());
                if (!list.isEmpty()) {
                    for (RdvDto r : list) {
                        addRdvCard(day, r);
                    }
                } else {
                    showEmptyDay(day);
                }
            }

            for (DayOfWeek day : dayHeaders.keySet()) {
                DetailJourneeDto dj = currentDetailsByDay.get(day);
                int count = currentRdvsByDay.getOrDefault(day, List.of()).size();
                updateDayBadge(day, dj, count);
            }

            DayOfWeek sel = date.getDayOfWeek();
            if (!currentDatesByDay.containsKey(sel)) {
                sel = DayOfWeek.MONDAY;
            }
            selectDay(sel);

            for (DayColumnPanel p : dayColumns.values()) {
                p.revalidate();
                p.repaint();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Agenda Semaine", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showCard(JPanel panel, String name) {
        if (panel == null) return;
        LayoutManager lm = panel.getLayout();
        if (lm instanceof CardLayout) {
            ((CardLayout) lm).show(panel, name);
        }
    }

    private void applySelectedDayStyles() {
        for (Map.Entry<DayOfWeek, JPanel> entry : dayWraps.entrySet()) {
            JPanel wrap = entry.getValue();
            if (wrap == null) continue;
            if (entry.getKey() == selectedDay) {
                wrap.setBorder(BorderFactory.createLineBorder(DentalTheme.PRIMARY_DARK, 2, true));
            } else {
                wrap.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }
        revalidate();
        repaint();
    }

    private void selectDay(DayOfWeek day) {
        selectedDay = day;
        DetailJourneeDto dj = currentDetailsByDay.get(day);
        selectedDate = currentDatesByDay.get(day);
        if (selectedDate == null) {
            try {
                LocalDate base = LocalDate.parse(dateField.getText().trim());
                LocalDate monday = base.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                selectedDate = monday.plusDays(day.getValue() - DayOfWeek.MONDAY.getValue());
            } catch (Exception ignored) {}
        }
        selectedDetail = dj;
        selectedDetailId = dj == null ? null : dj.getId();

        rdvDayModel.clear();
        for (RdvDto r : currentRdvsByDay.getOrDefault(day, List.of())) {
            rdvDayModel.addElement(r);
        }
        showCard(rdvCard, rdvDayModel.isEmpty() ? "EMPTY" : "LIST");

        plageModel.clear();
        if (selectedDetailId != null) {
            List<PlageHoraire> list = agendaController.getPlagesByDetailJournee(selectedDetailId);
            if (list != null) list.forEach(plageModel::addElement);
        }
        showCard(plageCard, plageModel.isEmpty() ? "EMPTY" : "LIST");

        selectedDayLabel.setText(selectedDate == null ? "Selectionnez une journee"
                : "Jour selectionne: " + DF.format(selectedDate));
        applySelectedDayStyles();
        if (selectedAppointmentBlock != null) {
            selectedAppointmentBlock.setSelected(false);
            selectedAppointmentBlock = null;
        }
        updateSelectedRdv(null);
        selectedPlage = null;
        selectedPlageId = null;
        updateButtonsState();
    }

    private void onProgrammer() {
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Selectionne une journee d'abord.");
            return;
        }

        if (selectedPlageId == null || selectedPlage == null || !Boolean.TRUE.equals(selectedPlage.getDisponible())) {
            JOptionPane.showMessageDialog(this, "Selectionne un creneau libre d'abord.");
            return;
        }

        if (selectedDetailId == null) {
            DetailJourneeDto dj = ensureDetailForDate(medecinId, selectedDate);
            if (dj != null) {
                selectedDetail = dj;
                selectedDetailId = dj.getId();
            }
        }

        if (selectedDetailId == null) {
            JOptionPane.showMessageDialog(this, "Aucune journee definie pour cette date.");
            return;
        }

        if (selectedDetail != null && selectedDetail.getEtatJour() != null
                && !"OUVERT".equalsIgnoreCase(selectedDetail.getEtatJour())) {
            JOptionPane.showMessageDialog(this, "Journee fermee ou feriee, choisissez un autre jour.");
            return;
        }

        List<PlageHoraire> libres = new ArrayList<>();
        libres.add(selectedPlage);

        ProgramDialog.Result res = ProgramDialog.open(this, medecinId, selectedDate, libres);
        if (res == null) return;

        if (agendaController == null || rdvController == null) {
            JOptionPane.showMessageDialog(this, "Controllers agenda/rdv introuvables.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RdvDto dto = RdvDto.builder()
                .patientId(res.patientId)
                .detailJourneeId(selectedDetailId)
                .dateRdv(selectedDate)
                .heure(res.heure)
                .motif(res.motif)
                .statut(EtatRendezVous.PLANIFIE)
                .build();

        rdvController.createAndLockPlage(dto, res.plageId);
        JOptionPane.showMessageDialog(this, "RDV programme.", "OK", JOptionPane.INFORMATION_MESSAGE);
        loadSemaine();
    }

    private void onDelete() {
        if (selectedRdvId == null) {
            JOptionPane.showMessageDialog(this, "Selectionne un RDV d'abord.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Supprimer RDV #" + selectedRdvId + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        if (rdvController == null) return;
        rdvController.deleteAndFreePlage(selectedRdvId);
        selectedRdvId = null;
        if (selectedAppointmentBlock != null) {
            selectedAppointmentBlock.setSelected(false);
            selectedAppointmentBlock = null;
        }
        loadSemaine();
    }

    private void updateSelectedRdv(RdvDto r) {
        if (r == null) {
            selectedRdvLabel.setText("Aucun RDV selectionne");
            updateButtonsState();
            return;
        }
        String patient = firstNonBlank(r.getPatientNom(), "Patient #" + r.getPatientId());
        String heure = (r.getHeure() != null ? r.getHeure().toString() : "--:--");
        String statut = (r.getStatut() != null ? r.getStatut().name() : "");
        String med = medecinLabel.getText().replace("Agenda de : ", "");
        selectedRdvLabel.setText("RDV: " + patient + " / " + heure + " / " + statut + " / " + med);
        selectedRdvId = r.getId();
        updateButtonsState();
    }

    private void updateButtonsState() {
        btnDelete.setEnabled(selectedRdvId != null);
        btnProgram.setEnabled(selectedPlageId != null && selectedPlage != null && Boolean.TRUE.equals(selectedPlage.getDisponible()));
    }

    private void updateDayBadge(DayOfWeek day, DetailJourneeDto dj, int rdvCount) {
        JLabel header = dayHeaders.get(day);
        if (header == null) return;

        header.setText(formatDayHeader(day, dj));

        if (dj == null || dj.getEtatJour() == null || !"OUVERT".equalsIgnoreCase(dj.getEtatJour())) {
            header.setBackground(new Color(0xDD, 0xDD, 0xDD));
            String status = (dj == null || dj.getEtatJour() == null) ? "FERME" : dj.getEtatJour();
            header.setToolTipText("Statut: " + status);
            return;
        }

        if (rdvCount >= 8) header.setBackground(new Color(0xF1, 0xD6, 0xD6));
        else if (rdvCount >= 6) header.setBackground(new Color(0xF8, 0xE6, 0xCC));
        else header.setBackground(new Color(0xD6, 0xF0, 0xE0));
        header.setToolTipText("RDV: " + rdvCount);
    }

    private String formatDayHeader(DayOfWeek day, DetailJourneeDto dj) {
        String label = dayLabel(day);
        LocalDate date = (dj != null && dj.getDateJour() != null)
                ? dj.getDateJour()
                : currentDatesByDay.get(day);
        String dateTxt = (date == null) ? "--/--" : DF.format(date);
        return "<html><center>" + label + "<br>" + dateTxt + "</center></html>";
    }

    private String dayLabel(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "LUNDI";
            case TUESDAY -> "MARDI";
            case WEDNESDAY -> "MERCREDI";
            case THURSDAY -> "JEUDI";
            case FRIDAY -> "VENDREDI";
            default -> day.toString();
        };
    }

    private int dayHeight() {
        int totalMinutes = (int) java.time.Duration.between(START_TIME, END_TIME).toMinutes();
        int slots = Math.max(1, totalMinutes / SLOT_MINUTES);
        return slots * SLOT_HEIGHT;
    }

    private void addRdvCard(DayOfWeek day, RdvDto r) {
        DayColumnPanel body = dayColumns.get(day);
        if (body == null || r == null) return;

        AppointmentBlockPanel card = new AppointmentBlockPanel(r);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                selectDay(day);
                updateSelectedRdv(r);
                if (selectedAppointmentBlock != null) selectedAppointmentBlock.setSelected(false);
                selectedAppointmentBlock = card;
                selectedAppointmentBlock.setSelected(true);
                selectedRdvId = r.getId();
            }
        });
        body.addAppointment(r.getHeure(), card);
    }

    private void showEmptyDay(DayOfWeek day) {
        DayColumnPanel body = dayColumns.get(day);
        if (body == null) return;
        body.showEmpty();
    }

    private void updateMedecinLabel(Long medecinId) {
        String label = "Agenda de : Dr #" + medecinId;
        try {
            var repo = new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();
            var med = repo.findById(medecinId);
            if (med != null) {
                String nom = (med.getNom() == null ? "" : med.getNom());
                String prenom = (med.getPrenom() == null ? "" : med.getPrenom());
                String full = (prenom + " " + nom).trim();
                if (!full.isBlank()) label = "Agenda de : Dr " + full;
            }
        } catch (Exception ignored) {}
        medecinLabel.setText(label);
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return "";
    }


    private static class DayColumnPanel extends JPanel {
        private final List<JPanel> slots = new ArrayList<>();
        private final Map<LocalTime, JPanel> slotByTime = new java.util.HashMap<>();

        DayColumnPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;
            LocalTime t = START_TIME;
            while (!t.isAfter(END_TIME.minusMinutes(SLOT_MINUTES))) {
                JPanel slot = new JPanel();
                slot.setOpaque(false);
                slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
                slot.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE7, 0xDE, 0xD4)));
                slot.setPreferredSize(new Dimension(10, SLOT_HEIGHT));
                slot.setMinimumSize(new Dimension(10, SLOT_HEIGHT));
                slot.setMaximumSize(new Dimension(Integer.MAX_VALUE, SLOT_HEIGHT));

                gbc.gridy = row++;
                gbc.weighty = 0;
                add(slot, gbc);

                slots.add(slot);
                slotByTime.put(t, slot);
                t = t.plusMinutes(SLOT_MINUTES);
            }
        }

        void addDayClickListener(java.awt.event.MouseListener listener) {
            addMouseListener(listener);
            for (JPanel slot : slots) {
                slot.addMouseListener(listener);
            }
        }

        void clearAppointments() {
            for (JPanel slot : slots) {
                slot.removeAll();
            }
        }

        void addAppointment(LocalTime time, AppointmentBlockPanel card) {
            if (card == null) return;
            JPanel slot = slotByTime.get(normalizeSlot(time));
            if (slot == null) return;
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, SLOT_HEIGHT - 6));
            card.setPreferredSize(new Dimension(10, SLOT_HEIGHT - 6));
            slot.add(card);
        }

        void showEmpty() {
            if (slots.isEmpty()) return;
            JLabel empty = new JLabel("Aucun RDV");
            empty.setForeground(DentalTheme.MUTED);
            empty.setFont(DentalTheme.textFont(11));
            JPanel slot = slots.get(0);
            slot.add(empty);
        }

        private LocalTime normalizeSlot(LocalTime time) {
            if (time == null) return START_TIME;
            int minutes = (int) java.time.Duration.between(START_TIME, time).toMinutes();
            if (minutes < 0) minutes = 0;
            int slotIndex = minutes / SLOT_MINUTES;
            return START_TIME.plusMinutes((long) slotIndex * SLOT_MINUTES);
        }
    }

    private void shiftWeek(int days) {
        try {
            LocalDate d = LocalDate.parse(dateField.getText().trim());
            dateField.setText(d.plusDays(days).toString());
        } catch (Exception ignored) {
            dateField.setText(LocalDate.now().toString());
        }
    }

    private void ensureWeekSetup(Long medecinId, LocalDate monday) {
        if (agendaController == null || medecinId == null || monday == null) return;
        for (int i = 0; i < 5; i++) {
            LocalDate day = monday.plusDays(i);
            ensureDetailForDate(medecinId, day);
        }
    }

    private DetailJourneeDto ensureDetailForDate(Long medecinId, LocalDate day) {
        if (agendaController == null || medecinId == null || day == null) return null;
        try {
            DetailJourneeDto dj = agendaController.getDetailJourneeByMedecinAndDate(medecinId, day);
            if (dj == null) {
                AgendaMensuelDto agenda = findOrCreateAgenda(medecinId, day);
                if (agenda == null || agenda.getId() == null) return null;
                DetailJourneeDto create = DetailJourneeDto.builder()
                        .agendaId(agenda.getId())
                        .dateJour(day)
                        .heureDebutTravail(LocalTime.of(9, 0))
                        .heureFinTravail(LocalTime.of(17, 0))
                        .etatJour("OUVERT")
                        .commentaire("Auto-cree")
                        .build();
                dj = agendaController.createDetailJournee(create);
            }
            if (dj != null && dj.getId() != null) {
                ensureDefaultPlages(dj.getId(), LocalTime.of(9, 0), LocalTime.of(17, 0));
            }
            return dj;
        } catch (Exception ignored) {
            return null;
        }
    }

    private AgendaMensuelDto findOrCreateAgenda(Long medecinId, LocalDate date) {
        try {
            List<AgendaMensuelDto> list = agendaController.getAllAgendas();
            if (list != null) {
                for (AgendaMensuelDto a : list) {
                    if (a != null && medecinId.equals(a.getMedecinId())
                            && a.getAnnee() == date.getYear()
                            && a.getMois() == toMois(date.getMonthValue())) {
                        return a;
                    }
                }
            }
            AgendaMensuelDto dto = AgendaMensuelDto.builder()
                    .medecinId(medecinId)
                    .mois(toMois(date.getMonthValue()))
                    .annee(date.getYear())
                    .build();
            return agendaController.createAgenda(dto);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Mois toMois(int month) {
        return switch (month) {
            case 1 -> Mois.JANVIER;
            case 2 -> Mois.FEVRIER;
            case 3 -> Mois.MARS;
            case 4 -> Mois.AVRIL;
            case 5 -> Mois.MAI;
            case 6 -> Mois.JUIN;
            case 7 -> Mois.JUILLET;
            case 8 -> Mois.AOUT;
            case 9 -> Mois.SEPTEMBRE;
            case 10 -> Mois.OCTOBRE;
            case 11 -> Mois.NOVEMBRE;
            default -> Mois.DECEMBRE;
        };
    }

    private void ensureDefaultPlages(Long detailId, LocalTime start, LocalTime end) {
        try {
            List<PlageHoraire> exist = agendaController.getPlagesByDetailJournee(detailId);
            if (exist != null && !exist.isEmpty()) return;
            LocalTime t = start;
            while (t.isBefore(end)) {
                LocalTime t2 = t.plusMinutes(30);
                if (t2.isAfter(end)) break;
                PlageHoraire p = new PlageHoraire();
                p.setDetailJourneeId(detailId);
                p.setHeureDebut(t);
                p.setHeureFin(t2);
                p.setDisponible(true);
                agendaController.createPlage(p);
                t = t2;
            }
        } catch (Exception ignored) {}
    }

    private static class ProgramDialog extends JDialog {
        static class Result {
            Long patientId;
            Long medecinId;
            LocalDate date;
            LocalTime heure;
            String motif;
            Long plageId;
        }

        private Result result;

        static Result open(Component parent, Long defaultMedecinId, LocalDate date, List<PlageHoraire> libres) {
            ProgramDialog d = new ProgramDialog(SwingUtilities.getWindowAncestor(parent), defaultMedecinId, date, libres);
            d.setVisible(true);
            return d.result;
        }

        ProgramDialog(Window owner, Long defaultMedecinId, LocalDate date, List<PlageHoraire> libres) {
            super(owner, "Programmer un rendez-vous", ModalityType.APPLICATION_MODAL);
            setLayout(new BorderLayout(10, 10));

            JComboBox<PatientItem> cbPatient = new JComboBox<>(loadPatients());
            JComboBox<PlageItem> cbHeure = new JComboBox<>();
            JTextField tfDate = new JTextField(date.toString());
            JTextField tfMotif = new JTextField("RDV");

            tfDate.setEnabled(false);

            for (PlageHoraire p : libres) {
                cbHeure.addItem(new PlageItem(p.getId(), p.getHeureDebut(), p.getHeureFin()));
            }

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Patient"));
            form.add(cbPatient);
            form.add(new JLabel("Date"));
            form.add(tfDate);
            form.add(new JLabel("Heure"));
            form.add(cbHeure);
            form.add(new JLabel("Motif"));
            form.add(tfMotif);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new DentalButton("Annuler");
            JButton save = new DentalButton("Programmer");
            actions.add(cancel);
            actions.add(save);

            cancel.addActionListener(e -> {
                result = null;
                dispose();
            });
            save.addActionListener(e -> {
                PatientItem p = (PatientItem) cbPatient.getSelectedItem();
                PlageItem h = (PlageItem) cbHeure.getSelectedItem();
                if (p == null || p.id == null || h == null) {
                    JOptionPane.showMessageDialog(this, "Patient et heure obligatoires.");
                    return;
                }
                result = new Result();
                result.patientId = p.id;
                result.medecinId = defaultMedecinId;
                result.date = date;
                result.heure = h.heureDebut;
                result.motif = tfMotif.getText();
                result.plageId = h.plageId;
                dispose();
            });

            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            pack();
            setSize(520, 240);
            setLocationRelativeTo(owner);
        }
    }

    private static class PatientItem {
        final Long id;
        final String label;
        PatientItem(Long id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class PlageItem {
        final Long plageId;
        final LocalTime heureDebut;
        final LocalTime heureFin;
        PlageItem(Long plageId, LocalTime heureDebut, LocalTime heureFin) {
            this.plageId = plageId;
            this.heureDebut = heureDebut;
            this.heureFin = heureFin;
        }
        @Override public String toString() { return heureDebut + " - " + heureFin; }
    }

    private static DefaultComboBoxModel<PatientItem> loadPatients() {
        DefaultComboBoxModel<PatientItem> model = new DefaultComboBoxModel<>();
        model.addElement(new PatientItem(null, "-- Selectionner un patient --"));
        try {
            var repo = new ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl();
            var list = repo.findAll();
            if (list != null) {
                for (var p : list) {
                    String label = (p.getNom() + " " + p.getPrenom()).trim();
                    model.addElement(new PatientItem(p.getId(), label));
                }
            }
        } catch (Exception ignored) {}
        return model;
    }
}

