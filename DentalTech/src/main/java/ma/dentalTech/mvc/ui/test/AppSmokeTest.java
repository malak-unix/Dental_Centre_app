package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

public class AppSmokeTest {

    public static void main(String[] args) {

        // 1) Essai par TYPE
        PatientController c = ApplicationContext.getBean(PatientController.class);

        // 2) Fallback par NOM (les 3 clés les plus probables)
        if (c == null) c = (PatientController) ApplicationContext.getBean("patientController");
        if (c == null) c = (PatientController) ApplicationContext.getBean("patient.controller");
        if (c == null) c = (PatientController) ApplicationContext.getBean("patientControllerSwing"); // si tu testes swing

        if (c == null) {
            throw new IllegalStateException(
                    "PatientController introuvable dans ApplicationContext.\n" +
                            "Vérifie /config/beans.properties (resource) contient:\n" +
                            "patientController = ...PatientControllerImpl\n" +
                            "ou patient.controller = ...PatientControllerImpl\n"
            );
        }

        System.out.println("✅ PatientController trouvé: " + c.getClass().getName());
        System.out.println("Patients count = " + c.lister().size());
        System.out.println("✅ AppSmokeTest OK");
    }
}
