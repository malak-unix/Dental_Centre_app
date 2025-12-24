package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.ui.MainFrame;

import javax.swing.*;

public class MainUiApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
