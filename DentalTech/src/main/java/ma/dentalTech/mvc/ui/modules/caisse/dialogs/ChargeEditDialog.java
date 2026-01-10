package ma.dentalTech.mvc.ui.modules.caisse.dialogs;

import ma.dentalTech.mvc.dto.caisse.ChargeCreateDTO;
import ma.dentalTech.mvc.dto.caisse.ChargeItemDTO;
import ma.dentalTech.mvc.dto.caisse.ChargeUpdateDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Dialog UI-only (ne touche PAS le controller/service).
 * Retourne un DTO prêt à être envoyé au controller.
 */
public class ChargeEditDialog extends JDialog {

    private final ChargeItemDTO existing;

    private JTextField tfTitre;
    private JTextField tfDesc;
    private JTextField tfMontant;
    private JTextField tfDate; // yyyy-MM-dd

    private Optional<ChargeCreateDTO> createResult = Optional.empty();
    private Optional<ChargeUpdateDTO> updateResult = Optional.empty();

    private ChargeEditDialog(Component parent, ChargeItemDTO existing) {
        super(SwingUtilities.getWindowAncestor(parent),
                existing == null ? "Ajouter / Modifier Charge" : "Ajouter / Modifier Charge",
                ModalityType.APPLICATION_MODAL);

        this.existing = existing;

        setContentPane(buildUi());
        pack();
        setLocationRelativeTo(parent);

        if (existing != null) {
            fillFromExisting(existing);
        } else {
            tfDate.setText(LocalDate.now().toString());
        }
    }

    public static Optional<ChargeCreateDTO> showCreate(Component parent) {
        ChargeEditDialog dlg = new ChargeEditDialog(parent, null);
        dlg.setVisible(true);
        return dlg.createResult;
    }

    public static Optional<ChargeUpdateDTO> showEdit(Component parent, ChargeItemDTO existing) {
        ChargeEditDialog dlg = new ChargeEditDialog(parent, existing);
        dlg.setVisible(true);
        return dlg.updateResult;
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
        tfMontant.setText(ex.getMontant() == null ? "" : ex.getMontant().toPlainString());
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

            if (existing == null) {
                // cabinetId: si tu as une session/cabinetId réel, on le branchera après.
                Long cabinetId = 1L;

                createResult = Optional.of(ChargeCreateDTO.builder()
                        .cabinetId(cabinetId)
                        .titre(titre)
                        .description(desc)
                        .montant(montant)
                        .dateCharge(dateCharge)
                        .build());
            } else {
                updateResult = Optional.of(ChargeUpdateDTO.builder()
                        .titre(titre)
                        .description(desc)
                        .montant(montant)
                        .dateCharge(dateCharge)
                        .build());
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String val(JTextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }
}
