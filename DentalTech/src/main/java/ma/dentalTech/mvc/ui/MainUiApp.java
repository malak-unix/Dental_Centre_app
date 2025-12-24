package ma.dentalTech.mvc.ui;

import javax.swing.*;

public class MainUiApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
