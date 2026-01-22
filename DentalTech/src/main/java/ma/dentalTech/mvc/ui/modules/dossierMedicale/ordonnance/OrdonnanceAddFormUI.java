package ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.dto.dossierMedicale.ordonnance.OrdonnanceDTO;
import ma.dentalTech.mvc.ui.common.DentalTheme;

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
 * Dialog modal pour ajouter une nouvelle ordonnance.
 * Selon la maquette fournie avec tableau de médicaments.
 */
public class OrdonnanceAddFormUI extends JDialog {

    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Champs du formulaire
    private final JComboBox<ConsultationComboItem> cbConsultation = new JComboBox<>();
    private final JComboBox<MedicamentComboItem> cbMedicament = new JComboBox<>();
    private final JTextField txtDate = new JTextField();

    // Tableau de médicaments
    private final JTable tableMedicaments = new JTable();
    private final MedicamentTableModel modelMedicaments = new MedicamentTableModel();

    private final JButton btnAddMedicament = new JButton("+ Ajouter médicament");
    private final JButton btnCancel = new JButton("Annuler");
    private final JButton btnCreate = new JButton("Enregistrer l'ordonnance");

    private boolean confirmed = false;

    /**
     * Item pour le combobox des consultations
     */
    public static class ConsultationComboItem {
        private final Long consultationId;
        private final String displayText;

        public ConsultationComboItem(Long consultationId, String displayText) {
            this.consultationId = consultationId;
            this.displayText = displayText;
        }

        public Long getConsultationId() {
            return consultationId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    /**
     * Item pour le combobox des médicaments
     */
    public static class MedicamentComboItem {
        private final Long medicamentId;
        private final String displayText;

        public MedicamentComboItem(Long medicamentId, String displayText) {
            this.medicamentId = medicamentId;
            this.displayText = displayText;
        }

        public Long getMedicamentId() {
            return medicamentId;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    /**
     * Item pour le tableau de médicaments
     */
    private static class MedicamentRow {
        @SuppressWarnings("unused")
        Long medicamentId; // Utilisé lors de la sauvegarde pour créer la prescription
        String medicamentNom;
        String quantite;
        String frequence;
        String duree;

        MedicamentRow(Long medicamentId, String medicamentNom, String quantite, String frequence, String duree) {
            this.medicamentId = medicamentId;
            this.medicamentNom = medicamentNom;
            this.quantite = quantite;
            this.frequence = frequence;
            this.duree = duree;
        }
    }

    public OrdonnanceAddFormUI(Frame parent, OrdonnanceController controller,
                                List<ConsultationComboItem> consultations,
                                List<MedicamentComboItem> medicaments,
                                String username) {
        super(parent, "Ajout d'une ordonnance", true);

        setSize(700, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Populate comboboxes
        cbConsultation.addItem(new ConsultationComboItem(null, "-- Sélectionner une consultation --"));
        for (ConsultationComboItem item : consultations) {
            cbConsultation.addItem(item);
        }

        cbMedicament.addItem(new MedicamentComboItem(null, "-- Sélectionner un médicament --"));
        for (MedicamentComboItem item : medicaments) {
            cbMedicament.addItem(item);
        }

        // Date par défaut = aujourd'hui
        txtDate.setText(LocalDate.now().format(DATE_INPUT_FORMATTER));

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(20, 20));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(DentalTheme.BG);

        content.add(buildForm(), BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);

        setContentPane(content);

        // Actions
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnAddMedicament.addActionListener(e -> addMedicamentToTable());

        btnCreate.addActionListener(e -> {
            if (validateAndCreate(controller, username)) {
                confirmed = true;
                dispose();
            }
        });
    }

    private JComponent buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Consultation
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        form.add(new JLabel("Consultation:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cbConsultation.setFont(DentalTheme.textFont(13));
        form.add(cbConsultation, gc);

        // Médicament (dropdown pour sélection)
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Médicament:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cbMedicament.setFont(DentalTheme.textFont(13));
        form.add(cbMedicament, gc);

        // Tableau de médicaments
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        form.add(buildMedicamentTable(), gc);

        // Bouton Ajouter médicament
        gc.gridx = 1;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.weighty = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        btnAddMedicament.setFont(DentalTheme.textBold(12));
        btnAddMedicament.setBackground(DentalTheme.CARD);
        btnAddMedicament.setForeground(DentalTheme.TEXT2);
        btnAddMedicament.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        form.add(btnAddMedicament, gc);

        // Date de l'ordonnance
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Date de l'ordonnance:"), gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtDate.setFont(DentalTheme.textFont(13));
        txtDate.setToolTipText("Format: yyyy-MM-dd (ex: 2024-04-20)");
        form.add(txtDate, gc);

        return form;
    }

    private JComponent buildMedicamentTable() {
        tableMedicaments.setModel(modelMedicaments);
        tableMedicaments.setRowHeight(35);
        tableMedicaments.setFont(DentalTheme.textFont(12));
        tableMedicaments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMedicaments.setGridColor(DentalTheme.BORDER);
        tableMedicaments.setShowGrid(true);

        // Colonnes
        tableMedicaments.getColumnModel().getColumn(0).setPreferredWidth(200); // Médicament
        tableMedicaments.getColumnModel().getColumn(1).setPreferredWidth(150); // Quantité
        tableMedicaments.getColumnModel().getColumn(2).setPreferredWidth(150); // Fréquence
        tableMedicaments.getColumnModel().getColumn(3).setPreferredWidth(100); // Durée
        tableMedicaments.getColumnModel().getColumn(4).setPreferredWidth(80);  // Actions

        // Renderer/Editor pour Actions (colonne 4)
        tableMedicaments.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JButton btn = new JButton("Supprimer");
                btn.setBackground(DentalTheme.CARD);
                btn.setForeground(DentalTheme.TEXT2);
                btn.setFocusPainted(false);
                btn.setMargin(new Insets(2, 6, 2, 6));
                return btn;
            }
        });
        tableMedicaments.getColumnModel().getColumn(4).setCellEditor(new MedicamentActionCellEditor());

        JScrollPane scroll = new JScrollPane(tableMedicaments);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scroll.setPreferredSize(new Dimension(0, 150));

        return scroll;
    }

    private JComponent buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        btnCancel.setFont(DentalTheme.textFont(13));
        btnCancel.setBackground(DentalTheme.BEIGE);
        btnCancel.setForeground(DentalTheme.TEXT2);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        btnCreate.setFont(DentalTheme.textBold(13));
        btnCreate.setBackground(DentalTheme.CARD);
        btnCreate.setForeground(DentalTheme.TEXT2);
        btnCreate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCreate.setFocusPainted(false);

        buttons.add(btnCancel);
        buttons.add(btnCreate);

        return buttons;
    }

    private void addMedicamentToTable() {
        MedicamentComboItem selected = (MedicamentComboItem) cbMedicament.getSelectedItem();
        if (selected == null || selected.getMedicamentId() == null) {
            showError("Veuillez sélectionner un médicament.");
            cbMedicament.requestFocus();
            return;
        }

        // Ajouter une ligne vide au tableau
        modelMedicaments.addRow(new MedicamentRow(
                selected.getMedicamentId(),
                selected.toString(),
                "", // quantite
                "", // frequence
                ""  // duree
        ));

        // Réinitialiser le combobox
        cbMedicament.setSelectedIndex(0);
    }

    private boolean validateAndCreate(OrdonnanceController controller, String username) {
        // Validation: Consultation
        ConsultationComboItem selectedConsultation = (ConsultationComboItem) cbConsultation.getSelectedItem();
        if (selectedConsultation == null || selectedConsultation.getConsultationId() == null) {
            showError("Veuillez sélectionner une consultation.");
            cbConsultation.requestFocus();
            return false;
        }

        // Validation: Date
        LocalDate date = null;
        String dateStr = txtDate.getText().trim();
        if (!dateStr.isEmpty()) {
            try {
                date = LocalDate.parse(dateStr, DATE_INPUT_FORMATTER);
            } catch (DateTimeParseException e) {
                showError("Date invalide. Format attendu: yyyy-MM-dd (ex: 2024-04-20)");
                txtDate.requestFocus();
                return false;
            }
        } else {
            date = LocalDate.now(); // Par défaut aujourd'hui
        }

        // Validation: Au moins un médicament
        if (modelMedicaments.getRowCount() == 0) {
            showError("Veuillez ajouter au moins un médicament à l'ordonnance.");
            return false;
        }

        // Validation des médicaments
        for (int i = 0; i < modelMedicaments.getRowCount(); i++) {
            MedicamentRow row = modelMedicaments.getRowAt(i);
            if (row.quantite == null || row.quantite.trim().isEmpty()) {
                showError("La quantité est obligatoire pour le médicament: " + row.medicamentNom);
                return false;
            }
            if (row.frequence == null || row.frequence.trim().isEmpty()) {
                showError("La fréquence est obligatoire pour le médicament: " + row.medicamentNom);
                return false;
            }
            if (row.duree == null || row.duree.trim().isEmpty()) {
                showError("La durée est obligatoire pour le médicament: " + row.medicamentNom);
                return false;
            }
            try {
                int d = Integer.parseInt(row.duree.trim());
                if (d <= 0) {
                    showError("La durée doit être un nombre positif pour: " + row.medicamentNom);
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Durée invalide pour: " + row.medicamentNom);
                return false;
            }
        }

        // Création de l'ordonnance
        try {
            // Récupérer le dossierId depuis la consultation (à implémenter si nécessaire)
            // Pour l'instant, on utilise null
            Long dossierId = null; // TODO: récupérer depuis la consultation

            OrdonnanceDTO ordonnance = new OrdonnanceDTO(
                    null, // id
                    dossierId,
                    selectedConsultation.getConsultationId(),
                    date
            );

            Long ordonnanceId = controller.create(ordonnance, username);
            JOptionPane.showMessageDialog(this,
                    "Ordonnance créée avec succès (ID: " + ordonnanceId + ")\n" +
                    "Note: Les prescriptions (médicaments) doivent être ajoutées séparément.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            showError("Erreur lors de la création: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // =========================================================
    // Table model pour médicaments
    // =========================================================
    private class MedicamentTableModel extends AbstractTableModel {
        private final String[] cols = {"Médicament", "Quantité", "Fréquence", "Durée (jours)", "Actions"};
        private final List<MedicamentRow> rows = new ArrayList<>();

        void addRow(MedicamentRow row) {
            rows.add(row);
            fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }

        void removeRow(int index) {
            if (index >= 0 && index < rows.size()) {
                rows.remove(index);
                fireTableRowsDeleted(index, index);
            }
        }

        MedicamentRow getRowAt(int index) {
            if (index >= 0 && index < rows.size()) {
                return rows.get(index);
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int col) {
            return cols[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col >= 1 && col <= 3; // Quantité, Fréquence, Durée sont éditables
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MedicamentRow row = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> row.medicamentNom;
                case 1 -> row.quantite;
                case 2 -> row.frequence;
                case 3 -> row.duree;
                case 4 -> "Supprimer";
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            MedicamentRow row = rows.get(rowIndex);
            String str = value != null ? value.toString() : "";
            switch (columnIndex) {
                case 1 -> row.quantite = str;
                case 2 -> row.frequence = str;
                case 3 -> row.duree = str;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    // =========================================================
    // Cell Editor pour Actions
    // =========================================================
    private class MedicamentActionCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {
        private int currentRow = -1;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            JButton btn = new JButton("Supprimer");
            btn.setBackground(DentalTheme.CARD);
            btn.setForeground(DentalTheme.TEXT2);
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(2, 6, 2, 6));
            btn.addActionListener(e -> {
                modelMedicaments.removeRow(currentRow);
                fireEditingStopped();
            });
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
