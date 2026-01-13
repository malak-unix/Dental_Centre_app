package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.dossier.DossierMedicalListUI;

import javax.swing.*;

/**
 * Lanceur rapide pour tester l'écran "Dossiers médicaux".
 */
public final class DossiersTestFrame {

    private DossiersTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("dossierMedicalController");
            if (!(bean instanceof DossierMedicalController controller)) {
                throw new IllegalStateException("dossierMedicalController introuvable dans ApplicationContext");
            }

            Long medecinId = 1L; // peut être null pour tout afficher
            String username = "medecin_" + medecinId;

            JFrame f = new JFrame("TEST - Dossiers médicaux");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1450, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new DossierMedicalListUI(controller, medecinId, username));
            f.setVisible(true);
        });
    }
}
