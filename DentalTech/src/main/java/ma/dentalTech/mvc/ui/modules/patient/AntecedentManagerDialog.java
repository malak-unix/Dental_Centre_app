package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.dto.patient.AntecedentFormDto;
import ma.dentalTech.mvc.dto.patient.AntecedentListDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AntecedentManagerDialog extends JDialog {

    private final AntecedentController controller;
    private final Long patientId;
    private final String patientNom;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Catégorie", "Risque", "Description"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final DentalButton btnAdd = new DentalButton("Ajouter");
    private final DentalButton btnEdit = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");
    private final DentalButton btnClose = new DentalButton("Fermer");

    public AntecedentManagerDialog(Window owner, Long patientId, String patientNom) {
        super(owner, "Antécédents - " + patientNom, ModalityType.APPLICATION_MODAL);

        this.patientId = patientId;
        this.patientNom = patientNom;

        Object bean = ApplicationContext.getBean("antecedentController");
        if (!(bean instanceof AntecedentController ac)) {
            throw new IllegalStateException("Bean 'antecedentController' introuvable. Vérifie beans.properties + ApplicationContext.");
        }
        this.controller = ac;

        setSize(920, 520);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(DentalTheme.BG);
        setLayout(new BorderLayout(12, 12));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        root.add(card, BorderLayout.CENTER);

        card.add(buildHeader(), BorderLayout.NORTH);
        card.add(buildTableCard(), BorderLayout.CENTER);
        card.add(buildActions(), BorderLayout.SOUTH);

        wire();
        refresh();
    }

    private JComponent buildHeader() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Antécédents du patient : " + patientNom);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(DentalTheme.TEXT2);

        top.add(title, BorderLayout.WEST);
        return top;
    }

    private JComponent buildTableCard() {
        CardPanel results = new CardPanel("Résultats");
        results.setLayout(new BorderLayout());

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        results.add(sp, BorderLayout.CENTER);
        return results;
    }

    private JComponent buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);
        actions.add(btnClose);

        return actions;
    }

    private void wire() {
        btnClose.addActionListener(e -> dispose());

        btnAdd.addActionListener(e -> {
            AntecedentFormDialog dlg = new AntecedentFormDialog(this, "Ajouter un antécédent", null, patientId);
            dlg.setVisible(true);
            if (!dlg.isConfirmed()) return;

            try {
                controller.create(patientId, dlg.getDto());
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnEdit.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            AntecedentFormDto initial = rowToDto();
            AntecedentFormDialog dlg = new AntecedentFormDialog(this, "Modifier antécédent #" + id, initial, patientId);
            dlg.setVisible(true);
            if (!dlg.isConfirmed()) return;

            try {
                controller.update(id, dlg.getDto());
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnDelete.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            int ok = JOptionPane.showConfirmDialog(this,
                    "Supprimer l'antécédent #" + id + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (ok != JOptionPane.YES_OPTION) return;

            try {
                controller.delete(id);
                refresh();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        // double-clic => modifier
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) btnEdit.doClick();
            }
        });
    }

    private void refresh() {
        try {
            List<AntecedentListDto> list = controller.listByPatient(patientId);
            model.setRowCount(0);
            if (list == null) return;

            for (AntecedentListDto a : list) {
                model.addRow(new Object[]{
                        a.getId(),
                        a.getNom(),
                        a.getCategorie(),
                        a.getNiveauDeRisque(),
                        a.getDescription()
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne d’abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        return v == null ? null : Long.valueOf(v.toString());
    }

    private AntecedentFormDto rowToDto() {
        int row = table.getSelectedRow();
        if (row < 0) return null;

        AntecedentFormDto dto = new AntecedentFormDto();
        dto.setId(Long.valueOf(model.getValueAt(row, 0).toString()));
        dto.setPatientId(patientId);
        dto.setNom(String.valueOf(model.getValueAt(row, 1)));
        dto.setCategorie(String.valueOf(model.getValueAt(row, 2)));

        // risque est stocké en texte dans la table => on laisse le form dialog le gérer
        dto.setDescription(String.valueOf(model.getValueAt(row, 4)));
        return dto;
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
