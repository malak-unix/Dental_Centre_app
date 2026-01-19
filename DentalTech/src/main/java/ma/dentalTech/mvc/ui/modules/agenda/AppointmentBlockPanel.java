package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;

public class AppointmentBlockPanel extends JPanel {

    private final RdvDto rdv;
    private boolean selected = false;

    public AppointmentBlockPanel(RdvDto rdv) {
        this.rdv = rdv;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(6, 8, 6, 8));

        JLabel time = new JLabel(formatTimeRange(rdv));
        time.setFont(DentalTheme.textBold(11));
        time.setForeground(DentalTheme.TEXT2);

        String patient = rdv != null && rdv.getPatientNom() != null && !rdv.getPatientNom().isBlank()
                ? rdv.getPatientNom()
                : ("Patient #" + (rdv != null ? rdv.getPatientId() : ""));
        JLabel name = new JLabel(patient);
        name.setFont(DentalTheme.textFont(11));
        name.setForeground(DentalTheme.TEXT2);

        JLabel status = new JLabel(statusLabel(rdv));
        status.setFont(DentalTheme.textBold(10));
        status.setForeground(DentalTheme.TEXT2);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(time);
        box.add(Box.createVerticalStrut(2));
        box.add(name);
        box.add(Box.createVerticalStrut(4));
        box.add(status);

        add(box, BorderLayout.CENTER);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public LocalTime getStartTime() {
        return rdv != null ? rdv.getHeure() : null;
    }

    public RdvDto getRdv() {
        return rdv;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 14;
        int w = getWidth();
        int h = getHeight();

        Color fill = colorForStatus(rdv);
        if (selected) fill = fill.darker();

        g2.setColor(new Color(0, 0, 0, 18));
        g2.fillRoundRect(3, 3, w - 6, h - 6, arc, arc);

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

        g2.setColor(DentalTheme.STROKE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }

    private String formatTimeRange(RdvDto r) {
        if (r == null || r.getHeure() == null) return "--:--";
        LocalTime start = r.getHeure();
        LocalTime end = start.plusMinutes(30);
        return start + " - " + end;
    }

    private String statusLabel(RdvDto r) {
        if (r == null || r.getStatut() == null) return "";
        EtatRendezVous st = r.getStatut();
        return switch (st) {
            case CONFIRME -> "Confirme";
            case PLANIFIE -> "En attente";
            case ANNULE -> "Annule";
            case TERMINE -> "Termine";
        };
    }

    private Color colorForStatus(RdvDto r) {
        if (r == null || r.getStatut() == null) return new Color(0xF7, 0xF2, 0xEC);
        return switch (r.getStatut()) {
            case CONFIRME -> new Color(0xD6, 0xF0, 0xE0);
            case PLANIFIE -> new Color(0xF8, 0xE6, 0xCC);
            case ANNULE -> new Color(0xF1, 0xD6, 0xD6);
            case TERMINE -> new Color(0xD8, 0xE6, 0xF8);
        };
    }
}
