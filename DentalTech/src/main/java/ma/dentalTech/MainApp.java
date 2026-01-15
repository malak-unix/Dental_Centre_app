package ma.dentalTech;

import ma.dentalTech.mvc.ui.common.UiTheme;
import ma.dentalTech.mvc.ui.modules.auth.LoginFrame;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();
            ma.dentalTech.configuration.DatabaseInitializer.initialize();
            new LoginFrame().setVisible(true);
        });
    }
}
