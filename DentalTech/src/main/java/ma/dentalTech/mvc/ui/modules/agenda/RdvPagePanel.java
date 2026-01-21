package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class RdvPagePanel extends JPanel {

    private final RdvController controller;
    private Long selectedMedecinId = null;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Date", "Heure", "Motif", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JLabel emptyLabel = new JLabel("Aucun rendez-vous.");

    private final PillButton bAll = new PillButton("Tous");
    private final PillButton bToday = new PillButton("Aujourd'hui");
    private final PillButton bUpcoming = new PillButton("A venir");

    private final DentalButton btnAdd = new DentalButton("Ajouter");
    private final DentalButton btnEdit = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");
    private final DentalButton btnConfirm = new DentalButton("Confirmer");
    private final DentalButton btnCancel = new DentalButton("Annuler");

    public RdvPagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (RdvController) ApplicationContext.getBean("rdv.controller");

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        add(card, BorderLayout.CENTER);

        card.add(buildTop(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildBottom(), BorderLayout.SOUTH);

        wireActions();

        setFilterSelected(bAll);
        refresh(safe(() -> controller.getAll()));
    }

    public void setMedecinId(Long medecinId) {
        this.selectedMedecinId = medecinId;
    }

    private JComponent buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Rendez-vous");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        filters.add(bAll);
        filters.add(bToday);
        filters.add(bUpcoming);

        top.add(title);
        top.add(Box.createVerticalStrut(8));
        top.add(filters);

        return top;
    }

    private JComponent buildCenter() {
        CardPanel results = new CardPanel((String) null);
        results.setLayout(new BorderLayout());

        UiStyles.styleTable(table);
        table.setRowHeight(28);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        results.add(sp, BorderLayout.CENTER);

        emptyLabel.setFont(DentalTheme.textFont(12));
        emptyLabel.setForeground(DentalTheme.MUTED);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        results.add(emptyLabel, BorderLayout.SOUTH);

        return results;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnConfirm);
        bottom.add(btnCancel);

        return bottom;
    }

    private void wireActions() {
        bAll.addActionListener(e -> {
            setFilterSelected(bAll);
            refresh(safe(() -> controller.getAll()));
        });

        bToday.addActionListener(e -> {
            setFilterSelected(bToday);
            refresh(safe(() -> controller.getByDate(LocalDate.now())));
        });

        bUpcoming.addActionListener(e -> {
            setFilterSelected(bUpcoming);
            refresh(safe(() -> controller.getUpcomingFromToday()));
        });

        btnAdd.addActionListener(e -> openNewRdvDialog());

        btnEdit.addActionListener(e -> {
            try {
                ensureController();

                Long id = selectedId();
                if (id == null) return;

                RdvDto current = controller.getById(id);

                RdvFormDialog dlg = new RdvFormDialog(
                        SwingUtilities.getWindowAncestor(this),
                        current,
                        selectedMedecinId
                );
                dlg.setVisible(true);

                RdvDto dto = dlg.getResult();
                if (dto == null) return;

                controller.update(dto);
                refreshCurrentFilter();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                ensureController();

                Long id = selectedId();
                if (id == null) return;

                int ok = JOptionPane.showConfirmDialog(
                        this,
                        "Supprimer le RDV #" + id + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                controller.deleteById(id);
                refreshCurrentFilter();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnConfirm.addActionListener(e -> {
            try {
                ensureController();
                Long id = selectedId();
                if (id == null) return;

                EtatRendezVous st = selectedStatus();
                if (st == EtatRendezVous.PLANIFIE) {
                    controller.confirmer(id);
                } else if (st == EtatRendezVous.CONFIRME) {
                    controller.terminer(id);
                } else {
                    JOptionPane.showMessageDialog(this, "Action non disponible pour ce statut.");
                    return;
                }

                refreshCurrentFilter();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            try {
                ensureController();
                Long id = selectedId();
                if (id == null) return;
                controller.annuler(id);
                refreshCurrentFilter();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            updateActionButtons();
        });
    }

    private void refreshCurrentFilter() {
        if (bToday.isPillSelected()) refresh(safe(() -> controller.getByDate(LocalDate.now())));
        else if (bUpcoming.isPillSelected()) refresh(safe(() -> controller.getUpcomingFromToday()));
        else refresh(safe(() -> controller.getAll()));
    }

    public void openNewRdvDialog() {
        try {
            ensureController();

            RdvFormDialog dlg = new RdvFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    null,
                    selectedMedecinId
            );
            dlg.setVisible(true);

            RdvDto dto = dlg.getResult();
            if (dto == null) return;

            controller.create(dto);
            refreshCurrentFilter();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh(List<RdvDto> list) {
        model.setRowCount(0);
        if (list == null || list.isEmpty()) {
            emptyLabel.setVisible(true);
            updateActionButtons();
            return;
        }
        emptyLabel.setVisible(false);

        for (RdvDto r : list) {
            String patientAff = (r.getPatientNom() != null && !r.getPatientNom().isBlank())
                    ? r.getPatientNom()
                    : ("#" + r.getPatientId());

            model.addRow(new Object[]{
                    r.getId(),
                    patientAff,
                    r.getDateRdv(),
                    r.getHeure(),
                    r.getMotif(),
                    r.getStatut()
            });
        }
        updateActionButtons();
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selectionne une ligne d'abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    private EtatRendezVous selectedStatus() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object v = model.getValueAt(row, 5);
        if (v == null) return null;
        try {
            return EtatRendezVous.valueOf(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private void updateActionButtons() {
        EtatRendezVous st = selectedStatus();
        boolean has = st != null;
        btnConfirm.setEnabled(has && (st == EtatRendezVous.PLANIFIE || st == EtatRendezVous.CONFIRME));
        btnCancel.setEnabled(has && (st == EtatRendezVous.PLANIFIE || st == EtatRendezVous.CONFIRME));
    }

    private void ensureController() {
        if (controller == null) {
            throw new IllegalStateException("Bean rdv.controller introuvable (beans.properties + ApplicationContext)");
        }
    }

    private interface SupplierX<T> { T get() throws Exception; }

    @SuppressWarnings("unchecked")
    private <T> T safe(SupplierX<T> s) {
        try {
            ensureController();
            return s.get();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            return (T) Collections.emptyList();
        }
    }

    private static class PillButton extends JButton {
        private boolean pillSelected = false;

        PillButton(String text) {
            super(text);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPillSelected(false);
        }

        void setPillSelected(boolean v) {
            pillSelected = v;
            UiStyles.stylePillButton(this, pillSelected);
            repaint();
        }

        boolean isPillSelected() {
            return pillSelected;
        }
    }

    private void setFilterSelected(PillButton selected) {
        bAll.setPillSelected(selected == bAll);
        bToday.setPillSelected(selected == bToday);
        bUpcoming.setPillSelected(selected == bUpcoming);
        if (selected == bAll) {
            // Keep "Tous" text dark like the other filters
            UiStyles.stylePillButton(bAll, false);
        }
    }

    private static class RdvFormDialog extends JDialog {

        private final JTextField tfId = new JTextField();
        private final JTextField tfPatientId = new JTextField();
        private final JComboBox<PatientItem> cbPatient = new JComboBox<>(loadPatients());
        private final JComboBox<MedecinItem> cbMedecin = new JComboBox<>(loadMedecins());
        private final JTextField tfDetailJourneeId = new JTextField();
        private final JTextField tfListeAttenteId = new JTextField();
        private final JTextField tfDate = new JTextField();
        private final JTextField tfHeure = new JTextField();
        private final JTextField tfMotif = new JTextField();
        private final JTextField tfNote = new JTextField();
        private final JComboBox<EtatRendezVous> cbStatut = new JComboBox<>(EtatRendezVous.values());

        private RdvDto result;
        private final Long defaultMedecinId;

        RdvFormDialog(Window owner, RdvDto initial, Long defaultMedecinId) {
            super(owner, (initial == null ? "Ajouter RDV" : "Modifier RDV"), ModalityType.APPLICATION_MODAL);
            this.defaultMedecinId = defaultMedecinId;

            setSize(600, 520);
            setMinimumSize(new Dimension(560, 480));
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(12, 12));

            tfId.setEnabled(false);
            tfDetailJourneeId.setEditable(false);

            add(buildForm(), BorderLayout.CENTER);
            add(buildActions(), BorderLayout.SOUTH);

            if (defaultMedecinId != null) selectMedecinById(defaultMedecinId);
            if (initial != null) fill(initial);
            if (initial == null) {
                tfDate.setText(LocalDate.now().toString());
                tfHeure.setText("09:00");
                tfMotif.setText("Consultation");
            }
        }

        RdvDto getResult() {
            return result;
        }

        private JComponent buildForm() {
            CardPanel p = new CardPanel((String) null);
            p.setBackground(DentalTheme.CARD);
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            p.setLayout(new GridBagLayout());
            p.setPreferredSize(new Dimension(540, 420));

            styleField(tfId);
            styleField(cbPatient);
            styleField(cbMedecin);
            styleField(tfDetailJourneeId);
            styleField(tfListeAttenteId);
            styleField(tfDate);
            styleField(tfHeure);
            styleField(tfMotif);
            styleField(cbStatut);
            styleField(tfNote);

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;

            addFormRow(p, c, "ID (auto)", tfId);
            addFormRow(p, c, "Patient *", cbPatient);
            addFormRow(p, c, "Medecin *", cbMedecin);
            addFormRow(p, c, "DetailJournee (auto)", tfDetailJourneeId);
            addFormRow(p, c, "ListeAttente ID (optionnel)", tfListeAttenteId);
            addFormRow(p, c, "Date RDV (yyyy-MM-dd) *", tfDate);
            addFormRow(p, c, "Heure (HH:mm) *", tfHeure);
            addFormRow(p, c, "Motif *", tfMotif);
            addFormRow(p, c, "Statut", cbStatut);
            addFormRow(p, c, "Note medecin (optionnel)", tfNote);

            return p;
        }

        private JComponent buildActions() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton btnCancel = new DentalButton("Annuler");
            JButton btnOk = new DentalButton("Enregistrer");
            UiStyles.styleSecondaryButton(btnCancel);
            UiStyles.stylePrimaryButton(btnOk);

            btnCancel.addActionListener(e -> {
                result = null;
                dispose();
            });

            btnOk.addActionListener(e -> {
                try {
                    result = readDto();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
                }
            });

            p.add(btnCancel);
            p.add(btnOk);
            return p;
        }

        private void addFormRow(JPanel p, GridBagConstraints c, String label, JComponent field) {
            JLabel l = new JLabel(label);
            l.setFont(DentalTheme.textFont(12));
            l.setForeground(DentalTheme.TEXT2);

            c.gridx = 0;
            c.weightx = 0;
            p.add(l, c);

            c.gridx = 1;
            c.weightx = 1;
            p.add(field, c);

            c.gridy++;
        }

        private void styleField(JComponent field) {
            if (field == null) return;
            field.setFont(DentalTheme.textFont(12));
            if (field instanceof JTextField tf) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        UiStyles.roundedBorder(),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            } else if (field instanceof JComboBox<?> cb) {
                cb.setBorder(BorderFactory.createCompoundBorder(
                        UiStyles.roundedBorder(),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
        }

        private void selectPatientById(Long patientId) {
            if (patientId == null) return;
            for (int i = 0; i < cbPatient.getItemCount(); i++) {
                PatientItem item = cbPatient.getItemAt(i);
                if (item != null && patientId.equals(item.id)) {
                    cbPatient.setSelectedIndex(i);
                    return;
                }
            }
        }

        private void selectMedecinById(Long medecinId) {
            if (medecinId == null) return;
            for (int i = 0; i < cbMedecin.getItemCount(); i++) {
                MedecinItem item = cbMedecin.getItemAt(i);
                if (item != null && medecinId.equals(item.id)) {
                    cbMedecin.setSelectedIndex(i);
                    return;
                }
            }
        }

        private void fill(RdvDto d) {
            if (d.getId() != null) tfId.setText(String.valueOf(d.getId()));
            if (d.getPatientId() != null) {
                tfPatientId.setText(String.valueOf(d.getPatientId()));
                selectPatientById(d.getPatientId());
            }
            if (d.getDetailJourneeId() != null) tfDetailJourneeId.setText(String.valueOf(d.getDetailJourneeId()));
            if (d.getListeAttenteId() != null) tfListeAttenteId.setText(String.valueOf(d.getListeAttenteId()));
            if (d.getDateRdv() != null) tfDate.setText(d.getDateRdv().toString());
            if (d.getHeure() != null) tfHeure.setText(d.getHeure().toString());
            if (d.getMotif() != null) tfMotif.setText(d.getMotif());
            if (d.getStatut() != null) cbStatut.setSelectedItem(d.getStatut());
            if (d.getNoteMedecin() != null) tfNote.setText(d.getNoteMedecin());
        }

        private RdvDto readDto() {
            Long id = parseLongOrNull(tfId.getText());
            PatientItem p = (PatientItem) cbPatient.getSelectedItem();
            Long patientId = (p == null ? null : p.id);
            if (patientId == null) {
                patientId = parseLongOrNull(tfPatientId.getText());
            }
            if (patientId == null) {
                throw new IllegalArgumentException("patient obligatoire");
            }

            String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();
            String heureStr = tfHeure.getText() == null ? "" : tfHeure.getText().trim();
            String motif = tfMotif.getText() == null ? "" : tfMotif.getText().trim();
            String note = tfNote.getText() == null ? "" : tfNote.getText().trim();

            if (dateStr.isBlank()) throw new IllegalArgumentException("dateRdv obligatoire (yyyy-MM-dd)");
            if (heureStr.isBlank()) throw new IllegalArgumentException("heure obligatoire (HH:mm)");
            if (motif.isBlank()) throw new IllegalArgumentException("motif obligatoire");

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heure = LocalTime.parse(heureStr);

            Long detailJourneeId = parseLongOrNull(tfDetailJourneeId.getText());
            if (detailJourneeId != null && !detailJourneeExists(detailJourneeId)) {
                detailJourneeId = null;
            }
            if (detailJourneeId == null) {
                MedecinItem m = (MedecinItem) cbMedecin.getSelectedItem();
                Long medecinId = (m == null ? null : m.id);
                if (medecinId == null) medecinId = defaultMedecinId;
                if (medecinId == null) {
                    throw new IllegalArgumentException("medecin obligatoire");
                }
                detailJourneeId = resolveDetailJourneeId(medecinId, date);
            }
            if (detailJourneeId == null) {
                throw new IllegalArgumentException("Aucune journee definie pour cette date");
            }

            Long listeAttenteId = parseLongOrNull(tfListeAttenteId.getText());

            EtatRendezVous statut = (EtatRendezVous) cbStatut.getSelectedItem();
            if (statut == null) statut = EtatRendezVous.PLANIFIE;

            return RdvDto.builder()
                    .id(id)
                    .patientId(patientId)
                    .detailJourneeId(detailJourneeId)
                    .listeAttenteId(listeAttenteId)
                    .dateRdv(date)
                    .heure(heure)
                    .motif(motif)
                    .statut(statut)
                    .noteMedecin(note.isBlank() ? null : note)
                    .typeRdv(null)
                    .patientNom(null)
                    .build();
        }

        private Long resolveDetailJourneeId(Long medecinId, LocalDate date) {
            if (medecinId == null || date == null) return null;
            try {
                var agendaController = ma.dentalTech.configuration.ApplicationContext.getBean(
                        ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController.class);
                if (agendaController == null) return null;

                ma.dentalTech.mvc.dto.agenda.DetailJourneeDto dj =
                        agendaController.getDetailJourneeByMedecinAndDate(medecinId, date);
                if (dj != null && dj.getId() != null) return dj.getId();

                ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto agenda =
                        findOrCreateAgenda(agendaController, medecinId, date);
                if (agenda == null || agenda.getId() == null) return null;

                ma.dentalTech.mvc.dto.agenda.DetailJourneeDto created = ma.dentalTech.mvc.dto.agenda.DetailJourneeDto.builder()
                        .agendaId(agenda.getId())
                        .dateJour(date)
                        .heureDebutTravail(java.time.LocalTime.of(9, 0))
                        .heureFinTravail(java.time.LocalTime.of(17, 0))
                        .etatJour("OUVERT")
                        .commentaire("Auto-cree")
                        .build();

                created = agendaController.createDetailJournee(created);
                if (created == null || created.getId() == null) return null;

                ensureDefaultPlages(agendaController, created.getId(), java.time.LocalTime.of(9, 0), java.time.LocalTime.of(17, 0));
                return created.getId();
            } catch (Exception ignored) {
                return null;
            }
        }

        private boolean detailJourneeExists(Long detailId) {
            if (detailId == null) return false;
            try {
                var agendaController = ma.dentalTech.configuration.ApplicationContext.getBean(
                        ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController.class);
                if (agendaController == null) return false;
                return agendaController.getDetailJourneeById(detailId) != null;
            } catch (Exception ignored) {
                return false;
            }
        }

        private ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto findOrCreateAgenda(
                ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController agendaController,
                Long medecinId, LocalDate date) {
            try {
                java.util.List<ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto> list = agendaController.getAllAgendas();
                if (list != null) {
                    for (var a : list) {
                        if (a != null && medecinId.equals(a.getMedecinId()) &&
                                a.getAnnee() == date.getYear() &&
                                a.getMois() == toMois(date.getMonthValue())) {
                            return a;
                        }
                    }
                }

                var dto = ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto.builder()
                        .medecinId(medecinId)
                        .mois(toMois(date.getMonthValue()))
                        .annee(date.getYear())
                        .build();

                return agendaController.createAgenda(dto);
            } catch (Exception ignored) {
                return null;
            }
        }

        private ma.dentalTech.entities.enums.Mois toMois(int month) {
            return switch (month) {
                case 1 -> ma.dentalTech.entities.enums.Mois.JANVIER;
                case 2 -> ma.dentalTech.entities.enums.Mois.FEVRIER;
                case 3 -> ma.dentalTech.entities.enums.Mois.MARS;
                case 4 -> ma.dentalTech.entities.enums.Mois.AVRIL;
                case 5 -> ma.dentalTech.entities.enums.Mois.MAI;
                case 6 -> ma.dentalTech.entities.enums.Mois.JUIN;
                case 7 -> ma.dentalTech.entities.enums.Mois.JUILLET;
                case 8 -> ma.dentalTech.entities.enums.Mois.AOUT;
                case 9 -> ma.dentalTech.entities.enums.Mois.SEPTEMBRE;
                case 10 -> ma.dentalTech.entities.enums.Mois.OCTOBRE;
                case 11 -> ma.dentalTech.entities.enums.Mois.NOVEMBRE;
                default -> ma.dentalTech.entities.enums.Mois.DECEMBRE;
            };
        }

        private void ensureDefaultPlages(
                ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaController agendaController,
                Long detailId, java.time.LocalTime start, java.time.LocalTime end) {
            try {
                java.util.List<ma.dentalTech.entities.agenda.PlageHoraire> exist = agendaController.getPlagesByDetailJournee(detailId);
                if (exist != null && !exist.isEmpty()) return;

                java.time.LocalTime t = start;
                while (t.isBefore(end)) {
                    java.time.LocalTime t2 = t.plusMinutes(30);
                    if (t2.isAfter(end)) break;
                    ma.dentalTech.entities.agenda.PlageHoraire p = new ma.dentalTech.entities.agenda.PlageHoraire();
                    p.setDetailJourneeId(detailId);
                    p.setHeureDebut(t);
                    p.setHeureFin(t2);
                    p.setDisponible(true);
                    agendaController.createPlage(p);
                    t = t2;
                }
            } catch (Exception ignored) {}
        }

        private Long parseLongRequired(String s, String msg) {
            Long v = parseLongOrNull(s);
            if (v == null || v <= 0) throw new IllegalArgumentException(msg);
            return v;
        }

        private Long parseLongOrNull(String s) {
            if (s == null) return null;
            String t = s.trim();
            if (t.isBlank()) return null;
            return Long.parseLong(t);
        }
    
        private static class MedecinItem {
            final Long id;
            final String label;
            MedecinItem(Long id, String label) { this.id = id; this.label = label; }
            @Override public String toString() { return label; }
        }

        private static DefaultComboBoxModel<MedecinItem> loadMedecins() {
            DefaultComboBoxModel<MedecinItem> model = new DefaultComboBoxModel<>();
            model.addElement(new MedecinItem(null, "-- Selectionner un medecin --"));
            try {
                var repo = new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();
                var list = repo.findAll();
                if (list != null) {
                    for (var m : list) {
                        String label = ((m.getNom() == null ? "" : m.getNom()) + " " +
                                (m.getPrenom() == null ? "" : m.getPrenom())).trim();
                        if (label.isBlank()) label = "Medecin #" + m.getId();
                        model.addElement(new MedecinItem(m.getId(), label));
                    }
                }
            } catch (Exception ignored) {}
            return model;
        }

        private static class PatientItem {
            final Long id;
            final String label;
            PatientItem(Long id, String label) { this.id = id; this.label = label; }
            @Override public String toString() { return label; }
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
                        if (label.isBlank()) label = "Patient #" + p.getId();
                        model.addElement(new PatientItem(p.getId(), label));
                    }
                }
            } catch (Exception ignored) {}
            return model;
        }
}
}

