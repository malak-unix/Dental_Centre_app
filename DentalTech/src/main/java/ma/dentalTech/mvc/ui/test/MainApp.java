package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.ui.MainFrame;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Look&Feel système (optionnel)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame f = new MainFrame();
            f.setVisible(true); // ✅ sinon rien ne s'affiche
        });
    }
}
