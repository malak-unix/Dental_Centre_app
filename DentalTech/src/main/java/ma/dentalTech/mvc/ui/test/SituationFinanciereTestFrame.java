package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.situationFinanciere.SituationFinanciereListUI;

import javax.swing.*;

/**
 * Lanceur rapide pour tester l'écran "Situation financière".
 */
public final class SituationFinanciereTestFrame {

    private SituationFinanciereTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("situationFinanciereController");
            if (!(bean instanceof SituationFinanciereController controller)) {
                throw new IllegalStateException("situationFinanciereController introuvable dans ApplicationContext");
            }

            Long medecinId = 1L;
            String username = "medecin_" + medecinId;

            JFrame f = new JFrame("TEST - Situation financière");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1450, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new SituationFinanciereListUI(controller, medecinId, username));
            f.setVisible(true);
        });
    }
}
