package ma.dentalTech.mvc.ui.modules.caisse.dialogs;

import ma.dentalTech.mvc.dto.caisse.FacturePaiementDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class FacturePaiementDialog extends JDialog {

    private JTextField tfMontant;
    private FacturePaiementDTO result = null;

    public static FacturePaiementDTO open(Component parent) {
        FacturePaiementDialog dlg = new FacturePaiementDialog(parent);
        dlg.setVisible(true);
        return dlg.result;
    }

    private FacturePaiementDialog(Component parent) {
        super(SwingUtilities.getWindowAncestor(parent), "Paiement facture", ModalityType.APPLICATION_MODAL);
        setContentPane(buildUi());
        pack();
        setLocationRelativeTo(parent);
    }

    private JComponent buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        tfMontant = new JTextField(12);

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Montant payé (DH) *"), c);
        c.gridx = 1;
        form.add(tfMontant, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new DentalButton("Annuler");
        JButton btnOk = new DentalButton("Valider");
        buttons.add(btnCancel);
        buttons.add(btnOk);

        btnCancel.addActionListener(e -> dispose());
        btnOk.addActionListener(e -> onOk());

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        return root;
    }

    private void onOk() {
        try {
            String montantStr = tfMontant.getText() == null ? "" : tfMontant.getText().trim();
            if (montantStr.isBlank()) throw new IllegalArgumentException("Montant obligatoire");

            BigDecimal montant = new BigDecimal(montantStr);

            result = FacturePaiementDTO.builder()
                    .montant(montant)
                    .build();

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
