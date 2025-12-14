package ma.dentalTech.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.patient.api.PatientService;

public class ContextSmokeTest {
    public static void main(String[] args) {
        PatientService ps = ApplicationContext.getBean(PatientService.class);
        System.out.println("PatientService = " + ps);

        System.out.println("Has rdv.controller? " + ApplicationContext.hasBean("rdv.controller"));
    }
}
