package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.mvc.dto.patient.AntecedentFormDto;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AntecedentFormDialog extends JDialog {

    private final JTextField tfNom = new JTextField();
    private final JTextField tfCategorie = new JTextField();
    private final JComboBox<NiveauDeRisque> cbRisque = new JComboBox<>();
    private final JTextArea taDescription = new JTextArea(5, 28);

    // Modèle référentiel d'antécédent (liste alimentée par l'admin)
    private final JComboBox<Antecedents> cbTemplate = new JComboBox<>();

    private boolean confirmed = false;
    private AntecedentFormDto dto;
    private final Long patientId;

    public AntecedentFormDialog(Window owner, String title, AntecedentFormDto initial, Long patientId) {
        super(owner, title, ModalityType.APPLICATION_MODAL);

        this.patientId = patientId;
        this.dto = initial;

        setSize(640, 430);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(DentalTheme.BG);

        // combo risque
        DefaultComboBoxModel<NiveauDeRisque> m = new DefaultComboBoxModel<>();
        m.addElement(null); // option vide
        for (NiveauDeRisque r : NiveauDeRisque.values()) m.addElement(r);
        cbRisque.setModel(m);
        cbRisque.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-" : value.toString());
                return this;
            }
        });

        // Charger les modèles référentiels (tous antécédents existants)
        loadTemplates();

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Modèle référentiel
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        body.add(mkLabel("Modèle existant"), c);
        c.gridx = 1; c.weightx = 1;
        cbTemplate.setPreferredSize(new Dimension(360, 34));
        body.add(cbTemplate, c);

        // Nom
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        body.add(mkLabel("Nom *"), c);
        c.gridx = 1; c.weightx = 1;
        tfNom.setPreferredSize(new Dimension(360, 34));
        body.add(tfNom, c);

        // Catégorie
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        body.add(mkLabel("Catégorie"), c);
        c.gridx = 1; c.weightx = 1;
        tfCategorie.setPreferredSize(new Dimension(360, 34));
        body.add(tfCategorie, c);

        // Risque
        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        body.add(mkLabel("Niveau de risque"), c);
        c.gridx = 1; c.weightx = 1;
        cbRisque.setPreferredSize(new Dimension(220, 34));
        body.add(cbRisque, c);

        // Description
        c.gridx = 0; c.gridy = 4; c.weightx = 0; c.anchor = GridBagConstraints.NORTHWEST;
        body.add(mkLabel("Description"), c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.BOTH; c.weighty = 1;
        taDescription.setLineWrap(true);
        taDescription.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(taDescription);
        body.add(sp, c);

        add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        DentalButton cancel = new DentalButton("Annuler");
        DentalButton ok = new DentalButton("Enregistrer");

        cancel.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        ok.addActionListener(e -> onSave());

        footer.add(cancel);
        footer.add(ok);

        add(footer, BorderLayout.SOUTH);

        // prefill
        if (dto != null) {
            tfNom.setText(n(dto.getNom()));
            tfCategorie.setText(n(dto.getCategorie()));
            cbRisque.setSelectedItem(dto.getNiveauDeRisque());
            taDescription.setText(n(dto.getDescription()));
        }

        // Quand un modèle référentiel est choisi, on remplit les champs
        cbTemplate.addActionListener(e -> {
            Object sel = cbTemplate.getSelectedItem();
            if (!(sel instanceof Antecedents a)) return;
            tfNom.setText(n(a.getNom()));
            tfCategorie.setText(n(a.getCategorie()));
            cbRisque.setSelectedItem(a.getNiveauDeRisque());
            taDescription.setText(n(a.getDescription()));
        });
    }

    private void onSave() {
        if (tfNom.getText() == null || tfNom.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nom obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dto == null) dto = new AntecedentFormDto();

        dto.setPatientId(patientId);
        dto.setNom(tfNom.getText().trim());
        dto.setCategorie(tfCategorie.getText() != null ? tfCategorie.getText().trim() : null);
        dto.setNiveauDeRisque((NiveauDeRisque) cbRisque.getSelectedItem());
        dto.setDescription(taDescription.getText() != null ? taDescription.getText().trim() : null);

        confirmed = true;
        setVisible(false);
    }

    private JLabel mkLabel(String text) {
        JLabel l = new JLabel(text + " :");
        l.setFont(DentalTheme.textBold(12));
        l.setForeground(DentalTheme.TEXT2);
        return l;
    }

    private static String n(String s) { return s == null ? "" : s; }

    /**
     * Charge tous les antécédents existants comme modèles possibles.
     * Cela permet à la secrétaire de réutiliser ceux saisis par l'admin
     * dans les écrans de référentiels / reporting.
     */
    private void loadTemplates() {
        try {
            Object bean = ApplicationContext.getBean(AntecedentRepository.class);
            if (!(bean instanceof AntecedentRepository repo)) return;

            List<Antecedents> list = repo.findAll();
            DefaultComboBoxModel<Antecedents> model = new DefaultComboBoxModel<>();
            model.addElement(null); // option vide
            if (list != null) {
                for (Antecedents a : list) {
                    model.addElement(a);
                }
            }
            cbTemplate.setModel(model);
            cbTemplate.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Antecedents a) {
                        String nom = n(a.getNom());
                        String cat = n(a.getCategorie());
                        setText(cat.isBlank() ? nom : nom + " (" + cat + ")");
                    } else if (value == null) {
                        setText("-");
                    }
                    return this;
                }
            });
        } catch (Exception ignored) {
            // En cas de problème, on laisse simplement la combo vide.
        }
    }

    public boolean isConfirmed() { return confirmed; }

    public AntecedentFormDto getDto() { return dto; }
}
