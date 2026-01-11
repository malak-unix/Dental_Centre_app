package ma.dentalTech.mvc.ui.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.service.modules.agenda.api.AgendaAppService;

public class AppSmokeTest {
    public static void main(String[] args) {

        System.out.println("=== SMOKE TEST ApplicationContext ===");

        Object patientCtrl = ApplicationContext.getBean("patientController");
        System.out.println("patientController = " + patientCtrl);

        PatientController pc = (PatientController) ApplicationContext.getBean("patientController");
        System.out.println("patientController cast OK => " + (pc != null));

        AgendaAppService agendaApp = ApplicationContext.getBean(AgendaAppService.class);
        System.out.println("agendaAppService = " + agendaApp);

        System.out.println("✅ SMOKE TEST OK");
    }
}
