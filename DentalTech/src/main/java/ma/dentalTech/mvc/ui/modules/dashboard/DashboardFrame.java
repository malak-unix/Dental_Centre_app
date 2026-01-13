package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.enums.LibelleRole;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        this(LibelleRole.SECRETAIRE, 1L); // valeurs demo
    }

    public DashboardFrame(LibelleRole role, Long userId) {
        super("Dental Center");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // navigate demo (ne fait rien)
        setContentPane(new DashboardView(role, userId, k -> {}));
    }

    // ✅ compat MainApp (si quelqu’un l’appelle avec un context)
    public DashboardFrame(Object ignoredContext) {
        this();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
