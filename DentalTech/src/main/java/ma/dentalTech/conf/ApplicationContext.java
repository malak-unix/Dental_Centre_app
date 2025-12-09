package ma.dentalTech.conf;

import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

// Module Caisse
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseDashboardController;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

// Module RDV
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;
import ma.dentalTech.service.modules.rdv.api.RdvService;
import ma.dentalTech.mvc.controllers.modules.rdv.api.RdvController;

// Module Agenda
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {

        String currentBean = "aucun";

        try {
            // ===============================
            // Chargement du fichier properties
            // ===============================
            Properties properties = new Properties();

            try (InputStream in = ApplicationContext.class.getResourceAsStream("/config/beans.properties")) {
                if (in == null)
                    throw new IllegalStateException("Impossible de trouver /config/beans.properties dans le classpath");

                properties.load(in);
            }

            // ============================================================
            // 1. MODULE PATIENT
            // ============================================================
            currentBean = "patientRepo";
            PatientRepository patientRepository =
                    (PatientRepository) Class.forName(properties.getProperty("patientRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "patientService";
            PatientService patientService =
                    (PatientService) Class.forName(properties.getProperty("patientService"))
                            .getDeclaredConstructor(PatientRepository.class)
                            .newInstance(patientRepository);

            currentBean = "patientController";
            PatientController patientController =
                    (PatientController) Class.forName(properties.getProperty("patientController"))
                            .getDeclaredConstructor(PatientService.class)
                            .newInstance(patientService);

            context.put(PatientRepository.class, patientRepository);
            context.put(PatientService.class, patientService);
            context.put(PatientController.class, patientController);

            contextByName.put("patientRepo", patientRepository);
            contextByName.put("patientService", patientService);
            contextByName.put("patientController", patientController);

            // ============================================================
            // 2. MODULE CAISSE
            // ============================================================
            currentBean = "factureRepo";
            FactureRepository factureRepository =
                    (FactureRepository) Class.forName(properties.getProperty("factureRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "chargesRepo";
            ChargesRepository chargesRepository =
                    (ChargesRepository) Class.forName(properties.getProperty("chargesRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "revenusRepo";
            RevenuesRepository revenusRepository =
                    (RevenuesRepository) Class.forName(properties.getProperty("revenusRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "sitFinRepo";
            SituationFinanciereRepository sitFinRepository =
                    (SituationFinanciereRepository) Class.forName(properties.getProperty("sitFinRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "caisseDashboardService";
            CaisseDashboardService caisseService =
                    (CaisseDashboardService)
                            Class.forName(properties.getProperty("caisseDashboardService"))
                                    .getDeclaredConstructor(FactureRepository.class, RevenuesRepository.class, ChargesRepository.class)
                                    .newInstance(factureRepository, revenusRepository, chargesRepository);

            currentBean = "caisseDashboardController";
            CaisseDashboardController caisseController =
                    (CaisseDashboardController)
                            Class.forName(properties.getProperty("caisseDashboardController"))
                                    .getDeclaredConstructor(CaisseDashboardService.class)
                                    .newInstance(caisseService);

            context.put(FactureRepository.class, factureRepository);
            context.put(ChargesRepository.class, chargesRepository);
            context.put(RevenuesRepository.class, revenusRepository);
            context.put(SituationFinanciereRepository.class, sitFinRepository);
            context.put(CaisseDashboardService.class, caisseService);
            context.put(CaisseDashboardController.class, caisseController);

            contextByName.put("factureRepo", factureRepository);
            contextByName.put("chargesRepo", chargesRepository);
            contextByName.put("revenusRepo", revenusRepository);
            contextByName.put("sitFinRepo", sitFinRepository);
            contextByName.put("caisseDashboardService", caisseService);
            contextByName.put("caisseDashboardController", caisseController);

            // ============================================================
            // 3. MODULE RDV
            // ============================================================
            currentBean = "rdv.repository";
            RdvRepository rdvRepository =
                    (RdvRepository) Class.forName(properties.getProperty("rdv.repository"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "rdv.service";
            RdvService rdvService =
                    (RdvService) Class.forName(properties.getProperty("rdv.service"))
                            .getDeclaredConstructor(RdvRepository.class)
                            .newInstance(rdvRepository);

            currentBean = "rdv.controller";
            RdvController rdvController =
                    (RdvController) Class.forName(properties.getProperty("rdv.controller"))
                            .getDeclaredConstructor(RdvService.class)
                            .newInstance(rdvService);

            context.put(RdvRepository.class, rdvRepository);
            context.put(RdvService.class, rdvService);
            context.put(RdvController.class, rdvController);

            contextByName.put("rdv.repository", rdvRepository);
            contextByName.put("rdv.service", rdvService);
            contextByName.put("rdv.controller", rdvController);

            // ============================================================
            // 4. MODULE AGENDA
            // ============================================================
            currentBean = "agendaMensuelRepo";
            AgendaMensuelRepository agendaMensuelRepo =
                    (AgendaMensuelRepository) Class.forName(properties.getProperty("agendaMensuelRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "detailJourneeRepo";
            DetailJourneeRepository detailJourneeRepo =
                    (DetailJourneeRepository) Class.forName(properties.getProperty("detailJourneeRepo"))
                            .getDeclaredConstructor().newInstance();

            currentBean = "agendaService";
            AgendaService agendaService =
                    (AgendaService)
                            Class.forName(properties.getProperty("agendaService"))
                                    .getDeclaredConstructor(AgendaMensuelRepository.class, DetailJourneeRepository.class)
                                    .newInstance(agendaMensuelRepo, detailJourneeRepo);

            context.put(AgendaMensuelRepository.class, agendaMensuelRepo);
            context.put(DetailJourneeRepository.class, detailJourneeRepo);
            context.put(AgendaService.class, agendaService);

            contextByName.put("agendaMensuelRepo", agendaMensuelRepo);
            contextByName.put("detailJourneeRepo", detailJourneeRepo);
            contextByName.put("agendaService", agendaService);


        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur lors de l'initialisation de l'ApplicationContext (bean courant = "
                    + currentBean + ")", e);
        }
    }

    private ApplicationContext() {}

    public static Object getBean(String name) {
        return contextByName.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) context.get(clazz);
    }
}
