package ma.dentalTech;

import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.common.UiTheme;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();

            //  Tant que le LoginPanel n'existe pas : mode demo
            MainFrame f = new MainFrame();

            //  Plus tard, après login :
            // MainFrame f = new MainFrame(role, userId);

            f.setVisible(true);
        });
    }
}
