package ma.dentalTech.mvc.ui.modules.caisse.dialogs;

import ma.dentalTech.mvc.controllers.modules.caisse.api.ChargesControllerV2;
import ma.dentalTech.mvc.dto.caisse.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChargeEditDialog extends JDialog {

    private final ChargesControllerV2 controller;
    private final ChargeItemDTO existing;

    private JTextField tfTitre;
    private JTextField tfDesc;
    private JTextField tfMontant;
    private JTextField tfDate; // yyyy-MM-dd

    private boolean saved = false;

    public static boolean openCreate(Component parent, ChargesControllerV2 controller) {
        ChargeEditDialog dlg = new ChargeEditDialog(parent, controller, null);
        dlg.setVisible(true);
        return dlg.saved;
    }

    public static boolean openEdit(Component parent, ChargesControllerV2 controller, ChargeItemDTO existing) {
        ChargeEditDialog dlg = new ChargeEditDialog(parent, controller, existing);
        dlg.setVisible(true);
        return dlg.saved;
    }

    private ChargeEditDialog(Component parent, ChargesControllerV2 controller, ChargeItemDTO existing) {
        super(SwingUtilities.getWindowAncestor(parent),
                existing == null ? "Ajouter / Modifier Charge" : "Ajouter / Modifier Charge",
                ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.existing = existing;

        setContentPane(buildUi());
        pack();
        setLocationRelativeTo(parent);

        if (existing != null) fillFromExisting(existing);
        else tfDate.setText(LocalDate.now().toString());
    }

    private JComponent buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        tfTitre = new JTextField(24);
        tfDesc = new JTextField(24);
        tfMontant = new JTextField(12);
        tfDate = new JTextField(12);

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Libellé *"), c);
        c.gridx = 1;
        form.add(tfTitre, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Description"), c);
        c.gridx = 1;
        form.add(tfDesc, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Montant (DH) *"), c);
        c.gridx = 1;
        form.add(tfMontant, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Date (yyyy-MM-dd) *"), c);
        c.gridx = 1;
        form.add(tfDate, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton(existing == null ? "Ajouter" : "Enregistrer");
        buttons.add(btnCancel);
        buttons.add(btnSave);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        return root;
    }

    private void fillFromExisting(ChargeItemDTO ex) {
        tfTitre.setText(ex.getTitre() == null ? "" : ex.getTitre());
        tfDesc.setText(ex.getDescription() == null ? "" : ex.getDescription());
        tfMontant.setText(ex.getMontant() == null ? "" : ex.getMontant().toString());
        if (ex.getDateCharge() != null) tfDate.setText(ex.getDateCharge().toLocalDate().toString());
    }

    private void onSave() {
        try {
            String titre = val(tfTitre);
            String desc = val(tfDesc);
            String montantStr = val(tfMontant);
            String dateStr = val(tfDate);

            if (titre.isBlank()) throw new IllegalArgumentException("Libellé obligatoire");
            if (montantStr.isBlank()) throw new IllegalArgumentException("Montant obligatoire");
            if (dateStr.isBlank()) throw new IllegalArgumentException("Date obligatoire");

            BigDecimal montant = new BigDecimal(montantStr);
            LocalDateTime dateCharge = LocalDate.parse(dateStr).atStartOfDay();

            // ✅ cabinetId temporaire (si tu as un vrai cabinetId dans ton login/session, on le branchera)
            Long cabinetId = (existing != null && existing.getCabinetId() != null) ? existing.getCabinetId() : 1L;

            if (existing == null) {
                ChargeCreateDTO dto = ChargeCreateDTO.builder()
                        .cabinetId(cabinetId)
                        .titre(titre)
                        .description(desc)
                        .montant(montant)
                        .dateCharge(dateCharge)
                        .build();
                controller.create(dto);
            } else {
                ChargeUpdateDTO dto = ChargeUpdateDTO.builder()
                         .titre(titre)
                        .description(desc)
                        .montant(montant)
                        .dateCharge(dateCharge)
                        .build();
                controller.update(existing.getId(), dto);
            }

            saved = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String val(JTextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }
}
