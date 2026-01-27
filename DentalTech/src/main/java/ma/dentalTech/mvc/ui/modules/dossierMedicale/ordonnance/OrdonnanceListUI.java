package ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.MedicamentController;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDetailDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceListRequestDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.entities.dossierMedical.Medicament;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface liste des ordonnances selon la maquette.
 * Affiche : Nom du patient, Date, Actions
 */
public class OrdonnanceListUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final OrdonnanceController controller;
    private final Long medecinId;
    private final String username;

    private final JTextField txtPatient = new JTextField(15);
    private final JTextField txtDateFrom = new JTextField(10); // yyyy-MM-dd
    private final JTextField txtDateTo = new JTextField(10);

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnReset = new JButton("Actualiser");
    private final JButton btnAdd = new JButton("+ Ajouter une ordonnance");

    private final JTable table = new JTable();
    private final OrdonnanceTableModel model = new OrdonnanceTableModel();

    public OrdonnanceListUI(OrdonnanceController controller, Long medecinId) {
        this(controller, medecinId, "medecin");
    }

    public OrdonnanceListUI(OrdonnanceController controller, Long medecinId, String username) {
        this.controller = controller;
        this.medecinId = medecinId;
        this.username = username;

        setLayout(new BorderLayout());
        setOpaque(false);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(12, 12));

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTable(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        wireActions();
        refresh();
    }

    private JComponent buildHeader() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        // Titre + Bouton Ajouter
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel title = new JLabel("Liste des ordonnances");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);
        titleRow.add(title, BorderLayout.WEST);

        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(new Color(0x1C, 0x25, 0x41));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.setFocusPainted(false);
        UiStyles.stylePrimaryButton(btnAdd);
        styleReadableButton(btnAdd);
        btnAdd.addActionListener(e -> onAddOrdonnance());
        titleRow.add(btnAdd, BorderLayout.EAST);

        wrap.add(titleRow);
        wrap.add(Box.createVerticalStrut(12));

        // Filtres
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filters.setOpaque(false);

        filters.add(new JLabel("Patient:"));
        filters.add(txtPatient);

        filters.add(Box.createHorizontalStrut(10));
        filters.add(new JLabel("Date:"));
        filters.add(txtDateFrom);
        filters.add(new JLabel("a"));
        filters.add(txtDateTo);

        filters.add(Box.createHorizontalStrut(10));
        UiStyles.styleSecondaryButton(btnSearch);
        styleReadableButton(btnSearch);
        UiStyles.styleSecondaryButton(btnReset);
        styleReadableButton(btnReset);
        filters.add(btnSearch);
        filters.add(btnReset);

        wrap.add(filters);

        return wrap;
    }

    private JComponent buildTable() {
        table.setModel(model);
        UiStyles.styleTable(table);
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);

        table.getColumnModel().getColumn(2).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ActionsCellEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    private void wireActions() {
        btnReset.addActionListener(e -> {
            txtPatient.setText("");
            txtDateFrom.setText("");
            txtDateTo.setText("");
            refresh();
        });

        btnSearch.addActionListener(e -> refresh());
    }

    private Frame getParentFrame() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner instanceof Frame f) return f;
        if (owner instanceof Dialog d && d.getOwner() instanceof Frame f) return f;
        return null;
    }

    private void onAddOrdonnance() {
        List<OrdonnanceAddFormUI.ConsultationComboItem> consultations = loadConsultations();
        List<OrdonnanceAddFormUI.MedicamentComboItem> medicaments = loadMedicaments();
        OrdonnanceAddFormUI dialog = new OrdonnanceAddFormUI(
                getParentFrame(),
                controller,
                consultations,
                medicaments,
                username
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }

    public void refresh() {
        try {
            OrdonnanceListRequestDTO req = buildRequestFromUI();
            List<OrdonnanceListItemDTO> list = controller.searchForList(req);
            model.setRows(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private OrdonnanceListRequestDTO buildRequestFromUI() {
        OrdonnanceListRequestDTO req = new OrdonnanceListRequestDTO();
        req.setMedecinId(medecinId);

        String kw = txtPatient.getText();
        if (!isBlank(kw)) req.setPatientKeyword(kw.trim());

        String d1 = txtDateFrom.getText();
        if (!isBlank(d1)) {
            try {
                req.setDateFrom(LocalDate.parse(d1.trim()));
            } catch (DateTimeParseException e) {
                // ignore
            }
        }

        String d2 = txtDateTo.getText();
        if (!isBlank(d2)) {
            try {
                req.setDateTo(LocalDate.parse(d2.trim()));
            } catch (DateTimeParseException e) {
                // ignore
            }
        }

        return req;
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // =========================================================
    // Table model
    // =========================================================
    private class OrdonnanceTableModel extends AbstractTableModel {
        private final String[] cols = {"Nom du patient", "Date", "Actions"};
        private List<OrdonnanceListItemDTO> rows = new ArrayList<>();

        void setRows(List<OrdonnanceListItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        OrdonnanceListItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 2; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            OrdonnanceListItemDTO r = rows.get(rowIndex);
            if (r == null) return "";
            return switch (columnIndex) {
                case 0 -> r.getPatientNomComplet() == null ? "" : r.getPatientNomComplet();
                case 1 -> r.getDate() == null ? "" : r.getDate().format(DATE_FMT);
                case 2 -> "";
                default -> "";
            };
        }
    }

    private class ActionsCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Consulter");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");

        ActionsCellRenderer() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3)) {
                b.setFocusable(false);
                b.setMargin(new Insets(2, 6, 2, 6));
                styleReadableButton(b);
                panel.add(b);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
    }

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Consulter");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");

        private int currentRow = -1;

        ActionsCellEditor() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3)) {
                b.setFocusable(false);
                b.setMargin(new Insets(2, 6, 2, 6));
                styleReadableButton(b);
                panel.add(b);
            }

            b1.addActionListener(e -> {
                stopCellEditing();
                int row = (currentRow >= 0) ? currentRow : table.getSelectedRow();
                OrdonnanceListItemDTO r = model.getAt(row);
                if (r == null || r.getOrdonnanceId() == null) {
                    JOptionPane.showMessageDialog(OrdonnanceListUI.this,
                            "Ordonnance introuvable",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Window owner = SwingUtilities.getWindowAncestor(OrdonnanceListUI.this);
                    JDialog dialog = new JDialog(owner, "Consultation de l'ordonnance", Dialog.ModalityType.APPLICATION_MODAL);
                    OrdonnanceDetailUI detailUI = new OrdonnanceDetailUI(controller, r.getOrdonnanceId(), dialog::dispose);
                    dialog.setContentPane(detailUI);
                    dialog.setSize(900, 700);
                    dialog.setLocationRelativeTo(OrdonnanceListUI.this);
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    showError(ex);
                }
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                int row = (currentRow >= 0) ? currentRow : table.getSelectedRow();
                OrdonnanceListItemDTO r = model.getAt(row);
                if (r == null || r.getOrdonnanceId() == null) return;

                try {
                    OrdonnanceDTO ordonnanceDTO = controller.getById(r.getOrdonnanceId());
                    if (ordonnanceDTO == null) {
                        JOptionPane.showMessageDialog(OrdonnanceListUI.this,
                                "Ordonnance introuvable",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    List<OrdonnanceAddFormUI.ConsultationComboItem> consultations = loadConsultations();
                    List<OrdonnanceAddFormUI.MedicamentComboItem> medicaments = loadMedicaments();

                    OrdonnanceAddFormUI editDialog = new OrdonnanceAddFormUI(
                            getParentFrame(),
                            controller,
                            consultations,
                            medicaments,
                            username,
                            ordonnanceDTO
                    );
                    try {
                        OrdonnanceDetailDTO detail = controller.getDetail(r.getOrdonnanceId());
                        if (detail != null) {
                            editDialog.setReadOnlyPrescriptions(detail.getPrescriptions());
                        }
                    } catch (Exception ignored) {}
                    editDialog.setVisible(true);
                    if (editDialog.isConfirmed()) {
                        refresh();
                    }
                } catch (Exception ex) {
                    showError(ex);
                }
            });

            b3.addActionListener(e -> {
                stopCellEditing();
                OrdonnanceListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        OrdonnanceListUI.this,
                        "Supprimer l'ordonnance #" + r.getOrdonnanceId() + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.delete(r.getOrdonnanceId());
                    refresh();
                    JOptionPane.showMessageDialog(OrdonnanceListUI.this,
                            "Ordonnance supprimee avec succes",
                            "Succes",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showError(ex);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void styleReadableButton(AbstractButton b) {
        if (b == null) return;
        b.setForeground(DentalTheme.TEXT2);
        b.setBackground(DentalTheme.CARD);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private List<OrdonnanceAddFormUI.ConsultationComboItem> loadConsultations() {
        List<OrdonnanceAddFormUI.ConsultationComboItem> out = new ArrayList<>();
        try {
            Object bean = ApplicationContext.getBean("consultationController");
            if (!(bean instanceof ConsultationController consultationController)) {
                return out;
            }
            ConsultationListRequestDTO req = new ConsultationListRequestDTO();
            req.setMedecinId(medecinId);
            List<ConsultationListItemDTO> list = consultationController.searchForList(req);
            if (list != null) {
                for (ConsultationListItemDTO c : list) {
                    if (c == null || c.getConsultationId() == null) continue;
                    String date = (c.getDateConsultation() != null)
                            ? c.getDateConsultation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            : "";
                    String label = (c.getPatientNomComplet() != null ? c.getPatientNomComplet() : "Patient")
                            + " - " + date;
                    out.add(new OrdonnanceAddFormUI.ConsultationComboItem(c.getConsultationId(), label.trim()));
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private List<OrdonnanceAddFormUI.MedicamentComboItem> loadMedicaments() {
        List<OrdonnanceAddFormUI.MedicamentComboItem> out = new ArrayList<>();
        try {
            MedicamentController ctrl = ApplicationContext.getBean(MedicamentController.class);
            if (ctrl == null) return out;
            List<Medicament> meds = ctrl.getAll();
            if (meds != null) {
                for (Medicament m : meds) {
                    if (m == null || m.getId() == null) continue;
                    String label = m.getNom() != null ? m.getNom() : ("Medicament #" + m.getId());
                    if (!isBlank(m.getForme())) {
                        label += " (" + m.getForme() + ")";
                    }
                    out.add(new OrdonnanceAddFormUI.MedicamentComboItem(m.getId(), label));
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
