package ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.certificat.CertificatListRequestDTO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface liste des certificats selon la maquette.
 * Affiche : Nom du patient, Date début, Date fin, Durée, Note médecin, Actions
 */
public class CertificatListUI extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CertificatController controller;
    private final Long medecinId;
    private final String username;

    private final JTextField txtPatient = new JTextField(15);
    private final JTextField txtDateDebutFrom = new JTextField(10); // yyyy-MM-dd
    private final JTextField txtDateDebutTo = new JTextField(10);
    private final JTextField txtDateFinFrom = new JTextField(10);
    private final JTextField txtDateFinTo = new JTextField(10);
    private final JTextField txtNote = new JTextField(15);

    private final JButton btnSearch = new JButton("Rechercher");
    private final JButton btnReset = new JButton("Actualiser");
    private final JButton btnAdd = new JButton("+ Certificats");

    private final JTable table = new JTable();
    private final CertificatTableModel model = new CertificatTableModel();

    public CertificatListUI(CertificatController controller, Long medecinId) {
        this(controller, medecinId, "medecin");
    }

    public CertificatListUI(CertificatController controller, Long medecinId, String username) {
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

        JLabel title = new JLabel("Certificats");
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
        UiStyles.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> onAddCertificat());
        titleRow.add(btnAdd, BorderLayout.EAST);

        wrap.add(titleRow);
        wrap.add(Box.createVerticalStrut(12));

        // Filtres (optionnels pour l'instant, on peut les simplifier)
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filters.setOpaque(false);

        filters.add(new JLabel("Patient:"));
        filters.add(txtPatient);

        filters.add(Box.createHorizontalStrut(10));
        filters.add(new JLabel("Date début:"));
        filters.add(txtDateDebutFrom);
        filters.add(new JLabel("à"));
        filters.add(txtDateDebutTo);

        filters.add(Box.createHorizontalStrut(10));
        filters.add(new JLabel("Note:"));
        filters.add(txtNote);

        filters.add(Box.createHorizontalStrut(10));
        UiStyles.styleSecondaryButton(btnSearch);
        UiStyles.styleSecondaryButton(btnReset);
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

        // Colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Nom patient
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Date début
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Date fin
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Durée
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Note médecin
        table.getColumnModel().getColumn(5).setPreferredWidth(300); // Actions

        // Renderer pour les actions
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionsCellRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionsCellEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        return scroll;
    }

    private void wireActions() {
        btnReset.addActionListener(e -> {
            txtPatient.setText("");
            txtDateDebutFrom.setText("");
            txtDateDebutTo.setText("");
            txtDateFinFrom.setText("");
            txtDateFinTo.setText("");
            txtNote.setText("");
            refresh();
        });

        btnSearch.addActionListener(e -> refresh());
    }

    private void onAddCertificat() {
        // Pour l'instant, on utilise une liste vide pour les dossiers
        // TODO: Récupérer la liste réelle des dossiers médicaux pour le médecin
        java.util.List<CertificatAddFormUI.DossierComboItem> dossiers = new ArrayList<>();
        CertificatAddFormUI dialog = new CertificatAddFormUI(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                dossiers,
                username
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refresh();
        }
    }

    public void refresh() {
        try {
            CertificatListRequestDTO req = buildRequestFromUI();
            List<CertificatListItemDTO> list = controller.searchForList(req);
            model.setRows(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private CertificatListRequestDTO buildRequestFromUI() {
        CertificatListRequestDTO req = new CertificatListRequestDTO();
        req.setMedecinId(medecinId);

        String kw = txtPatient.getText();
        if (kw != null && !kw.isBlank()) req.setPatientKeyword(kw.trim());

        String d1 = txtDateDebutFrom.getText();
        if (d1 != null && !d1.isBlank()) {
            try {
                req.setDateDebutFrom(LocalDate.parse(d1.trim()));
            } catch (DateTimeParseException e) {
                // Ignorer
            }
        }

        String d2 = txtDateDebutTo.getText();
        if (d2 != null && !d2.isBlank()) {
            try {
                req.setDateDebutTo(LocalDate.parse(d2.trim()));
            } catch (DateTimeParseException e) {
                // Ignorer
            }
        }

        String note = txtNote.getText();
        if (note != null && !note.isBlank()) req.setNoteKeyword(note.trim());

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
    private class CertificatTableModel extends AbstractTableModel {
        private final String[] cols = {"Nom du patient", "Date début", "Date fin", "Durée", "Note médecin", "Actions"};
        private List<CertificatListItemDTO> rows = new ArrayList<>();

        void setRows(List<CertificatListItemDTO> data) {
            this.rows = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
            fireTableDataChanged();
        }

        CertificatListItemDTO getAt(int row) {
            if (row < 0 || row >= rows.size()) return null;
            return rows.get(row);
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 5; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CertificatListItemDTO r = rows.get(rowIndex);
            if (r == null) return "";
            return switch (columnIndex) {
                case 0 -> r.getPatientNomComplet() == null ? "" : r.getPatientNomComplet();
                case 1 -> r.getDateDebut() == null ? "" : r.getDateDebut().format(DATE_FMT);
                case 2 -> r.getDateFin() == null ? "" : r.getDateFin().format(DATE_FMT);
                case 3 -> r.getDuree() == null ? "" : (r.getDuree() + " jour" + (r.getDuree() > 1 ? "s" : ""));
                case 4 -> r.getNoteMedecin() == null ? "" : r.getNoteMedecin();
                case 5 -> ""; // actions renderer
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
        private final JButton b4 = new JButton("Imprimer");

        ActionsCellRenderer() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3, b4)) {
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

    private class ActionsCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        private final JButton b1 = new JButton("Consulter");
        private final JButton b2 = new JButton("Modifier");
        private final JButton b3 = new JButton("Supprimer");
        private final JButton b4 = new JButton("Imprimer");

        private int currentRow = -1;

        ActionsCellEditor() {
            panel.setOpaque(true);
            for (JButton b : List.of(b1, b2, b3, b4)) {
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

            b4.setBackground(new Color(0x1C, 0x25, 0x41)); // Bleu foncé pour Imprimer
            b4.setForeground(Color.WHITE);
            b4.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xCB, 0xA1, 0x35), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));

            b1.addActionListener(e -> {
                stopCellEditing();
                CertificatListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                // Ouvrir l'interface de consultation
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(CertificatListUI.this),
                        "Consultation du certificat", true);
                CertificatDetailUI detailUI = new CertificatDetailUI(controller, r.getCertificatId(), () -> dialog.dispose());
                dialog.setContentPane(detailUI);
                dialog.setSize(800, 600);
                dialog.setLocationRelativeTo(CertificatListUI.this);
                dialog.setVisible(true);
            });

            b2.addActionListener(e -> {
                stopCellEditing();
                CertificatListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                try {
                    CertificatDTO certificatDTO = controller.getById(r.getCertificatId());
                    if (certificatDTO == null) {
                        JOptionPane.showMessageDialog(CertificatListUI.this,
                                "Certificat introuvable",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Ouvrir le formulaire de modification
                    // Récupérer les dossiers via le service dossier
                    ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController dossierController =
                            (ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController)
                                    ApplicationContext.getBean("dossierMedicalController");
                    
                    List<ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListEnrichedItemDTO> dossiersList =
                            dossierController.searchForList(new ma.dentalTech.mvc.dto.dossierMedicale.dossier.DossierListRequestDTO(
                                    null, medecinId, new ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO(100, 0)
                            ));
                    
                    List<CertificatAddFormUI.DossierComboItem> dossiers = dossiersList.stream()
                            .map(d -> new CertificatAddFormUI.DossierComboItem(
                                    d.getDossierId(),
                                    d.getPatientNomComplet() != null ? d.getPatientNomComplet() : "Dossier #" + d.getDossierId()
                            ))
                            .toList();

                    CertificatAddFormUI dialog = new CertificatAddFormUI(
                            (Frame) SwingUtilities.getWindowAncestor(CertificatListUI.this),
                            controller,
                            dossiers,
                            username,
                            certificatDTO
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
                CertificatListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                int ok = JOptionPane.showConfirmDialog(
                        CertificatListUI.this,
                        "Supprimer le certificat #" + r.getCertificatId() + " ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (ok != JOptionPane.YES_OPTION) return;

                try {
                    controller.delete(r.getCertificatId());
                    refresh();
                    JOptionPane.showMessageDialog(CertificatListUI.this,
                            "Certificat supprimé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showError(ex);
                }
            });

            b4.addActionListener(e -> {
                stopCellEditing();
                CertificatListItemDTO r = model.getAt(currentRow);
                if (r == null) return;
                
                try {
                    JOptionPane.showMessageDialog(CertificatListUI.this,
                            "Impression du certificat #" + r.getCertificatId() + "\n" +
                            "Patient: " + r.getPatientNomComplet(),
                            "Impression",
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

