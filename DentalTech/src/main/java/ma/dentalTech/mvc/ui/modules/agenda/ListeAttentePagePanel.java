package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.time.LocalDate;
import java.time.LocalTime;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;

public class ListeAttentePagePanel extends JPanel {

    private final ListeAttenteController controller;

    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);

    private final JTextField tfSearch = new JTextField();
    private final DentalButton btnSearch = new DentalButton("Rechercher");
    private final DentalButton btnRefresh = new DentalButton("Rafraîchir");

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

        // gauche : recherche
        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);

        JLabel lNom = new JLabel("Nom:");
        lNom.setFont(DentalTheme.textBold(12));
        lNom.setForeground(DentalTheme.TEXT2);

        tfSearch.setPreferredSize(new Dimension(360, 34));
        tfSearch.setFont(DentalTheme.textFont(12));

        left.add(lNom, BorderLayout.WEST);
        left.add(tfSearch, BorderLayout.CENTER);

        // droite : boutons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnSearch);
        right.add(btnRefresh);

        bar.add(left, BorderLayout.CENTER);
        bar.add(right, BorderLayout.EAST);

        top.add(title);
        top.add(Box.createVerticalStrut(10));
        top.add(bar);

        return top;
    }

    private JComponent buildCenter() {
        CardPanel results = new CardPanel("Résultats");
        results.setLayout(new BorderLayout());

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        results.add(sp, BorderLayout.CENTER);
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
            if (q == null || q.isBlank()) loadAll();
            else loadSearch(q.trim());
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

            // ✅ Appel dynamique: supprimer(Long) si tu l’ajoutes plus tard
            if (!invokeIfExists(controller, new String[]{"supprimer", "delete", "remove"},
                    new Class[]{Long.class}, new Object[]{id})) {

                JOptionPane.showMessageDialog(
                        this,
                        "Action non disponible.\nAjoute une méthode supprimer(Long id) dans ListeAttenteController + Service.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            loadAll();
        });

        btnProgrammer.addActionListener(e -> {
            try {
                Long idListe = selectedId();
                if (idListe == null) return;

                ProgrammerDepuisListeAttenteDialog dlg =
                        new ProgrammerDepuisListeAttenteDialog(SwingUtilities.getWindowAncestor(this), idListe);

                dlg.setVisible(true);

                RdvDto rdv = dlg.getResult();
                if (rdv == null) return;

                // ✅ on passe par le controller ListeAttente (méthode programmer)
                controller.programmer(idListe, rdv);

                JOptionPane.showMessageDialog(this, "RDV programmé ✅", "Info", JOptionPane.INFORMATION_MESSAGE);
                loadAll();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
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
        if (list == null) return;
        for (ListeAttenteDto l : list) {
            model.addRow(new Object[]{l.getId(), l.getNom()});
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne d’abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    private static boolean invokeIfExists(Object target, String[] names, Class<?>[] types, Object[] args) {
        if (target == null) return false;
        for (String n : names) {
            try {
                Method m = target.getClass().getMethod(n, types);
                m.invoke(target, args);
                return true;
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return false;
    }

    // =========================================================
    // ✅ Dialog : Programmer depuis la liste d'attente
    // =========================================================
    private static class ProgrammerDepuisListeAttenteDialog extends JDialog {

        private final JTextField tfListeId = new JTextField();
        private final JTextField tfPatientId = new JTextField();
        private final JTextField tfDetailJourneeId = new JTextField();
        private final JTextField tfDate = new JTextField();   // yyyy-MM-dd
        private final JTextField tfHeure = new JTextField();  // HH:mm
        private final JTextField tfMotif = new JTextField();
        private final JTextField tfNote = new JTextField();
        private final JComboBox<EtatRendezVous> cbStatut = new JComboBox<>(EtatRendezVous.values());

        private RdvDto result;

        ProgrammerDepuisListeAttenteDialog(Window owner, Long idListeAttente) {
            super(owner, "Programmer depuis la liste d'attente", ModalityType.APPLICATION_MODAL);

            setSize(560, 380);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(12, 12));

            tfListeId.setEnabled(false);
            tfListeId.setText(String.valueOf(idListeAttente));

            add(buildForm(), BorderLayout.CENTER);
            add(buildActions(), BorderLayout.SOUTH);

            cbStatut.setSelectedItem(EtatRendezVous.PLANIFIE);
            tfHeure.setText("09:00");
            tfDate.setText(LocalDate.now().toString());
        }

        RdvDto getResult() { return result; }

        private JComponent buildForm() {
            JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            p.add(new JLabel("ListeAttente ID"));
            p.add(tfListeId);

            p.add(new JLabel("Patient ID *"));
            p.add(tfPatientId);

            p.add(new JLabel("DetailJournee ID *"));
            p.add(tfDetailJourneeId);

            p.add(new JLabel("Date (yyyy-MM-dd) *"));
            p.add(tfDate);

            p.add(new JLabel("Heure (HH:mm) *"));
            p.add(tfHeure);

            p.add(new JLabel("Motif *"));
            p.add(tfMotif);

            p.add(new JLabel("Statut"));
            p.add(cbStatut);

            p.add(new JLabel("Note médecin (optionnel)"));
            p.add(tfNote);

            return p;
        }

        private JComponent buildActions() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton btnCancel = new JButton("Annuler");
            JButton btnOk = new JButton("Programmer");

            btnCancel.addActionListener(e -> { result = null; dispose(); });

            btnOk.addActionListener(e -> {
                try {
                    result = readDto();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.ERROR_MESSAGE);
                }
            });

            p.add(btnCancel);
            p.add(btnOk);
            return p;
        }

        private RdvDto readDto() {
            Long patientId = parseLongRequired(tfPatientId.getText(), "patientId obligatoire");
            Long detailJourneeId = parseLongRequired(tfDetailJourneeId.getText(), "detailJourneeId obligatoire");

            String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();
            String heureStr = tfHeure.getText() == null ? "" : tfHeure.getText().trim();
            String motif = tfMotif.getText() == null ? "" : tfMotif.getText().trim();
            String note = tfNote.getText() == null ? "" : tfNote.getText().trim();

            if (dateStr.isBlank()) throw new IllegalArgumentException("date obligatoire (yyyy-MM-dd)");
            if (heureStr.isBlank()) throw new IllegalArgumentException("heure obligatoire (HH:mm)");
            if (motif.isBlank()) throw new IllegalArgumentException("motif obligatoire");

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heure = LocalTime.parse(heureStr);

            EtatRendezVous statut = (EtatRendezVous) cbStatut.getSelectedItem();
            if (statut == null) statut = EtatRendezVous.PLANIFIE;

            return RdvDto.builder()
                    .id(null)
                    .patientId(patientId)
                    .detailJourneeId(detailJourneeId)
                    .listeAttenteId(null) // forcé côté service/controller
                    .dateRdv(date)
                    .heure(heure)
                    .motif(motif)
                    .statut(statut)
                    .noteMedecin(note.isBlank() ? null : note)
                    .typeRdv(null)
                    .patientNom(null)
                    .build();
        }

        private Long parseLongRequired(String s, String msg) {
            Long v = parseLongOrNull(s);
            if (v == null || v <= 0) throw new IllegalArgumentException(msg);
            return v;
        }

        private Long parseLongOrNull(String s) {
            if (s == null) return null;
            String t = s.trim();
            if (t.isBlank()) return null;
            return Long.parseLong(t);
        }
    }
}
