package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        super("Dental Center");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setContentPane(new DashboardView());
    }

    // ✅ COMPATIBILITÉ MainApp
    public DashboardFrame(Object ignoredContext) {
        this();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
