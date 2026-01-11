package ma.dentalTech;

import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.common.UiTheme;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
