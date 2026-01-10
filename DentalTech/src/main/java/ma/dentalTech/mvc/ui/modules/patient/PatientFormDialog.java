package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Method;

public class PatientFormDialog extends JDialog {

    private final JTextField tfNom = new JTextField();
    private final JTextField tfPrenom = new JTextField();
    private final JTextField tfTel = new JTextField();
    private final JTextField tfEmail = new JTextField();

    private boolean confirmed = false;
    private PatientFormDto dto;

    public PatientFormDialog(Window owner, String title, PatientFormDto initialDto) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.dto = initialDto;

        setSize(520, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(DentalTheme.BG);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lNom = mkLabel("Nom");
        JLabel lPrenom = mkLabel("Prénom");
        JLabel lTel = mkLabel("Téléphone");
        JLabel lEmail = mkLabel("Email");

        // lignes
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        body.add(lNom, c);
        c.gridx = 1; c.weightx = 1;
        tfNom.setPreferredSize(new Dimension(260, 34));
        body.add(tfNom, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        body.add(lPrenom, c);
        c.gridx = 1; c.weightx = 1;
        tfPrenom.setPreferredSize(new Dimension(260, 34));
        body.add(tfPrenom, c);

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        body.add(lTel, c);
        c.gridx = 1; c.weightx = 1;
        tfTel.setPreferredSize(new Dimension(260, 34));
        body.add(tfTel, c);

        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        body.add(lEmail, c);
        c.gridx = 1; c.weightx = 1;
        tfEmail.setPreferredSize(new Dimension(260, 34));
        body.add(tfEmail, c);

        add(body, BorderLayout.CENTER);

        // footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        DentalButton cancel = new DentalButton("Annuler");
        DentalButton ok = new DentalButton("Enregistrer");

        cancel.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        ok.addActionListener(e -> {
            if (tfNom.getText().isBlank() || tfPrenom.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Nom et prénom sont obligatoires.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dto == null) dto = createEmptyDto();

            // ✅ remplissage via réflexion (compile même si les setters changent)
            setProp(dto, "Nom", tfNom.getText().trim());
            setProp(dto, "Prenom", tfPrenom.getText().trim());
            setProp(dto, "Telephone", tfTel.getText().trim());
            setProp(dto, "Tel", tfTel.getText().trim()); // parfois setTel(...)
            setProp(dto, "Email", tfEmail.getText().trim());

            confirmed = true;
            setVisible(false);
        });

        footer.add(cancel);
        footer.add(ok);
        add(footer, BorderLayout.SOUTH);

        // prefill
        if (dto != null) {
            tfNom.setText(getProp(dto, "Nom"));
            tfPrenom.setText(getProp(dto, "Prenom"));
            String tel = getProp(dto, "Telephone");
            if (tel.isBlank()) tel = getProp(dto, "Tel");
            tfTel.setText(tel);
            tfEmail.setText(getProp(dto, "Email"));
        }
    }

    private JLabel mkLabel(String text) {
        JLabel l = new JLabel(text + " :");
        l.setFont(DentalTheme.textBold(12));
        l.setForeground(DentalTheme.TEXT2);
        return l;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public PatientFormDto getDto() {
        return dto;
    }

    private static void setProp(Object obj, String prop, String value) {
        if (obj == null) return;
        try {
            Method m = obj.getClass().getMethod("set" + prop, String.class);
            m.invoke(obj, value);
        } catch (Exception ignored) {
            // si pas de setter, on ignore sans casser
        }
    }

    private static String getProp(Object obj, String prop) {
        if (obj == null) return "";
        try {
            Method m = obj.getClass().getMethod("get" + prop);
            Object v = m.invoke(obj);
            return v == null ? "" : v.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static PatientFormDto createEmptyDto() {
        try {
            // 1) no-args constructor
            return PatientFormDto.class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // 2) builder() fallback
            try {
                Object builder = PatientFormDto.class.getMethod("builder").invoke(null);
                Object built = builder.getClass().getMethod("build").invoke(builder);
                return (PatientFormDto) built;
            } catch (Exception ex) {
                throw new RuntimeException("Impossible de créer PatientFormDto (pas de constructeur ni builder).");
            }
        }
    }
}
