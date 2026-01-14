package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class RdvPagePanel extends JPanel {

    private final RdvController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Date", "Heure", "Motif", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);

    // filtres style maquette
    private final PillButton bAll = new PillButton("Tous");
    private final PillButton bToday = new PillButton("Aujourd'hui");
    private final PillButton bUpcoming = new PillButton("À venir");

    // actions
    private final DentalButton btnAdd = new DentalButton("Ajouter");
    private final DentalButton btnEdit = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");

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

        // initial
        setFilterSelected(bAll);
        refresh(safe(() -> controller.getAll()));
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

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        results.add(sp, BorderLayout.CENTER);

        return results;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

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

        // ✅ AJOUTER
        btnAdd.addActionListener(e -> {
            try {
                ensureController();

                RdvFormDialog dlg = new RdvFormDialog(
                        SwingUtilities.getWindowAncestor(this),
                        null
                );
                dlg.setVisible(true);

                RdvDto dto = dlg.getResult();
                if (dto == null) return;

                controller.create(dto);
                refreshCurrentFilter();

            } catch (Exception ex) {
                showRootError(this, ex, "Erreur RDV");
            }
        });

        // ✅ MODIFIER
        btnEdit.addActionListener(e -> {
            try {
                ensureController();

                Long id = selectedId();
                if (id == null) return;

                RdvDto current = controller.getById(id);

                RdvFormDialog dlg = new RdvFormDialog(
                        SwingUtilities.getWindowAncestor(this),
                        current
                );
                dlg.setVisible(true);

                RdvDto dto = dlg.getResult();
                if (dto == null) return;

                controller.update(dto);
                refreshCurrentFilter();

            } catch (Exception ex) {
                showRootError(this, ex, "Erreur RDV");
            }
        });

        // ✅ SUPPRIMER
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
                showRootError(this, ex, "Erreur RDV");
            }
        });
    }

    private void refreshCurrentFilter() {
        if (bToday.isPillSelected()) refresh(safe(() -> controller.getByDate(LocalDate.now())));
        else if (bUpcoming.isPillSelected()) refresh(safe(() -> controller.getUpcomingFromToday()));
        else refresh(safe(() -> controller.getAll()));
    }

    private void refresh(List<RdvDto> list) {
        model.setRowCount(0);
        if (list == null) return;

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
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne d’abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
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
            showRootError(this, ex, "Erreur RDV");
            return (T) Collections.emptyList();
        }
    }

    // ✅ affiche la vraie cause (SQLException, FK, parse date, etc.)
    private static void showRootError(Component parent, Throwable ex, String title) {
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();

        String msg = root.getMessage();
        if (msg == null || msg.isBlank()) msg = root.toString();

        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
        root.printStackTrace(); // utile en console/log
    }

    // ===== PillButton (sans override setSelected/isSelected)
    private static class PillButton extends JButton {
        private boolean pillSelected = false;

        PillButton(String text) {
            super(text);
            setFocusPainted(false);
            setFont(DentalTheme.textBold(12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                    BorderFactory.createEmptyBorder(7, 16, 7, 16)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPillSelected(false);
        }

        void setPillSelected(boolean v) {
            pillSelected = v;

            if (pillSelected) {
                setBackground(DentalTheme.PRIMARY_DARK);
                setForeground(Color.WHITE);
            } else {
                setBackground(DentalTheme.BG);
                setForeground(DentalTheme.TEXT2);
            }
            setOpaque(true);
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
    }

    // =========================================================
    // ✅ Dialog Ajouter / Modifier RDV
    // =========================================================
    private static class RdvFormDialog extends JDialog {

        private final JTextField tfId = new JTextField();
        private final JTextField tfPatientId = new JTextField();
        private final JTextField tfDetailJourneeId = new JTextField();
        private final JTextField tfListeAttenteId = new JTextField();
        private final JTextField tfDate = new JTextField();   // yyyy-MM-dd
        private final JTextField tfHeure = new JTextField();  // HH:mm
        private final JTextField tfMotif = new JTextField();
        private final JTextField tfNote = new JTextField();
        private final JComboBox<EtatRendezVous> cbStatut = new JComboBox<>(EtatRendezVous.values());

        private RdvDto result;

        RdvFormDialog(Window owner, RdvDto initial) {
            super(owner, (initial == null ? "Ajouter RDV" : "Modifier RDV"), ModalityType.APPLICATION_MODAL);

            setSize(520, 420);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(12, 12));

            tfId.setEnabled(false);

            add(buildForm(), BorderLayout.CENTER);
            add(buildActions(), BorderLayout.SOUTH);

            if (initial != null) fill(initial);
        }

        RdvDto getResult() {
            return result;
        }

        private JComponent buildForm() {
            JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            p.add(new JLabel("ID (auto)"));
            p.add(tfId);

            p.add(new JLabel("Patient ID *"));
            p.add(tfPatientId);

            p.add(new JLabel("DetailJournee ID *"));
            p.add(tfDetailJourneeId);

            p.add(new JLabel("ListeAttente ID (optionnel)"));
            p.add(tfListeAttenteId);

            p.add(new JLabel("Date RDV (yyyy-MM-dd) *"));
            p.add(tfDate);

            p.add(new JLabel("Heure (HH:mm) *"));
            p.add(tfHeure);

            p.add(new JLabel("Motif *"));
            p.add(tfMotif);

            p.add(new JLabel("Statut"));
            p.add(cbStatut);

            p.add(new JLabel("Note médecin (optionnel)"));
            p.add(tfNote);

            return p;
        }

        private JComponent buildActions() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton btnCancel = new JButton("Annuler");
            JButton btnOk = new JButton("Enregistrer");

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

        private void fill(RdvDto d) {
            if (d.getId() != null) tfId.setText(String.valueOf(d.getId()));
            if (d.getPatientId() != null) tfPatientId.setText(String.valueOf(d.getPatientId()));
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
            Long patientId = parseLongRequired(tfPatientId.getText(), "patientId obligatoire");
            Long detailJourneeId = parseLongRequired(tfDetailJourneeId.getText(), "detailJourneeId obligatoire");
            Long listeAttenteId = parseLongOrNull(tfListeAttenteId.getText());

            String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();
            String heureStr = tfHeure.getText() == null ? "" : tfHeure.getText().trim();
            String motif = tfMotif.getText() == null ? "" : tfMotif.getText().trim();
            String note = tfNote.getText() == null ? "" : tfNote.getText().trim();

            if (dateStr.isBlank()) throw new IllegalArgumentException("dateRdv obligatoire (yyyy-MM-dd)");
            if (heureStr.isBlank()) throw new IllegalArgumentException("heure obligatoire (HH:mm)");
            if (motif.isBlank()) throw new IllegalArgumentException("motif obligatoire");

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heure = LocalTime.parse(heureStr);

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
    }
}
