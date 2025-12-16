package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        super("DentalTech - Dashboard");

        // Configuration fenêtre principale
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Ajout du panel principal (DashboardPanel)
        DashboardPanel panel = new DashboardPanel();
        setContentPane(panel);
    }
}
