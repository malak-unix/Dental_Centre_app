package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.certificat.CertificatListUI;

import javax.swing.*;

/**
 * Lanceur rapide pour tester l'écran "Certificats".
 */
public final class CertificatsTestFrame {

    private CertificatsTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("certificatController");
            if (!(bean instanceof CertificatController controller)) {
                throw new IllegalStateException("certificatController introuvable dans ApplicationContext");
            }

            Long medecinId = 1L;
            String username = "medecin_" + medecinId;

            JFrame f = new JFrame("TEST - Certificats");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1450, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new CertificatListUI(controller, medecinId, username));
            f.setVisible(true);
        });
    }
}
