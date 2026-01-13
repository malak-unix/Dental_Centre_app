package ma.dentalTech.mvc.ui.modules.dossierMedicale.acte;

import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeListItemDTO;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface liste des actes selon la maquette.
 * Affiche : Libellé, Catégorie, Prix, Description, Actions
 */
public class ActeListUI extends JPanel {

    private final ActeController controller;
    private final String username;

    private final JTextField txtCategorie = new JTextField(15);
    private final JTextField txtKeyword = new JTextField(20);

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnReset = new JButton("Actualiser");
    private final JButton btnAdd = new JButton("+ Ajouter un acte");

    private final JTable table = new JTable();
    private final ActeTableModel model = new ActeTableModel();

    public ActeListUI(ActeController controller) {
        this(controller, "admin");
    }

    public ActeListUI(ActeController controller, String username) {
        this.controller = controller;
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

        JLabel title = new JLabel("Liste des actes");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);
        titleRow.add(title, BorderLayout.WEST);

        btnAdd.setFont(DentalTheme.textBold(13));
        btnAdd.setBackground(new Color(0x1C, 0x25, 0x41)); // Bleu foncé selon maquette
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 2), // Bordure dorée
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> onAddActe());
        titleRow.add(btnAdd, BorderLayout.EAST);

        wrap.add(titleRow);
        wrap.add(Box.createVerticalStrut(12));

        // Filtres
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filters.setOpaque(false);

        filters.add(new JLabel("Catégorie:"));
        filters.add(txtCategorie);

        filters.add(Box.createHorizontalStrut(10));
        filters.add(new JLabel("Recherche:"));
        filters.add(txtKeyword);

        filters.add(Box.createHorizontalStrut(10));
        filters.add(btnSearch);
        filters.add(btnReset);

        wrap.add(filters);

        return wrap;
    }

    private JComponent buildTable() {
        table.setModel(model);
        table.setRowHeight(40);
        table.setFont(DentalTheme.textFont(13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(DentalTheme.BORDER);
        table.setShowGrid(true);

        // Colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(250); // Libellé
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Catégorie
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Prix
        table.getColumnModel().getColumn(3).setPreferredWidth(300); // Description
        table.getColumnModel().getColumn(4).setPreferredWidth(300); // Actions

        // Renderer pour les actions
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionsCellEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    private void wireActions() {
        btnReset.addActionListener(e -> {
            txtCategorie.setText("");
            txtKeyword.setText("");
            refresh();
        });

        btnSearch.addActionListener(e -> refresh());
    }

    private void onAddActe() {
        ActeAddFormUI dialog = new ActeAddFormUI(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                username
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }

    public void refresh() {
        try {
            String categorie = txtCategorie.getText().trim();
            String keyword = txtKeyword.getText().trim();
            
            List<ActeListItemDTO> list;
            if (!categorie.isEmpty() || !keyword.isEmpty()) {
                list = controller.search(
                    categorie.isEmpty() ? null : categorie,
                    keyword.isEmpty() ? null : keyword
                );
            } else {
                list = controller.findAll();
            }
            
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
    private class ActeTableModel extends AbstractTableModel {
        private final String[] cols = {"Libellé", "Catégorie", "Prix", "Description", "Actions"};
        private List<ActeListItemDTO> rows = new ArrayList<>();

        void setRows(List<ActeListItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        ActeListItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 4; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ActeListItemDTO r = rows.get(rowIndex);
            if (r == null) return "";
            return switch (columnIndex) {
                case 0 -> r.getLibelle() == null ? "" : r.getLibelle();
                case 1 -> r.getCategorie() == null ? "" : r.getCategorie();
                case 2 -> r.getPrixBase() == null ? "0.00 €" : String.format("%.2f €", r.getPrixBase());
                case 3 -> r.getDescription() == null ? "" : r.getDescription();
                case 4 -> ""; // actions renderer
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
            b2.setForeground(Color.WHITE);
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
                ActeListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                ActeDTO acte = controller.getById(r.getActeId());
                JOptionPane.showMessageDialog(ActeListUI.this,
                        "Acte: " + acte.libelle() + "\n" +
                        "Catégorie: " + (acte.categorie() != null ? acte.categorie() : "") + "\n" +
                        "Prix: " + (acte.prixBase() != null ? String.format("%.2f €", acte.prixBase()) : "0.00 €") + "\n" +
                        "Description: " + (acte.description() != null ? acte.description() : ""),
                        "Détails de l'acte",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                ActeListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                ActeDTO acte = controller.getById(r.getActeId());
                ActeAddFormUI dialog = new ActeAddFormUI(
                        (Frame) SwingUtilities.getWindowAncestor(ActeListUI.this),
                        controller,
                        username,
                        acte
                );
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    refresh();
                }
            });

            b3.addActionListener(e -> {
                stopCellEditing();
                ActeListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        ActeListUI.this,
                        "Supprimer l'acte \"" + r.getLibelle() + "\" ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.delete(r.getActeId());
                    refresh();
                    JOptionPane.showMessageDialog(ActeListUI.this,
                            "Acte supprimé avec succès",
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
}
