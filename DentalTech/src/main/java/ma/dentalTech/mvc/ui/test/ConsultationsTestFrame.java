package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.ui.modules.dossierMedicale.consultation.ConsultationPagePanel;

import javax.swing.*;

/**
 * Lanceur temporaire (sans module roles/auth).
 * Permet de tester l'ecran "Mes consultations" rapidement.
 *
 * Notes:
 * - medecinId est hardcode (1L) en attendant l'integration login/roles.
 * - L'ecran utilise le workflow UI -> Controller -> Service -> Repo -> DB.
 */
public final class ConsultationsTestFrame {

    private ConsultationsTestFrame() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object bean = ApplicationContext.getBean("consultationController");
            if (!(bean instanceof ConsultationController controller)) {
                throw new IllegalStateException("consultationController introuvable dans ApplicationContext");
            }

            Long medecinId = 1L; // TODO remplacer par l'utilisateur connecte

            JFrame f = new JFrame("TEST - Mes consultations");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1450, 900);
            f.setLocationRelativeTo(null);
            f.setContentPane(new ConsultationPagePanel(controller, medecinId));
            f.setVisible(true);
        });
    }
}
