package ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListEnrichedItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListRequestDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface liste des dossiers médicaux selon la maquette.
 * Affiche : Nom (avec avatar, âge), Téléphone, Dernière consultation, Actions
 */
public class DossierMedicalListUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DossierMedicalController controller;
    private final PatientController patientController;
    private final Long medecinId; // null si on veut tous les dossiers
    private final String username;

    private final JTextField txtPatient = new JTextField(20);

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnAdd = new JButton("+ Nouveau dossier");

    private final JTable table = new JTable();
    private final DossierMedicalTableModel model = new DossierMedicalTableModel();

    public DossierMedicalListUI(DossierMedicalController controller, Long medecinId, String username) {
        this.controller = controller;
        this.medecinId = medecinId;
        this.username = username;
        
        // Récupérer le PatientController depuis ApplicationContext
        Object bean = ApplicationContext.getBean("patientController");
        if (bean instanceof PatientController pc) {
            this.patientController = pc;
        } else {
            throw new RuntimeException("patientController introuvable dans ApplicationContext");
        }

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

        // Titre
        JLabel title = new JLabel("DOSSIERS");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);
        wrap.add(title);
        wrap.add(Box.createVerticalStrut(12));

        // Recherche et boutons
        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Rechercher un patient:");
        searchPanel.add(lblSearch);
        txtPatient.setPreferredSize(new Dimension(250, 30));
        searchPanel.add(txtPatient);
        
        searchRow.add(searchPanel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        btnSearch.setFont(DentalTheme.textFont(13));
        btnSearch.setBackground(new Color(0x1C, 0x25, 0x41));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnSearch.setFocusPainted(false);
        UiStyles.stylePrimaryButton(btnSearch);
        styleReadableButton(btnSearch);

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
        
        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnAdd);
        
        searchRow.add(buttonsPanel, BorderLayout.EAST);

        wrap.add(searchRow);

        return wrap;
    }

    private JComponent buildTable() {
        table.setModel(model);
        UiStyles.styleTable(table);
        table.setRowHeight(60);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        // Colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(250); // Nom
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Téléphone
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Dernière consultation
        table.getColumnModel().getColumn(3).setPreferredWidth(300); // Actions

        // Renderer pour les actions
        table.getColumnModel().getColumn(3).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ActionsCellEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    private void wireActions() {
        btnSearch.addActionListener(e -> refresh());
        btnAdd.addActionListener(e -> onAddDossier());
    }

    private void onAddDossier() {
        DossierMedicalAddFormUI dialog = new DossierMedicalAddFormUI(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                patientController,
                username,
                medecinId
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }

    public void refresh() {
        try {
            DossierListRequestDTO request = new DossierListRequestDTO(
                    txtPatient.getText().trim().isEmpty() ? null : txtPatient.getText().trim(),
                    medecinId,
                    new PageRequestDTO(100, 0)
            );
            List<DossierListEnrichedItemDTO> list = controller.searchForList(request);
            model.setRows(list);
        } catch (Exception ex) {
            showError(ex);
        }
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
    private class DossierMedicalTableModel extends AbstractTableModel {
        private final String[] cols = {"Nom", "Téléphone", "Dernière consultation", "Actions"};
        private List<DossierListEnrichedItemDTO> rows = new ArrayList<>();

        void setRows(List<DossierListEnrichedItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        DossierListEnrichedItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 3; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DossierListEnrichedItemDTO r = rows.get(rowIndex);
            if (r == null) return "";
            return switch (columnIndex) {
                case 0 -> r.getPatientNomComplet() != null ? r.getPatientNomComplet() : "";
                case 1 -> r.getPatientTelephone() != null ? r.getPatientTelephone() : "";
                case 2 -> {
                    if (r.getDerniereConsultation() != null && r.getDerniereConsultationId() != null) {
                        yield "Consultation #" + r.getDerniereConsultationId() + " - " +
                                r.getDerniereConsultation().format(DATE_FMT);
                    }
                    yield "";
                }
                case 3 -> ""; // actions renderer
                default -> "";
            };
        }
    }

    // =========================================================
    // Renderers / Editors
    // =========================================================
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
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    private class ActionsCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {
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
                DossierListEnrichedItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(DossierMedicalListUI.this),
                        "Détails du dossier médical", true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLayout(new BorderLayout());

                DossierMedicalDetailUI detailUI = new DossierMedicalDetailUI(
                        controller,
                        r.getDossierId(),
                        () -> dialog.dispose()
                );
                dialog.add(detailUI, BorderLayout.CENTER);

                dialog.pack();
                dialog.setSize(1200, 800);
                dialog.setLocationRelativeTo(DossierMedicalListUI.this);
                dialog.setVisible(true);
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                DossierListEnrichedItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                // Récupérer le dossier pour modification
                try {
                    ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierDetailEnrichedDTO detail = 
                            controller.getDetail(r.getDossierId());
                    
                    DossierMedicalAddFormUI dialog = new DossierMedicalAddFormUI(
                            (Frame) SwingUtilities.getWindowAncestor(DossierMedicalListUI.this),
                            controller,
                            patientController,
                            username,
                            detail.dossier()
                    );
                    dialog.setVisible(true);
                    if (dialog.isConfirmed()) {
                        refresh();
                    }
                } catch (Exception ex) {
                    showError(ex);
                }
            });

            b3.addActionListener(e -> {
                stopCellEditing();
                DossierListEnrichedItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        DossierMedicalListUI.this,
                        "Supprimer le dossier de \"" + r.getPatientNomComplet() + "\" ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.delete(r.getDossierId(), username);
                    refresh();
                    JOptionPane.showMessageDialog(DossierMedicalListUI.this,
                            "Dossier supprimé avec succès",
                            "Succès",
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
        // Uniformiser tous les boutons sur le style bleu + bordure dorée
        UiStyles.styleSecondaryButton(b);
    }
}

