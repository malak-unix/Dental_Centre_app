package ma.dentalTech.mvc.ui.modules.caisse.dialogs;

import ma.dentalTech.mvc.controllers.modules.caisse.api.ChargesControllerV2;
import ma.dentalTech.mvc.dto.caisse.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
        super(SwingUtilities.getWindowAncestor(parent), existing == null ? "Ajouter une charge" : "Modifier une charge", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.existing = existing;

        setContentPane(buildUi());
        pack();
        setLocationRelativeTo(parent);

        if (existing != null) fillFromExisting(existing);
    }

    private JComponent buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        tfTitre = new JTextField(24);
        tfDesc = new JTextField(24);
        tfMontant = new JTextField(12);
        tfDate = new JTextField(12);

        if (existing == null) {
            tfDate.setText(LocalDate.now().toString());
        }

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Titre *"), c);
        c.gridx = 1;
        form.add(tfTitre, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Description"), c);
        c.gridx = 1;
        form.add(tfDesc, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Montant *"), c);
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
        tfTitre.setText(nz(ex.getTitre()));
        tfDesc.setText(nz(ex.getDescription()));
        tfMontant.setText(ex.getMontant() == null ? "" : ex.getMontant().toString());
        if (ex.getDateCharge() != null) {
            tfDate.setText(ex.getDateCharge().toLocalDate().toString());
        }
    }

    private void onSave() {
        try {
            String titre = tfTitre.getText() == null ? "" : tfTitre.getText().trim();
            String desc = tfDesc.getText() == null ? "" : tfDesc.getText().trim();
            String montantStr = tfMontant.getText() == null ? "" : tfMontant.getText().trim();
            String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();

            if (titre.isBlank()) throw new IllegalArgumentException("Titre obligatoire");
            if (montantStr.isBlank()) throw new IllegalArgumentException("Montant obligatoire");
            if (dateStr.isBlank()) throw new IllegalArgumentException("Date obligatoire");

            BigDecimal montant = new BigDecimal(montantStr);
            LocalDateTime dateCharge = LocalDate.parse(dateStr).atStartOfDay();

            if (existing == null) {
                ChargeCreateDTO dto = buildChargeCreateDTO(1L, titre, desc, montant, dateCharge);
                controller.create(dto);
            } else {
                ChargeUpdateDTO dto = buildChargeUpdateDTO(1L, titre, desc, montant, dateCharge);
                controller.update(existing.getId(), dto);
            }

            saved = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== DTO factories (reflection-safe) =====

    private ChargeCreateDTO buildChargeCreateDTO(Long cabinetId, String titre, String desc, BigDecimal montant, LocalDateTime dateCharge) throws Exception {
        // Try Lombok builder()
        try {
            Method builderM = ChargeCreateDTO.class.getMethod("builder");
            Object b = builderM.invoke(null);
            call(b, "cabinetId", cabinetId);
            call(b, "titre", titre);
            call(b, "description", desc);
            call(b, "montant", montant);
            call(b, "dateCharge", dateCharge);
            return (ChargeCreateDTO) b.getClass().getMethod("build").invoke(b);
        } catch (NoSuchMethodException ignored) {
            // fallback ctor
        }

        // Try constructor by types (common order)
        for (Constructor<?> ct : ChargeCreateDTO.class.getDeclaredConstructors()) {
            Class<?>[] p = ct.getParameterTypes();
            if (p.length == 5
                    && p[0] == Long.class
                    && p[1] == String.class
                    && p[2] == String.class
                    && p[3] == BigDecimal.class
                    && p[4] == LocalDateTime.class) {
                ct.setAccessible(true);
                return (ChargeCreateDTO) ct.newInstance(cabinetId, titre, desc, montant, dateCharge);
            }
        }

        // Last resort: no-args + setters if exist
        ChargeCreateDTO dto = ChargeCreateDTO.class.getDeclaredConstructor().newInstance();
        set(dto, "setCabinetId", cabinetId);
        set(dto, "setTitre", titre);
        set(dto, "setDescription", desc);
        set(dto, "setMontant", montant);
        set(dto, "setDateCharge", dateCharge);
        return dto;
    }

    private ChargeUpdateDTO buildChargeUpdateDTO(Long cabinetId, String titre, String desc, BigDecimal montant, LocalDateTime dateCharge) throws Exception {
        try {
            Method builderM = ChargeUpdateDTO.class.getMethod("builder");
            Object b = builderM.invoke(null);
            call(b, "cabinetId", cabinetId);
            call(b, "titre", titre);
            call(b, "description", desc);
            call(b, "montant", montant);
            call(b, "dateCharge", dateCharge);
            return (ChargeUpdateDTO) b.getClass().getMethod("build").invoke(b);
        } catch (NoSuchMethodException ignored) {
        }

        for (Constructor<?> ct : ChargeUpdateDTO.class.getDeclaredConstructors()) {
            Class<?>[] p = ct.getParameterTypes();
            if (p.length == 5
                    && p[0] == Long.class
                    && p[1] == String.class
                    && p[2] == String.class
                    && p[3] == BigDecimal.class
                    && p[4] == LocalDateTime.class) {
                ct.setAccessible(true);
                return (ChargeUpdateDTO) ct.newInstance(cabinetId, titre, desc, montant, dateCharge);
            }
        }

        ChargeUpdateDTO dto = ChargeUpdateDTO.class.getDeclaredConstructor().newInstance();
        set(dto, "setCabinetId", cabinetId);
        set(dto, "setTitre", titre);
        set(dto, "setDescription", desc);
        set(dto, "setMontant", montant);
        set(dto, "setDateCharge", dateCharge);
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

    private String nz(String s) { return s == null ? "" : s; }
}
