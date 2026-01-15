package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReferentielsPanel extends JPanel {

    private final ActeRepository acteRepo;
    private final MedicamentRepository medicamentRepo;

    public ReferentielsPanel() {
        this.acteRepo = ApplicationContext.getBean(ActeRepository.class);
        this.medicamentRepo = ApplicationContext.getBean(MedicamentRepository.class);

        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel title = new JLabel("Gestion des Referentiels", SwingConstants.LEFT);
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(DentalTheme.textFont(14));

        tabs.addTab("Actes", buildActesPanel());
        tabs.addTab("Medicaments", buildMedicamentsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    // === helpers refresh ===
    private void reloadActes(DefaultTableModel model) {
        model.setRowCount(0);
        if (acteRepo == null) return;
        try {
            List<Acte> list = acteRepo.findAll();
            if (list != null) {
                for (Acte a : list) {
                    model.addRow(new Object[]{a.getId(), a.getLibelle(), a.getCategorie(), a.getPrixBase()});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel buildActesPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Libellé", "Catégorie", "Prix Base"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(DentalTheme.textFont(13));
        table.getTableHeader().setFont(DentalTheme.textBold(13));

        // Load Data
        reloadActes(model);

        JButton btnAdd = new JButton("Nouvel Acte");
        btnAdd.setFont(DentalTheme.textFont(13));
        btnAdd.setBackground(DentalTheme.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> createActeDialog(model));

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.setOpaque(false);
        top.add(btnAdd);

        p.add(top, BorderLayout.NORTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void createActeDialog(DefaultTableModel modelToRefresh) {
        if (acteRepo == null) {
            JOptionPane.showMessageDialog(this, "ActeRepository introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this));
        d.setTitle("Nouvel Acte");
        d.setModal(true);
        d.setSize(460, 320);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        JTextField libelleF = new JTextField();
        JTextField categorieF = new JTextField();
        JTextField prixF = new JTextField();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        d.add(new JLabel("Libellé:"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(libelleF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Catégorie:"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(categorieF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Prix base (DH):"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(prixF, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton cancel = new JButton("Annuler");
        JButton save = new JButton("Enregistrer");

        save.setBackground(DentalTheme.PRIMARY);
        save.setForeground(Color.WHITE);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                String lib = libelleF.getText() == null ? "" : libelleF.getText().trim();
                String cat = categorieF.getText() == null ? "" : categorieF.getText().trim();
                if (lib.isBlank()) {
                    JOptionPane.showMessageDialog(d, "Libellé obligatoire");
                    return;
                }
                double prix = 0.0;
                String ptxt = prixF.getText() == null ? "" : prixF.getText().trim();
                if (!ptxt.isBlank()) prix = Double.parseDouble(ptxt);

                Acte a = Acte.builder()
                        .libelle(lib)
                        .categorie(cat)
                        .prixBase(prix)
                        .build();

                acteRepo.create(a);
                d.dispose();

                // refresh
                reloadActes(modelToRefresh);

                JOptionPane.showMessageDialog(this, "Acte ajouté avec succès");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        actions.add(cancel);
        actions.add(save);

        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        d.add(actions, c);

        d.setVisible(true);
    }

    private JPanel buildMedicamentsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom", "Labo", "Prix", "Remboursable"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(DentalTheme.textFont(13));
        table.getTableHeader().setFont(DentalTheme.textBold(13));

        // Load Data
        if (medicamentRepo != null) {
            try {
                List<Medicament> list = medicamentRepo.findAll();
                for (Medicament m : list) {
                    model.addRow(new Object[]{m.getId(), m.getNom(), m.getLaboratoire(), m.getPrixUnitaire(), m.isRemboursable() ? "OUI" : "NON"});
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        JButton btnAdd = new JButton("Nouveau Médicament");
        btnAdd.setFont(DentalTheme.textFont(13));
        btnAdd.setBackground(DentalTheme.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            // Simple mockup create
            createMedicamentDialog();
        });

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.setOpaque(false);
        top.add(btnAdd);

        p.add(top, BorderLayout.NORTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void createMedicamentDialog() {
        // Quick dialog to add medicament
        JDialog d = new JDialog();
        d.setTitle("Nouveau Médicament");
        d.setModal(true);
        d.setSize(400, 300);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nomF = new JTextField();
        JTextField laboF = new JTextField();
        JTextField prixF = new JTextField();
        JCheckBox rembF = new JCheckBox("Remboursable");

        d.add(new JLabel("Nom:")); d.add(nomF);
        d.add(new JLabel("Laboratoire:")); d.add(laboF);
        d.add(new JLabel("Prix:")); d.add(prixF);
        d.add(new JLabel("")); d.add(rembF);

        JButton ok = new JButton("Enregistrer");
        ok.addActionListener(e -> {
            try {
                Medicament m = Medicament.builder()
                        .nom(nomF.getText())
                        .laboratoire(laboF.getText())
                        .prixUnitaire(Double.parseDouble(prixF.getText()))
                        .remboursable(rembF.isSelected())
                        .forme(FormeMedicament.COMPRIME) // Default
                        .build();
                medicamentRepo.create(m); // direct repo call for speed
                d.dispose();
                JOptionPane.showMessageDialog(this, "Médicament ajouté ! (Rafraichir pour voir)");
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
            }
        });
        d.add(ok);
        d.setVisible(true);
    }
}
