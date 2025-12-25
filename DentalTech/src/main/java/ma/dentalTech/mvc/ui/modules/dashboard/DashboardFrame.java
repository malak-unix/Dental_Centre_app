package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;

import javax.swing.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        this(null);
    }
    public DashboardFrame(DashboardDTO dto) {
        super("DentalTech - Dashboard");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setContentPane(new DashboardPanel(dto));
    }
}
