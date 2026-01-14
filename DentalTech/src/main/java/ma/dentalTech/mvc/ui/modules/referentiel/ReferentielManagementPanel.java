package ma.dentalTech.mvc.ui.modules.referentiel;

import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.mvc.controllers.modules.referentiel.api.ReferentielController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.medicament.MedicamentDTO;
import ma.dentalTech.mvc.dto.referentiel.RefAntecedentDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReferentielManagementPanel extends JPanel {

    private final ReferentielController controller;
    private final JTabbedPane tabs = new JTabbedPane();

    // --- THEME COLORS & FONTS ---
    private static final Color BG_APP = new Color(240, 230, 214); // Warm Beige
    private static final Color BG_PANEL = new Color(255, 252, 245); // Light Cream
    private static final Color PRIMARY_DARK = new Color(22, 36, 56); // Deep Navy
    private static final Color ACCENT_GOLD = new Color(201, 166, 107); // Bronze/Gold
    private static final Color ACCENT_BUTTON_BG = new Color(22, 52, 92); // Richer Dental Blue
    private static final Color TEXT_DARK = new Color(60, 50, 40);

    private static final Font FONT_TITLE = new Font("Playfair Display", Font.BOLD, 26);
    private static final Font FONT_BODY = new Font("Poppins", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Poppins", Font.BOLD, 13);

    public ReferentielManagementPanel(ReferentielController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        setBackground(BG_APP);

        tabs.setFont(FONT_BUTTON);
        tabs.setBackground(BG_APP);
        tabs.setForeground(PRIMARY_DARK);

        tabs.addTab("Actes", buildActesPanel());
        tabs.addTab("Médicaments", buildMedicamentsPanel());
        tabs.addTab("Antécédents (Catalogue)", buildAntecedentsPanel());
        tabs.addTab("Assurances", buildAssurancesPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // --- ACTES ---
    private JPanel buildActesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(ACCENT_GOLD, 1, true)));

        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Libellé", "Catégorie", "Prix", "Description" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = createStyledTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_PANEL);
        JButton btnAdd = createStyledButton("Ajouter Acte", true);
        JButton btnDelete = createStyledButton("Supprimer", false);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        p.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> addActeDialog(model));
        btnDelete.addActionListener(e -> deleteActe(table, model));

        // Initial load
        refreshActes(model);

        return p;
    }

    private void refreshActes(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<ActeDTO> list = controller.getAllActes();
            if (list != null) {
                for (ActeDTO a : list) {
                    model.addRow(new Object[] { a.id(), a.libelle(), a.categorie(), a.prixBase(), a.description() });
                }
            }
            model.fireTableDataChanged(); // Ensure UI updates
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addActeDialog(DefaultTableModel model) {
        JTextField tfLib = new JTextField();
        JTextField tfCat = new JTextField();
        JTextField tfPrix = new JTextField();
        JTextField tfDesc = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Libellé:"));
        form.add(tfLib);
        form.add(new JLabel("Catégorie:"));
        form.add(tfCat);
        form.add(new JLabel("Prix de base:"));
        form.add(tfPrix);
        form.add(new JLabel("Description:"));
        form.add(tfDesc);

        int res = JOptionPane.showConfirmDialog(this, form, "Nouvel Acte", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                if (tfLib.getText().isBlank() || tfPrix.getText().isBlank()) {
                    JOptionPane.showMessageDialog(this, "Libellé et Prix obligatoires");
                    return;
                }
                double prix = Double.parseDouble(tfPrix.getText().replace(",", "."));
                ActeDTO dto = new ActeDTO(null, tfLib.getText(), tfCat.getText(), prix, tfDesc.getText());
                controller.createActe(dto);
                refreshActes(model);
                JOptionPane.showMessageDialog(this, "Acte créé avec succès !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur création: " + ex.getMessage());
            }
        }
    }

    private void deleteActe(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne.");
            return;
        }
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer l'acte " + id + " ?", "Confirmer",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.deleteActe(id);
                refreshActes(model);
                // Ensure model refresh happens
                JOptionPane.showMessageDialog(this, "Supprimé !");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur suppression: " + e.getMessage());
            }
        }
    }

    // --- MEDICAMENTS ---
    private JPanel buildMedicamentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(ACCENT_GOLD, 1, true)));

        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Nom", "Labo", "Type", "Forme", "Prix", "Remb.", "Desc." }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = createStyledTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_PANEL);
        JButton btnAdd = createStyledButton("Ajouter Médicament", true);
        JButton btnDelete = createStyledButton("Supprimer", false);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        p.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> addMedicamentDialog(model));
        btnDelete.addActionListener(e -> deleteMedicament(table, model));

        // Initial load
        refreshMedicaments(model);

        return p;
    }

    private void refreshMedicaments(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<MedicamentDTO> list = controller.getAllMedicaments();
            if (list != null) {
                for (MedicamentDTO m : list) {
                    model.addRow(new Object[] { m.id(), m.nom(), m.laboratoire(), m.type(), m.forme(), m.prixUnitaire(),
                            m.remboursable(), m.description() });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addMedicamentDialog(DefaultTableModel model) {
        JTextField tfNom = new JTextField();
        JTextField tfLab = new JTextField();
        JTextField tfType = new JTextField();
        JComboBox<FormeMedicament> cbForme = new JComboBox<>(FormeMedicament.values());
        JTextField tfPrix = new JTextField();
        JCheckBox cbRemb = new JCheckBox("Remboursable");
        JTextField tfDesc = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nom:"));
        form.add(tfNom);
        form.add(new JLabel("Laboratoire:"));
        form.add(tfLab);
        form.add(new JLabel("Type:"));
        form.add(tfType);
        form.add(new JLabel("Forme:"));
        form.add(cbForme);
        form.add(new JLabel("Prix Unitaire:"));
        form.add(tfPrix);
        form.add(new JLabel("Description:"));
        form.add(tfDesc);
        form.add(new JLabel(""));
        form.add(cbRemb);

        int res = JOptionPane.showConfirmDialog(this, form, "Nouveau Médicament", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                if (tfNom.getText().isBlank()) {
                    JOptionPane.showMessageDialog(this, "Nom obligatoire");
                    return;
                }
                double prix = 0.0;
                if (!tfPrix.getText().isBlank())
                    prix = Double.parseDouble(tfPrix.getText().replace(",", "."));

                MedicamentDTO dto = new MedicamentDTO(null, tfNom.getText(), tfLab.getText(), tfType.getText(),
                        (FormeMedicament) cbForme.getSelectedItem(), cbRemb.isSelected(), prix, tfDesc.getText());
                controller.createMedicament(dto);
                refreshMedicaments(model);
                JOptionPane.showMessageDialog(this, "Médicament créé avec succès !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur création: " + ex.getMessage());
            }
        }
    }

    private void deleteMedicament(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer le médicament " + id + " ?", "Confirmer",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteMedicament(id);
            refreshMedicaments(model);
        }
    }

    // --- ANTECEDENTS (CATALOGUE) ---
    private JPanel buildAntecedentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(ACCENT_GOLD, 1, true)));

        DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Nom", "Catégorie", "Risque" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = createStyledTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BG_PANEL);
        JButton btnAdd = createStyledButton("Ajouter Antécédent", true);
        JButton btnDelete = createStyledButton("Supprimer", false);

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        p.add(btnPanel, BorderLayout.SOUTH);

        // Actions
        btnAdd.addActionListener(e -> addAntecedentDialog(model));
        btnDelete.addActionListener(e -> deleteAntecedent(table, model));

        // Initial load
        refreshAntecedents(model);

        return p;
    }

    private void refreshAntecedents(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<RefAntecedentDTO> list = controller.getAllRefAntecedents();
            if (list != null) {
                for (var a : list) {
                    model.addRow(new Object[] { a.id(), a.nom(), a.categorie(), a.risque() });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addAntecedentDialog(DefaultTableModel model) {
        JTextField tfNom = new JTextField();
        JTextField tfCat = new JTextField();
        JComboBox<ma.dentalTech.entities.enums.NiveauDeRisque> cbRisque = new JComboBox<>(
                ma.dentalTech.entities.enums.NiveauDeRisque.values());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nom:"));
        form.add(tfNom);
        form.add(new JLabel("Catégorie:"));
        form.add(tfCat);
        form.add(new JLabel("Niveau de Risque:"));
        form.add(cbRisque);

        int res = JOptionPane.showConfirmDialog(this, form, "Nouvel Antécédent", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                if (tfNom.getText().isBlank())
                    return;
                var dto = new RefAntecedentDTO(
                        null, tfNom.getText(), tfCat.getText(),
                        (ma.dentalTech.entities.enums.NiveauDeRisque) cbRisque.getSelectedItem());
                controller.createRefAntecedent(dto);
                refreshAntecedents(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
            }
        }
    }

    private void deleteAntecedent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        Long id = (Long) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ?", "Confirmer", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteRefAntecedent(id);
            refreshAntecedents(model);
        }
    }

    // --- ASSURANCES (READ ONLY) ---
    private JPanel buildAssurancesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(ACCENT_GOLD, 1, true)));

        DefaultTableModel model = new DefaultTableModel(new Object[] { "Assurance (Enum)" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = createStyledTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        try {
            List<ma.dentalTech.entities.enums.Assurance> list = controller.getAllAssurances();
            if (list != null) {
                for (var a : list)
                    model.addRow(new Object[] { a.name() });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p;
    }

    // --- HELPERS (STYLES) ---

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_GOLD);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header Styling with Custom Renderer to force background color
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BUTTON);
        header.setBackground(PRIMARY_DARK); // Backup
        header.setForeground(Color.WHITE); // Backup
        header.setPreferredSize(new Dimension(0, 35));

        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                // Use default to get basic setup (borders, alignment)
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Force our colors
                c.setBackground(PRIMARY_DARK);
                c.setForeground(Color.WHITE);
                c.setFont(FONT_BUTTON);

                // Optional: Add a simple border for separation
                if (c instanceof JComponent) {
                    ((JComponent) c)
                            .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(255, 255, 255, 50)));
                }

                return c;
            }
        });

        // Body Styling
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    c.setForeground(TEXT_DARK); // Explicitly set text color to visible dark
                } else {
                    // Keep selection colors set by table
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        return table;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        // Rounded border
        btn.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(ACCENT_GOLD, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        if (isPrimary) {
            btn.setBackground(ACCENT_BUTTON_BG);
            btn.setForeground(ACCENT_GOLD);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(ACCENT_BUTTON_BG);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    btn.setBackground(ACCENT_GOLD);
                    btn.setForeground(ACCENT_BUTTON_BG);
                } else {
                    btn.setBackground(BG_APP);
                }
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    btn.setBackground(ACCENT_BUTTON_BG);
                    btn.setForeground(ACCENT_GOLD);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(ACCENT_BUTTON_BG);
                }
            }
        });

        return btn;
    }
}
