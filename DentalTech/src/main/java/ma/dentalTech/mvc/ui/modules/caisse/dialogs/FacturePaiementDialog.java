package ma.dentalTech.mvc.ui.modules.caisse.dialogs;

import ma.dentalTech.mvc.dto.caisse.FacturePaiementDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacturePaiementDialog extends JDialog {

    private JTextField tfMontant;
    private JComboBox<String> cbMode;
    private JTextField tfDate; // yyyy-MM-dd

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
        cbMode = new JComboBox<>(new String[]{"ESPECES", "CARTE", "CHEQUE", "VIREMENT"});
        tfDate = new JTextField(12);
        tfDate.setText(LocalDate.now().toString());

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Montant payé *"), c);
        c.gridx = 1;
        form.add(tfMontant, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Mode paiement"), c);
        c.gridx = 1;
        form.add(cbMode, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Date (yyyy-MM-dd) *"), c);
        c.gridx = 1;
        form.add(tfDate, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnOk = new JButton("Valider");
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
            String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();
            if (montantStr.isBlank()) throw new IllegalArgumentException("Montant obligatoire");
            if (dateStr.isBlank()) throw new IllegalArgumentException("Date obligatoire");

            BigDecimal montant = new BigDecimal(montantStr);
            LocalDateTime datePaiement = LocalDate.parse(dateStr).atStartOfDay();
            String mode = String.valueOf(cbMode.getSelectedItem());

            result = buildFacturePaiementDTO(montant, mode, datePaiement);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reflection-safe factory (builder/record/pojo)
    private FacturePaiementDTO buildFacturePaiementDTO(BigDecimal montant, String mode, LocalDateTime datePaiement) throws Exception {
        // Try Lombok builder()
        try {
            Method builderM = FacturePaiementDTO.class.getMethod("builder");
            Object b = builderM.invoke(null);
            call(b, "montantPaye", montant);
            call(b, "modePaiement", mode);
            call(b, "datePaiement", datePaiement);
            return (FacturePaiementDTO) b.getClass().getMethod("build").invoke(b);
        } catch (NoSuchMethodException ignored) {}

        // Try constructor common forms
        for (Constructor<?> ct : FacturePaiementDTO.class.getDeclaredConstructors()) {
            Class<?>[] p = ct.getParameterTypes();
            if (p.length == 3 && p[0] == BigDecimal.class && p[1] == String.class && p[2] == LocalDateTime.class) {
                ct.setAccessible(true);
                return (FacturePaiementDTO) ct.newInstance(montant, mode, datePaiement);
            }
        }

        // Last resort: no-args + setters
        FacturePaiementDTO dto = FacturePaiementDTO.class.getDeclaredConstructor().newInstance();
        set(dto, "setMontantPaye", montant);
        set(dto, "setModePaiement", mode);
        set(dto, "setDatePaiement", datePaiement);
        return dto;
    }

    private void call(Object target, String method, Object arg) {
        try {
            Method m = target.getClass().getMethod(method, arg.getClass());
            m.invoke(target, arg);
        } catch (Exception ignored) { }
    }

    private void set(Object target, String setter, Object arg) {
        try {
            Method m = target.getClass().getMethod(setter, arg.getClass());
            m.invoke(target, arg);
        } catch (Exception ignored) { }
    }
}
