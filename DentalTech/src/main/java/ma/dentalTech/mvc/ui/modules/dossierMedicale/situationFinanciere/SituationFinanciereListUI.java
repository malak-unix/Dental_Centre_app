package ma.dentalTech.mvc.ui.modules.dossierMedicale.situationFinanciere;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController;
import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere.SituationFinanciereListRequestDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

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
 * Interface liste des situations financières selon la maquette.
 * Affiche : Nom, Solde, Dernière facture, Prochain paiement, Actions
 */
public class SituationFinanciereListUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SituationFinanciereController controller;
    private final Long medecinId;
    private final String username;

    private final JTextField txtPatient = new JTextField(20);

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnReset = new JButton("Réinitialiser");

    private final JTable table = new JTable();
    private final SituationFinanciereTableModel model = new SituationFinanciereTableModel();
    private final JLabel statusLabel = new JLabel("");

    public SituationFinanciereListUI(SituationFinanciereController controller, Long medecinId, String username) {
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

        // Titre
        JLabel title = new JLabel("SITUATION FINANCIÈRE");
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

        btnReset.setFont(DentalTheme.textFont(13));
        btnReset.setBackground(new Color(0xCB, 0xA1, 0x35));
        btnReset.setForeground(new Color(0x1C, 0x25, 0x41));
        btnReset.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnReset.setFocusPainted(false);
        
        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnReset);
        
        searchRow.add(buttonsPanel, BorderLayout.EAST);

        wrap.add(searchRow);

        statusLabel.setFont(DentalTheme.textFont(12));
        statusLabel.setForeground(DentalTheme.MUTED);
        statusLabel.setBorder(new EmptyBorder(6, 2, 0, 0));
        wrap.add(statusLabel);

        return wrap;
    }

    private JComponent buildTable() {
        table.setModel(model);
        table.setRowHeight(50);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        // Colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Nom
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Solde
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Dernière facture
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Prochain paiement
        table.getColumnModel().getColumn(4).setPreferredWidth(250); // Actions

        // Renderer pour les actions
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionsCellEditor());

        // Renderer pour le solde (couleur verte si négatif)
        table.getColumnModel().getColumn(1).setCellRenderer(new SoldeCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    private void wireActions() {
        btnReset.addActionListener(e -> {
            txtPatient.setText("");
            refresh();
        });

        btnSearch.addActionListener(e -> refresh());
    }

    public void refresh() {
        try {
            SituationFinanciereListRequestDTO request = new SituationFinanciereListRequestDTO(
                    medecinId,
                    txtPatient.getText().trim().isEmpty() ? null : txtPatient.getText().trim(),
                    new PageRequestDTO(100, 0)
            );
            List<SituationFinanciereListItemDTO> list = controller.searchForList(request);
            model.setRows(list);
            statusLabel.setText("");
        } catch (Exception ex) {
            model.setRows(new ArrayList<>());
            statusLabel.setText("Situation financiere indisponible.");
        }
    }

    private void showError(Exception ex) {
        statusLabel.setText("Situation financiere indisponible.");
    }

    // =========================================================
    // Table model
    // =========================================================
    private class SituationFinanciereTableModel extends AbstractTableModel {
        private final String[] cols = {"Nom", "Solde", "Dernière facture", "Prochain paiement", "Actions"};
        private List<SituationFinanciereListItemDTO> rows = new ArrayList<>();

        void setRows(List<SituationFinanciereListItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        SituationFinanciereListItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 4; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SituationFinanciereListItemDTO r = rows.get(rowIndex);
            if (r == null) return "";
            return switch (columnIndex) {
                case 0 -> r.getPatientNomComplet() == null ? "" : r.getPatientNomComplet();
                case 1 -> r.getSolde() == null ? "0.00 €" : String.format("%.2f €", r.getSolde());
                case 2 -> r.getDerniereFacture() == null ? "" : r.getDerniereFacture();
                case 3 -> r.getProchainPaiement() == null ? "" : 
                        ("Le " + r.getProchainPaiement().format(DATE_FMT));
                case 4 -> ""; // actions renderer
                default -> "";
            };
        }
    }

    // =========================================================
    // Renderers / Editors
    // =========================================================
    private class SoldeCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            SituationFinanciereListItemDTO item = model.getAt(row);
            if (item != null && item.getSolde() != null && item.getSolde() < 0) {
                setForeground(new Color(0x00, 0x80, 0x00)); // Vert pour solde négatif (débit)
                setFont(DentalTheme.textBold(13));
            } else {
                setForeground(DentalTheme.TEXT2);
                setFont(DentalTheme.textFont(13));
            }
            
            return this;
        }
    }

    private class ActionsCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Lister");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");

        ActionsCellRenderer() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3)) {
                b.setFocusable(false);
                b.setMargin(new Insets(2, 6, 2, 6));
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
        private final JButton b1 = new JButton("Lister");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");

        private int currentRow = -1;

        ActionsCellEditor() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3)) {
                b.setFocusable(false);
                b.setMargin(new Insets(2, 6, 2, 6));
                panel.add(b);
            }

            // Style des boutons selon la maquette
            b1.setBackground(new Color(0x1C, 0x25, 0x41)); // Bleu foncé
            b1.setForeground(Color.WHITE);
            b1.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));

            b2.setBackground(new Color(0xCB, 0xA1, 0x35)); // Doré
            b2.setForeground(new Color(0x1C, 0x25, 0x41));
            b2.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));

            b3.setBackground(new Color(0xDC, 0x35, 0x45)); // Rouge
            b3.setForeground(Color.WHITE);
            b3.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xDC, 0x35, 0x45), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));

            b1.addActionListener(e -> {
                stopCellEditing();
                SituationFinanciereListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(SituationFinanciereListUI.this),
                        "Détails de la situation financière", true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLayout(new BorderLayout());

                SituationFinanciereDetailUI detailUI = new SituationFinanciereDetailUI(
                        controller,
                        r.getSituationFinanciereId(),
                        () -> dialog.dispose()
                );
                dialog.add(detailUI, BorderLayout.CENTER);

                dialog.pack();
                dialog.setSize(1000, 700);
                dialog.setLocationRelativeTo(SituationFinanciereListUI.this);
                dialog.setVisible(true);
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                SituationFinanciereListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                JOptionPane.showMessageDialog(SituationFinanciereListUI.this,
                        "Modification de la situation financière #" + r.getSituationFinanciereId() + "\n" +
                        "La situation financière est calculée automatiquement à partir des factures.\n" +
                        "Pour modifier, veuillez ajuster les factures ou utiliser la réinitialisation.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            b3.addActionListener(e -> {
                stopCellEditing();
                SituationFinanciereListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        SituationFinanciereListUI.this,
                        "Réinitialiser la situation financière pour \"" + r.getPatientNomComplet() + "\" ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.reset(r.getSituationFinanciereId(), username);
                    refresh();
                    JOptionPane.showMessageDialog(SituationFinanciereListUI.this,
                            "Situation financière réinitialisée avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    model.setRows(new ArrayList<>());
                    statusLabel.setText("Situation financiere indisponible.");
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
}

