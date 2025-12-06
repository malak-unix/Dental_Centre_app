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

// Module RDV / Agenda
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;
import ma.dentalTech.service.modules.rdv.api.RdvService;
import ma.dentalTech.mvc.controllers.modules.rdv.api.RdvController;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        try {
            Properties properties = new Properties();

            // Chargement du fichier beans.properties depuis le classpath
            try (InputStream in = ApplicationContext.class.getResourceAsStream("/config/beans.properties")) {
                if (in == null) {
                    throw new IllegalStateException("Impossible de trouver /config/beans.properties dans le classpath");
                }
                properties.load(in);
            }

            // ============================================================
            //  1. MODULE PATIENT
            // ============================================================

            // Repository Patient
            String patientRepoClassName = properties.getProperty("patientRepo");
            Class<?> cPatientRepo = Class.forName(patientRepoClassName);
            PatientRepository patientRepository = (PatientRepository)
                    cPatientRepo.getDeclaredConstructor().newInstance();

            // Service Patient (constructeur PatientService(PatientRepository))
            String patientServiceClassName = properties.getProperty("patientService");
            Class<?> cPatientService = Class.forName(patientServiceClassName);
            PatientService patientService = (PatientService)
                    cPatientService.getDeclaredConstructor(PatientRepository.class).newInstance(patientRepository);

            //  Controller Patient (constructeur PatientController(PatientService))
            String patientControllerClassName = properties.getProperty("patientController");
            Class<?> cPatientController = Class.forName(patientControllerClassName);
            PatientController patientController = (PatientController)
                    cPatientController.getDeclaredConstructor(PatientService.class).newInstance(patientService);

            // Enregistrement dans les maps
            context.put(PatientRepository.class, patientRepository);
            context.put(PatientService.class, patientService);
            context.put(PatientController.class, patientController);

            contextByName.put("patientRepo", patientRepository);
            contextByName.put("patientService", patientService);
            contextByName.put("patientController", patientController);

            // ============================================================
            //  2. MODULE CAISSE / DASHBOARD
            // ============================================================

            // FactureRepository
            String factureRepoClassName = properties.getProperty("factureRepo");
            Class<?> cFactureRepo = Class.forName(factureRepoClassName);
            FactureRepository factureRepository = (FactureRepository)
                    cFactureRepo.getDeclaredConstructor().newInstance();

            // ChargesRepository
            String chargesRepoClassName = properties.getProperty("chargesRepo");
            Class<?> cChargesRepo = Class.forName(chargesRepoClassName);
            ChargesRepository chargesRepository = (ChargesRepository)
                    cChargesRepo.getDeclaredConstructor().newInstance();

            // RevenuesRepository
            String revenusRepoClassName = properties.getProperty("revenusRepo");
            Class<?> cRevenusRepo = Class.forName(revenusRepoClassName);
            RevenuesRepository revenuesRepository = (RevenuesRepository)
                    cRevenusRepo.getDeclaredConstructor().newInstance();

            // SituationFinanciereRepository
            String sitFinRepoClassName = properties.getProperty("sitFinRepo");
            Class<?> cSitFinRepo = Class.forName(sitFinRepoClassName);
            SituationFinanciereRepository situationFinanciereRepository = (SituationFinanciereRepository)
                    cSitFinRepo.getDeclaredConstructor().newInstance();

            // Constructeur CaisseDashboardServiceImpl(FactureRepository, RevenuesRepository, ChargesRepository)
            String caisseServiceClassName = properties.getProperty("caisseDashboardService");
            Class<?> cCaisseService = Class.forName(caisseServiceClassName);
            CaisseDashboardService caisseDashboardService = (CaisseDashboardService)
                    cCaisseService
                            .getDeclaredConstructor(FactureRepository.class, RevenuesRepository.class, ChargesRepository.class)
                            .newInstance(factureRepository, revenuesRepository, chargesRepository);

            // Constructeur CaisseDashboardControllerImpl(CaisseDashboardService)
            String caisseControllerClassName = properties.getProperty("caisseDashboardController");
            Class<?> cCaisseController = Class.forName(caisseControllerClassName);
            CaisseDashboardController caisseDashboardController = (CaisseDashboardController)
                    cCaisseController
                            .getDeclaredConstructor(CaisseDashboardService.class)
                            .newInstance(caisseDashboardService);

            // Enregistrement dans les maps
            context.put(FactureRepository.class, factureRepository);
            context.put(ChargesRepository.class, chargesRepository);
            context.put(RevenuesRepository.class, revenuesRepository);
            context.put(SituationFinanciereRepository.class, situationFinanciereRepository);
            context.put(CaisseDashboardService.class, caisseDashboardService);
            context.put(CaisseDashboardController.class, caisseDashboardController);

            contextByName.put("factureRepo", factureRepository);
            contextByName.put("chargesRepo", chargesRepository);
            contextByName.put("revenusRepo", revenuesRepository);
            contextByName.put("sitFinRepo", situationFinanciereRepository);
            contextByName.put("caisseDashboardService", caisseDashboardService);
            contextByName.put("caisseDashboardController", caisseDashboardController);

            // ============================================================
            //  3. MODULE RDV / AGENDA  (AICHA)
            // ============================================================

            // Repository RDV
            String rdvRepoClassName = properties.getProperty("rdv.repository");
            Class<?> cRdvRepo = Class.forName(rdvRepoClassName);
            RdvRepository rdvRepository = (RdvRepository)
                    cRdvRepo.getDeclaredConstructor().newInstance();

            // Service RDV : constructeur RdvServiceImpl(RdvRepository)
            String rdvServiceClassName = properties.getProperty("rdv.service");
            Class<?> cRdvService = Class.forName(rdvServiceClassName);
            RdvService rdvService = (RdvService)
                    cRdvService.getDeclaredConstructor(RdvRepository.class).newInstance(rdvRepository);

            // Controller RDV : constructeur RdvControllerImpl(RdvService)
            String rdvControllerClassName = properties.getProperty("rdv.controller");
            Class<?> cRdvController = Class.forName(rdvControllerClassName);
            RdvController rdvController = (RdvController)
                    cRdvController.getDeclaredConstructor(RdvService.class).newInstance(rdvService);

            // Enregistrement dans les maps
            context.put(RdvRepository.class, rdvRepository);
            context.put(RdvService.class, rdvService);
            context.put(RdvController.class, rdvController);

            contextByName.put("rdv.repository", rdvRepository);
            contextByName.put("rdv.service", rdvService);
            contextByName.put("rdv.controller", rdvController);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de config/beans.properties", e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation de l'ApplicationContext", e);
        }
    }

    private ApplicationContext() {
    }

    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> beanClass) {
        return (T) context.get(beanClass);
    }
}
