package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaHomePanel;

import javax.swing.*;

public class AgendaMaintest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("DentalSoft - Agenda");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.getContentPane().setBackground(DentalTheme.BG);
            f.setContentPane(new AgendaHomePanel());
            f.setSize(1100, 700);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
