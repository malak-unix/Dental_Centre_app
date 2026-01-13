package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.acte.ActeListUI;

import javax.swing.*;

/**
 * Lanceur rapide pour tester l'écran "Actes".
 */
public final class ActesTestFrame {

    private ActesTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("acteController");
            if (!(bean instanceof ActeController controller)) {
                throw new IllegalStateException("acteController introuvable dans ApplicationContext");
            }

            String username = "medecin_1"; // TODO récupérer l'utilisateur connecté

            JFrame f = new JFrame("TEST - Actes");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1200, 800);
            f.setLocationRelativeTo(null);
            f.setContentPane(new ActeListUI(controller, username));
            f.setVisible(true);
        });
    }
}
