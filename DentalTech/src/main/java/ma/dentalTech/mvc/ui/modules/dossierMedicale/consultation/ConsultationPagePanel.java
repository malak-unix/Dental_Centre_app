package ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation;

import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Page Swing: Liste des consultations (filtre statut, date, patient) + actions.
 */
public class ConsultationPagePanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ConsultationController controller;
    private final Long medecinId;
    private final String username; // Username du medecin pour creer les consultations

    private final JTextField txtPatient = new JTextField();
    private final JTextField txtDate = new JTextField(); // yyyy-MM-dd
    private final JComboBox<Object> cbStatut = new JComboBox<>(new Object[]{
            "Tous",
            StatutConsultation.PLANIFIE,
            StatutConsultation.EN_COURS,
            StatutConsultation.TERMINE,
            StatutConsultation.ANNULE
    });

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnReset = new JButton("Actualiser");
    private final JButton btnAdd = new JButton("+ Ajouter une consultation");

    private final ConsultationTableModel model = new ConsultationTableModel();
    private final JTable table = new JTable(model);

    public ConsultationPagePanel(ConsultationController controller, Long medecinId) {
        this(controller, medecinId, "medecin"); // Default username
    }

    public ConsultationPagePanel(ConsultationController controller, Long medecinId, String username) {
        this.controller = controller;
        this.medecinId = medecinId;
        this.username = username != null ? username : "medecin";

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

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Mes consultations");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);

        styleOutlineButton(btnAdd);
        btnAdd.setPreferredSize(new Dimension(240, 40));
        btnAdd.addActionListener(e -> onAddConsultation());

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(btnAdd, BorderLayout.EAST);

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setOpaque(false);
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 10);
        gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.weightx = 0;
        filters.add(new JLabel("Patient:"), gc);
        gc.gridx = 1;
        gc.weightx = 0.6;
        txtPatient.setPreferredSize(new Dimension(220, 30));
        styleInput(txtPatient);
        filters.add(txtPatient, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        filters.add(new JLabel("Date (yyyy-MM-dd):"), gc);
        gc.gridx = 3;
        gc.weightx = 0.3;
        txtDate.setPreferredSize(new Dimension(140, 30));
        styleInput(txtDate);
        filters.add(txtDate, gc);

        gc.gridx = 4;
        gc.weightx = 0;
        filters.add(new JLabel("Statut:"), gc);
        gc.gridx = 5;
        gc.weightx = 0.2;
        cbStatut.setPreferredSize(new Dimension(140, 30));
        styleCombo(cbStatut);
        filters.add(cbStatut, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        styleOutlineButton(btnSearch);
        styleOutlineButton(btnReset);
        actions.add(btnSearch);
        actions.add(btnReset);

        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        line.add(filters, BorderLayout.CENTER);
        line.add(actions, BorderLayout.EAST);

        wrap.add(titlePanel);
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(line);
        return wrap;
    }


    private JComponent buildTable() {
        table.setRowHeight(38);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(DentalTheme.BORDER);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(0xF5, 0xE8, 0xD8));
        table.setFont(DentalTheme.textFont(13));
        table.getTableHeader().setFont(DentalTheme.textBold(13));
        table.getTableHeader().setBackground(DentalTheme.PANEL);
        table.getTableHeader().setForeground(DentalTheme.TEXT2);

        DefaultTableCellRenderer baseRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) return this;
                setBackground(row % 2 == 0 ? new Color(0xFB, 0xF6, 0xEF) : Color.WHITE);
                setForeground(DentalTheme.TEXT2);
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, baseRenderer);

        table.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionsCellEditor());
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(320);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        CardPanel card = new CardPanel("Resultats");
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void wireActions() {
        btnReset.addActionListener(e -> {
            txtPatient.setText("");
            txtDate.setText("");
            cbStatut.setSelectedIndex(0);
            refresh();
        });

        btnSearch.addActionListener(e -> refresh());
    }

    private void onAddConsultation() {
        // Pour l'instant, on utilise une liste vide pour les dossiers
        // TODO: Recuperer la liste reelle des dossiers medicaux pour le medecin
        java.util.List<ConsultationAddFormUI.DossierComboItem> dossiers = new ArrayList<>();

        Optional<ConsultationDTO> result = ConsultationAddFormUI.showDialog(this, dossiers);

        if (result.isPresent()) {
            try {
                ConsultationDTO consultation = result.get();
                Long consultationId = controller.create(consultation, username);
                JOptionPane.showMessageDialog(this,
                        "Consultation creee avec succes (ID: " + consultationId + ")",
                        "Succes",
                        JOptionPane.INFORMATION_MESSAGE);
                refresh(); // Rafraichir la liste
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    public void refresh() {
        try {
            ConsultationListRequestDTO req = buildRequestFromUI();
            List<ConsultationListItemDTO> list = controller.searchForList(req);
            model.setRows(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private ConsultationListRequestDTO buildRequestFromUI() {
        ConsultationListRequestDTO req = new ConsultationListRequestDTO();
        req.setMedecinId(medecinId);

        String kw = txtPatient.getText();
        if (kw != null && !kw.isBlank()) req.setPatientKeyword(kw.trim());

        Object st = cbStatut.getSelectedItem();
        if (st instanceof StatutConsultation s) req.setStatut(s);
        else req.setStatut(null);

        String ds = txtDate.getText();
        if (ds != null && !ds.isBlank()) {
            try {
                req.setDate(LocalDate.parse(ds.trim(), DATE_FMT));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Date invalide. Format attendu: yyyy-MM-dd");
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
    private class ConsultationTableModel extends AbstractTableModel {
        private final String[] cols = {"Patient", "Date", "Statut", "Facture", "Actions"};
        private List<ConsultationListItemDTO> rows = new ArrayList<>();

        void setRows(List<ConsultationListItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        ConsultationListItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 4; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ConsultationListItemDTO r = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getPatientNomComplet();
                case 1 -> r.getDateConsultation() == null ? "" : r.getDateConsultation().format(DATE_TIME_FMT);
                case 2 -> r.getStatut();
                case 3 -> r.getTotalFacture() == null ? "0,00 DH" : (r.getTotalFacture() + " DH");
                case 4 -> ""; // actions renderer
                default -> "";
            };
        }
    }

    // =========================================================
    // Renderers / Editors
    // =========================================================
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            StatutConsultation st = (value instanceof StatutConsultation s) ? s : null;
            setText(st == null ? "" : st.name());

            if (isSelected) {
                return this;
            }

            setOpaque(true);

            // Couleurs simples
            if (st == StatutConsultation.TERMINE) setBackground(new Color(0xD7, 0xF2, 0xD7));
            else if (st == StatutConsultation.EN_COURS) setBackground(new Color(0xD6, 0xE9, 0xFF));
            else if (st == StatutConsultation.ANNULE) setBackground(new Color(0xFF, 0xD6, 0xD6));
            else if (st == StatutConsultation.PLANIFIE) setBackground(new Color(0xFF, 0xF1, 0xCC));
            else setBackground(Color.WHITE);
            setForeground(DentalTheme.TEXT2);

            return this;
        }
    }


    private class ActionsCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Consulter");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");
        private final JButton b4 = new JButton("Facture");

        ActionsCellRenderer() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3, b4)) {
                styleTableActionButton(b);
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

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Consulter");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");
        private final JButton b4 = new JButton("Facture");

        private int currentRow = -1;

        ActionsCellEditor() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3, b4)) {
                styleTableActionButton(b);
                panel.add(b);
            }

            b1.addActionListener(e -> {
                stopCellEditing();
                ConsultationListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                // Ouvrir l'interface de consultation
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(ConsultationPagePanel.this),
                        "Details de la consultation", true);
                ConsultationDetailUI detailUI = new ConsultationDetailUI(controller, r.getConsultationId(), () -> dialog.dispose());
                dialog.setContentPane(detailUI);
                dialog.setSize(1000, 700);
                dialog.setLocationRelativeTo(ConsultationPagePanel.this);
                dialog.setVisible(true);
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                ConsultationListItemDTO r = model.getAt(currentRow);
                JOptionPane.showMessageDialog(ConsultationPagePanel.this,
                        "A brancher: modifier consultation id=" + (r == null ? null : r.getConsultationId()));
            });

            b4.addActionListener(e -> {
                stopCellEditing();
                ConsultationListItemDTO r = model.getAt(currentRow);
                JOptionPane.showMessageDialog(ConsultationPagePanel.this,
                        "A brancher: generer / ouvrir facture pour consultation id=" + (r == null ? null : r.getConsultationId()));
            });

            b3.addActionListener(e -> {
                stopCellEditing();
                ConsultationListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        ConsultationPagePanel.this,
                        "Supprimer la consultation #" + r.getConsultationId() + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.delete(r.getConsultationId());
                    refresh();
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


    private void styleInput(JTextField tf) {
        tf.setFont(DentalTheme.textFont(13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(DentalTheme.textFont(13));
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true));
    }

    private void styleOutlineButton(AbstractButton b) {
        b.setFont(DentalTheme.textBold(12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBackground(Color.WHITE);
        b.setForeground(DentalTheme.PRIMARY_DARK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
    }

    private void styleTableActionButton(AbstractButton b) {
        b.setFocusable(false);
        b.setFont(DentalTheme.textBold(11));
        b.setOpaque(true);
        b.setBackground(Color.WHITE);
        b.setForeground(DentalTheme.PRIMARY_DARK);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }
}
