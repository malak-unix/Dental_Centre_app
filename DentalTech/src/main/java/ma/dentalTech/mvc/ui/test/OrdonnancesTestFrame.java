package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.ordonnance.OrdonnanceListUI;

import javax.swing.*;

/**
 * Lanceur rapide pour tester l'écran "Ordonnances" (module dossier médical).
 * Utilise le workflow complet UI -> Controller -> Service -> Repository -> DB.
 */
public final class OrdonnancesTestFrame {

    private OrdonnancesTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("ordonnanceController");
            if (!(bean instanceof OrdonnanceController controller)) {
                throw new IllegalStateException("ordonnanceController introuvable dans ApplicationContext");
            }

            Long medecinId = 1L; // TODO récupérer l'utilisateur connecté
            String username = "medecin_" + medecinId;

            JFrame f = new JFrame("TEST - Ordonnances");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1450, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new OrdonnanceListUI(controller, medecinId, username));
            f.setVisible(true);
        });
    }
}
