package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.modules.auth.LoginFrame;

import javax.swing.*;

public class TestLoginUiNoDb {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            // Ouvre la fenêtre Login
            LoginFrame login = new LoginFrame() {

                // ✅ On remplace juste l’action login pour une démo sans DB
                // (on ne touche pas ton code métier / services)
                @Override
                public void setVisible(boolean b) {
                    super.setVisible(b);

                    // Optionnel : rien ici
                }

                // Petit helper local
                private void openMainAsDemo() {
                    // user fake
                    Long userId = 1L;
                    String fullName = "Admin Tech";
                    LibelleRole role = LibelleRole.ADMIN;

                    MainFrame main = new MainFrame(role, userId, fullName);
                    main.setVisible(true);
                    dispose();
                }

                // ⚠️ On intercepte juste le bouton login via RootPane default button
                // -> on écoute le bouton "Se connecter" avec un ActionMap sur Enter/clic
                {
                    getRootPane().getActionMap().put("DEMO_LOGIN", new AbstractAction() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            openMainAsDemo();
                        }
                    });

                    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                            .put(KeyStroke.getKeyStroke("ENTER"), "DEMO_LOGIN");
                }
            };

            login.setVisible(true);
        });
    }
}
