package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.ui.modules.auth.LoginFrame;

import javax.swing.*;

public class MainUiApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
