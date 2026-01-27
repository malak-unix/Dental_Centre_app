package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.NiveauDeRisque;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiStyles;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReferentielsPanel extends JPanel {

    private final ActeRepository acteRepo;
    private final MedicamentRepository medicamentRepo;
    private final AntecedentRepository antecedentRepo;

    public ReferentielsPanel() {
        this.acteRepo = ApplicationContext.getBean(ActeRepository.class);
        this.medicamentRepo = ApplicationContext.getBean(MedicamentRepository.class);
        this.antecedentRepo = ApplicationContext.getBean(AntecedentRepository.class);

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
        tabs.addTab("Antécédents", buildAntecedentsPanel());

        add(tabs, BorderLayout.CENTER);
    }

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

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Libelle", "Categorie", "Prix Base"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UiStyles.styleTable(table);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        reloadActes(model);

        DentalButton btnAdd = new DentalButton("Nouvel Acte");
        UiStyles.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> createActeDialog(model));

        DentalButton btnDelete = new DentalButton("Supprimer l'acte sélectionné");
        UiStyles.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> deleteSelectedActe(table, model));

        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(sp, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        top.setOpaque(false);
        top.add(btnAdd);
        top.add(btnDelete);

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
        d.add(new JLabel("Libelle:"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(libelleF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Categorie:"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(categorieF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Prix base (DH):"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(prixF, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actions.setOpaque(false);

        DentalButton cancel = new DentalButton("Annuler");
        DentalButton save = new DentalButton("Enregistrer");
        UiStyles.styleSecondaryButton(cancel);
        UiStyles.stylePrimaryButton(save);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                String lib = libelleF.getText() == null ? "" : libelleF.getText().trim();
                String cat = categorieF.getText() == null ? "" : categorieF.getText().trim();
                if (lib.isBlank()) {
                    JOptionPane.showMessageDialog(d, "Libelle obligatoire");
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
                reloadActes(modelToRefresh);
                JOptionPane.showMessageDialog(this, "Acte ajoute avec succes");
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

    private void deleteSelectedActe(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "ID d'acte invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Long id = Long.valueOf(idObj.toString());
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer l'acte #" + id + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            acteRepo.deleteById(id);
            reloadActes(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildMedicamentsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom", "Labo", "Prix", "Remboursable"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UiStyles.styleTable(table);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        reloadMedicaments(model);

        DentalButton btnAdd = new DentalButton("Nouveau Medicament");
        UiStyles.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> createMedicamentDialog(model));

        DentalButton btnDelete = new DentalButton("Supprimer le medicament sélectionné");
        UiStyles.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> deleteSelectedMedicament(table, model));

        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(sp, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        top.setOpaque(false);
        top.add(btnAdd);
        top.add(btnDelete);

        p.add(top, BorderLayout.NORTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void reloadMedicaments(DefaultTableModel model) {
        model.setRowCount(0);
        if (medicamentRepo == null) return;
        try {
            List<Medicament> list = medicamentRepo.findAll();
            if (list != null) {
                for (Medicament m : list) {
                    model.addRow(new Object[]{m.getId(), m.getNom(), m.getLaboratoire(),
                            m.getPrixUnitaire(), m.isRemboursable() ? "OUI" : "NON"});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMedicamentDialog(DefaultTableModel modelToRefresh) {
        if (medicamentRepo == null) {
            JOptionPane.showMessageDialog(this, "MedicamentRepository introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this));
        d.setTitle("Nouveau Medicament");
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

        DentalButton ok = new DentalButton("Enregistrer");
        DentalButton cancel = new DentalButton("Annuler");
        UiStyles.stylePrimaryButton(ok);
        UiStyles.styleSecondaryButton(cancel);

        ok.addActionListener(e -> {
            try {
                String nom = nomF.getText() == null ? "" : nomF.getText().trim();
                if (nom.isBlank()) {
                    JOptionPane.showMessageDialog(d, "Nom obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String labo = laboF.getText() == null ? "" : laboF.getText().trim();
                double prix = 0.0;
                String ptxt = prixF.getText() == null ? "" : prixF.getText().trim();
                if (!ptxt.isBlank()) prix = Double.parseDouble(ptxt);

                Medicament m = Medicament.builder()
                        .nom(nom)
                        .laboratoire(labo)
                        .prixUnitaire(prix)
                        .remboursable(rembF.isSelected())
                        .build();

                medicamentRepo.create(m);
                d.dispose();
                reloadMedicaments(modelToRefresh);
                JOptionPane.showMessageDialog(this, "Médicament ajouté avec succès");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> d.dispose());

        d.add(cancel);
        d.add(ok);
        d.setVisible(true);
    }

    private void deleteSelectedMedicament(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un medicament à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "ID de medicament invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Long id = Long.valueOf(idObj.toString());
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer le medicament #" + id + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            medicamentRepo.deleteById(id);
            reloadMedicaments(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================================================
    // Onglet Antécédents
    // ======================================================
    private JPanel buildAntecedentsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Patient ID", "Nom", "Catégorie", "Niveau de risque"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UiStyles.styleTable(table);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        reloadAntecedents(model);

        DentalButton btnAdd = new DentalButton("Nouvel antécédent");
        UiStyles.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> createAntecedentDialog(model));

        DentalButton btnDelete = new DentalButton("Supprimer l'antécédent sélectionné");
        UiStyles.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> deleteSelectedAntecedent(table, model));

        CardPanel card = new CardPanel(null);
        card.setBackground(DentalTheme.CARD);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(sp, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        top.setOpaque(false);
        top.add(btnAdd);
        top.add(btnDelete);

        p.add(top, BorderLayout.NORTH);
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private void reloadAntecedents(DefaultTableModel model) {
        model.setRowCount(0);
        if (antecedentRepo == null) return;
        try {
            List<Antecedents> list = antecedentRepo.findAll();
            if (list != null) {
                for (Antecedents a : list) {
                    model.addRow(new Object[]{
                            a.getId(),
                            a.getPatientId(),
                            a.getNom(),
                            a.getCategorie(),
                            a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : ""
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAntecedentDialog(DefaultTableModel modelToRefresh) {
        if (antecedentRepo == null) {
            JOptionPane.showMessageDialog(this, "AntecedentRepository introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this));
        d.setTitle("Nouvel antécédent");
        d.setModal(true);
        d.setSize(480, 320);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());

        JTextField patientIdF = new JTextField();
        JTextField nomF = new JTextField();
        JTextField categorieF = new JTextField();
        JComboBox<NiveauDeRisque> niveauF = new JComboBox<>(NiveauDeRisque.values());
        JTextField descF = new JTextField();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        d.add(new JLabel("Patient ID *"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(patientIdF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Nom"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(nomF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Catégorie"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(categorieF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Niveau de risque"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(niveauF, c);

        c.gridx = 0; c.gridy++; c.weightx = 0;
        d.add(new JLabel("Description"), c);
        c.gridx = 1; c.weightx = 1.0;
        d.add(descF, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        actions.setOpaque(false);
        DentalButton cancel = new DentalButton("Annuler");
        DentalButton save = new DentalButton("Enregistrer");
        UiStyles.styleSecondaryButton(cancel);
        UiStyles.stylePrimaryButton(save);

        cancel.addActionListener(e -> d.dispose());
        save.addActionListener(e -> {
            try {
                String pidText = patientIdF.getText() == null ? "" : patientIdF.getText().trim();
                if (pidText.isBlank()) {
                    JOptionPane.showMessageDialog(d, "Patient ID obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Long pid = Long.parseLong(pidText);

                Antecedents a = Antecedents.builder()
                        .patientId(pid)
                        .nom(nomF.getText())
                        .categorie(categorieF.getText())
                        .niveauDeRisque((NiveauDeRisque) niveauF.getSelectedItem())
                        .description(descF.getText())
                        .build();

                antecedentRepo.create(a);
                d.dispose();
                reloadAntecedents(modelToRefresh);
                JOptionPane.showMessageDialog(this, "Antécédent ajouté avec succès");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        actions.add(cancel);
        actions.add(save);
        d.add(actions, c);

        d.setVisible(true);
    }

    private void deleteSelectedAntecedent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un antécédent à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null) {
            JOptionPane.showMessageDialog(this, "ID d'antécédent invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Long id = Long.valueOf(idObj.toString());
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer l'antécédent #" + id + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            antecedentRepo.deleteById(id);
            reloadAntecedents(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
