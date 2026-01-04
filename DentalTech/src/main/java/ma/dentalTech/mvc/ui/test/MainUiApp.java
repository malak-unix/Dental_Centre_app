package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.common.UiTheme;

import javax.swing.*;

public class MainUiApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();
            new MainFrame().setVisible(true);
        });
    }
}
