package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class RdvEditDialog extends JDialog {

    private final JTextField tfId = new JTextField();
    private final JTextField tfPatientId = new JTextField();
    private final JTextField tfDetailJourneeId = new JTextField();
    private final JTextField tfListeAttenteId = new JTextField();
    private final JTextField tfDate = new JTextField();   // yyyy-MM-dd
    private final JTextField tfHeure = new JTextField();  // HH:mm
    private final JTextField tfMotif = new JTextField();
    private final JTextField tfNote = new JTextField();
    private final JComboBox<EtatRendezVous> cbStatut = new JComboBox<>(EtatRendezVous.values());

    private RdvDto result;

    public RdvEditDialog(Window owner, RdvDto initial) {
        super(owner, (initial == null ? "Ajouter RDV" : "Modifier RDV"), ModalityType.APPLICATION_MODAL);

        setSize(520, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(12, 12));

        add(buildForm(initial), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        if (initial != null) fill(initial);
    }

    public RdvDto getResult() {
        return result;
    }

    private JComponent buildForm(RdvDto initial) {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tfId.setEnabled(false);

        p.add(new JLabel("ID (auto)"));
        p.add(tfId);

        p.add(new JLabel("Patient ID *"));
        p.add(tfPatientId);

        p.add(new JLabel("DetailJournee ID *"));
        p.add(tfDetailJourneeId);

        p.add(new JLabel("ListeAttente ID (optionnel)"));
        p.add(tfListeAttenteId);

        p.add(new JLabel("Date RDV (yyyy-MM-dd) *"));
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
        JButton btnOk = new JButton("Enregistrer");

        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });

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

    private void fill(RdvDto d) {
        if (d.getId() != null) tfId.setText(String.valueOf(d.getId()));
        if (d.getPatientId() != null) tfPatientId.setText(String.valueOf(d.getPatientId()));
        if (d.getDetailJourneeId() != null) tfDetailJourneeId.setText(String.valueOf(d.getDetailJourneeId()));
        if (d.getListeAttenteId() != null) tfListeAttenteId.setText(String.valueOf(d.getListeAttenteId()));
        if (d.getDateRdv() != null) tfDate.setText(d.getDateRdv().toString());
        if (d.getHeure() != null) tfHeure.setText(d.getHeure().toString());
        if (d.getMotif() != null) tfMotif.setText(d.getMotif());
        if (d.getStatut() != null) cbStatut.setSelectedItem(d.getStatut());
        if (d.getNoteMedecin() != null) tfNote.setText(d.getNoteMedecin());
    }

    private RdvDto readDto() {
        Long id = parseLongOrNull(tfId.getText());
        Long patientId = parseLongRequired(tfPatientId.getText(), "patientId obligatoire");
        Long detailJourneeId = parseLongRequired(tfDetailJourneeId.getText(), "detailJourneeId obligatoire");
        Long listeAttenteId = parseLongOrNull(tfListeAttenteId.getText());

        String dateStr = tfDate.getText() == null ? "" : tfDate.getText().trim();
        String heureStr = tfHeure.getText() == null ? "" : tfHeure.getText().trim();
        String motif = tfMotif.getText() == null ? "" : tfMotif.getText().trim();
        String note = tfNote.getText() == null ? "" : tfNote.getText().trim();

        if (dateStr.isBlank()) throw new IllegalArgumentException("dateRdv obligatoire (yyyy-MM-dd)");
        if (heureStr.isBlank()) throw new IllegalArgumentException("heure obligatoire (HH:mm)");
        if (motif.isBlank()) throw new IllegalArgumentException("motif obligatoire");

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime heure = LocalTime.parse(heureStr);

        EtatRendezVous statut = (EtatRendezVous) cbStatut.getSelectedItem();
        if (statut == null) statut = EtatRendezVous.PLANIFIE;

        return RdvDto.builder()
                .id(id)
                .patientId(patientId)
                .detailJourneeId(detailJourneeId)
                .listeAttenteId(listeAttenteId)
                .dateRdv(date)
                .heure(heure)
                .motif(motif)
                .statut(statut)
                .noteMedecin(note.isBlank() ? null : note)
                .typeRdv(null) // non utilisé ici
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
