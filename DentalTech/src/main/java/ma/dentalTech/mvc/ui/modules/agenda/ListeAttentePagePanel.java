package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListeAttentePagePanel extends JPanel {

    private final ListeAttenteController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Motif", "Priorite", "Date ajout"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JLabel emptyLabel = new JLabel("Aucune entree en liste d'attente.");

    private final JTextField tfSearch = new JTextField();
    private final DentalButton btnSearch = new DentalButton("Rechercher");
    private final DentalButton btnRefresh = new DentalButton("Rafraichir");
    private final DentalButton btnAdd = new DentalButton("Ajouter");

    private final DentalButton btnProgrammer = new DentalButton("Programmer");
    private final DentalButton btnSupprimer = new DentalButton("Supprimer");

    public ListeAttentePagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (ListeAttenteController) ApplicationContext.getBean("listeAttente.controller");

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        add(card, BorderLayout.CENTER);

        card.add(buildTop(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildBottom(), BorderLayout.SOUTH);

        wireActions();
        loadAll();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Liste d'attente");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bar = new JPanel(new BorderLayout(12, 8));
        bar.setOpaque(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);
        JLabel lNom = new JLabel("Nom:");
        lNom.setFont(DentalTheme.textBold(12));
        lNom.setForeground(DentalTheme.TEXT2);
        tfSearch.setPreferredSize(new Dimension(360, 34));
        tfSearch.setFont(DentalTheme.textFont(12));
        left.add(lNom, BorderLayout.WEST);
        left.add(tfSearch, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnSearch);
        right.add(btnRefresh);
        right.add(btnAdd);

        bar.add(left, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        top.add(title);
        top.add(Box.createVerticalStrut(10));
        top.add(bar);

        return top;
    }

    private JComponent buildCenter() {
        CardPanel results = new CardPanel("Resultats");
        results.setLayout(new BorderLayout());

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        results.add(sp, BorderLayout.CENTER);
        emptyLabel.setFont(DentalTheme.textFont(12));
        emptyLabel.setForeground(DentalTheme.MUTED);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        results.add(emptyLabel, BorderLayout.SOUTH);
        return results;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);
        bottom.add(btnProgrammer);
        bottom.add(btnSupprimer);
        return bottom;
    }

    private void wireActions() {
        btnRefresh.addActionListener(e -> loadAll());

        btnSearch.addActionListener(e -> {
            String q = tfSearch.getText();
            if (q == null || q.isBlank()) {
                loadAll();
            } else {
                loadSearch(q.trim());
            }
        });

        btnAdd.addActionListener(e -> {
            AddDialog.Result res = AddDialog.open(this);
            if (res == null) return;

            ListeAttenteDto dto = ListeAttenteDto.builder()
                    .patientId(res.patientId)
                    .nom(res.nom)
                    .motif(res.motif)
                    .priorite(res.priorite)
                    .build();

            controller.create(dto);
            loadAll();
            JOptionPane.showMessageDialog(this, "Patient ajoute a la liste d'attente.", "OK", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSupprimer.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Supprimer de la liste d'attente (ID=" + id + ") ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok != JOptionPane.YES_OPTION) return;

            controller.deleteById(id);
            loadAll();
            JOptionPane.showMessageDialog(this, "Entree supprimee.", "OK", JOptionPane.INFORMATION_MESSAGE);
        });

        btnProgrammer.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            ProgramDialog.Result res = ProgramDialog.open(this);
            if (res == null) return;

            controller.programmer(id, res.patientId, res.medecinId, res.date, res.heure, res.motif);
            loadAll();
            JOptionPane.showMessageDialog(this, "RDV programme et entree retiree.", "OK", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void loadAll() {
        try {
            if (controller == null) throw new IllegalStateException("Bean listeAttente.controller introuvable (beans.properties)");
            List<ListeAttenteDto> list = controller.getAll();
            fill(list);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Liste d'attente", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSearch(String nom) {
        try {
            if (controller == null) throw new IllegalStateException("Bean listeAttente.controller introuvable (beans.properties)");
            List<ListeAttenteDto> list = controller.searchByNom(nom);
            fill(list);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur Liste d'attente", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fill(List<ListeAttenteDto> list) {
        model.setRowCount(0);
        if (list == null || list.isEmpty()) {
            emptyLabel.setVisible(true);
            return;
        }
        emptyLabel.setVisible(false);
        for (ListeAttenteDto l : list) {
            String label = (l.getPatientNom() != null && !l.getPatientNom().isBlank())
                    ? l.getPatientNom()
                    : l.getNom();
            model.addRow(new Object[]{
                    l.getId(),
                    label,
                    l.getMotif(),
                    l.getPriorite(),
                    l.getDateAjout()
            });
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selectionne une ligne d'abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    private static class AddDialog extends JDialog {
        static class Result {
            Long patientId;
            String nom;
            String motif;
            String priorite;
        }

        private Result result;

        static Result open(Component parent) {
            AddDialog d = new AddDialog(SwingUtilities.getWindowAncestor(parent));
            d.setVisible(true);
            return d.result;
        }

        AddDialog(Window owner) {
            super(owner, "Ajouter a la liste d'attente", ModalityType.APPLICATION_MODAL);
            setLayout(new BorderLayout(10, 10));

            JComboBox<PatientItem> cbPatient = new JComboBox<>(loadPatients());
            JTextField tfNom = new JTextField();
            JTextField tfMotif = new JTextField();
            JComboBox<String> cbPriorite = new JComboBox<>(new String[]{"NORMALE", "HAUTE", "BASSE"});

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Patient"));
            form.add(cbPatient);
            form.add(new JLabel("Libelle"));
            form.add(tfNom);
            form.add(new JLabel("Motif"));
            form.add(tfMotif);
            form.add(new JLabel("Priorite"));
            form.add(cbPriorite);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Annuler");
            JButton save = new JButton("Ajouter");
            actions.add(cancel);
            actions.add(save);

            cancel.addActionListener(e -> {
                result = null;
                dispose();
            });
            save.addActionListener(e -> {
                PatientItem p = (PatientItem) cbPatient.getSelectedItem();
                String nom = tfNom.getText() == null ? "" : tfNom.getText().trim();
                if (nom.isBlank() && p != null) nom = p.label;
                if (p == null && nom.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Patient ou libelle obligatoire.");
                    return;
                }
                result = new Result();
                result.patientId = p != null ? p.id : null;
                result.nom = nom;
                result.motif = tfMotif.getText() == null ? null : tfMotif.getText().trim();
                result.priorite = String.valueOf(cbPriorite.getSelectedItem());
                dispose();
            });

            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            pack();
            setSize(460, 240);
            setLocationRelativeTo(owner);
        }
    }

    private static class ProgramDialog extends JDialog {
        static class Result {
            Long patientId;
            Long medecinId;
            java.time.LocalDate date;
            java.time.LocalTime heure;
            String motif;
        }

        private Result result;

        static Result open(Component parent) {
            ProgramDialog d = new ProgramDialog(SwingUtilities.getWindowAncestor(parent));
            d.setVisible(true);
            return d.result;
        }

        ProgramDialog(Window owner) {
            super(owner, "Programmer un rendez-vous", ModalityType.APPLICATION_MODAL);
            setLayout(new BorderLayout(10, 10));

            JComboBox<PatientItem> cbPatient = new JComboBox<>(loadPatients());
            JComboBox<MedecinItem> cbMedecin = new JComboBox<>(loadMedecins());
            JTextField tfDate = new JTextField(java.time.LocalDate.now().toString());
            JComboBox<String> cbHeure = new JComboBox<>(new String[]{
                    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                    "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"
            });
            JTextField tfMotif = new JTextField("RDV");

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Patient"));
            form.add(cbPatient);
            form.add(new JLabel("Medecin"));
            form.add(cbMedecin);
            form.add(new JLabel("Date (yyyy-MM-dd)"));
            form.add(tfDate);
            form.add(new JLabel("Heure"));
            form.add(cbHeure);
            form.add(new JLabel("Motif"));
            form.add(tfMotif);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancel = new JButton("Annuler");
            JButton save = new JButton("Programmer");
            actions.add(cancel);
            actions.add(save);

            cancel.addActionListener(e -> {
                result = null;
                dispose();
            });
            save.addActionListener(e -> {
                PatientItem p = (PatientItem) cbPatient.getSelectedItem();
                MedecinItem m = (MedecinItem) cbMedecin.getSelectedItem();
                if (p == null || m == null) {
                    JOptionPane.showMessageDialog(this, "Patient et medecin obligatoires.");
                    return;
                }
                result = new Result();
                result.patientId = p.id;
                result.medecinId = m.id;
                result.date = java.time.LocalDate.parse(tfDate.getText().trim());
                result.heure = java.time.LocalTime.parse(String.valueOf(cbHeure.getSelectedItem()));
                result.motif = tfMotif.getText();
                dispose();
            });

            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            pack();
            setSize(520, 260);
            setLocationRelativeTo(owner);
        }
    }

    private static class PatientItem {
        final Long id;
        final String label;
        PatientItem(Long id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class MedecinItem {
        final Long id;
        final String label;
        MedecinItem(Long id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static DefaultComboBoxModel<PatientItem> loadPatients() {
        DefaultComboBoxModel<PatientItem> model = new DefaultComboBoxModel<>();
        try {
            var repo = new ma.dentalTech.repository.modules.patient.impl.PatientRepositoryImpl();
            var list = repo.findAll();
            if (list != null) {
                for (var p : list) {
                    String label = (p.getNom() + " " + p.getPrenom()).trim();
                    model.addElement(new PatientItem(p.getId(), label));
                }
            }
        } catch (Exception ignored) {}
        return model;
    }

    private static DefaultComboBoxModel<MedecinItem> loadMedecins() {
        DefaultComboBoxModel<MedecinItem> model = new DefaultComboBoxModel<>();
        try {
            var repo = new ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl();
            var list = repo.findAll();
            if (list != null) {
                for (var m : list) {
                    String label = (m.getNom() + " " + m.getPrenom()).trim();
                    model.addElement(new MedecinItem(m.getId(), label));
                }
            }
        } catch (Exception ignored) {}
        return model;
    }
}
