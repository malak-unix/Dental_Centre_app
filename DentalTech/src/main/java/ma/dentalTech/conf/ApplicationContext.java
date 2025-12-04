package ma.dentalTech.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

// Nouveaux Imports pour le Module Caisse/Facturation
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.FactureService;
import ma.dentalTech.service.modules.caisse.api.FinancialReportingService;


public class ApplicationContext {

    private static final Map<Class<?>, Object> context       = new HashMap<>();
    private static final Map<String, Object>   contextByName = new HashMap<>(); // Ajout d'une deuxième map

    static {
        var configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/beans.properties");

        if (configFile != null) {
            Properties properties = new Properties();
            try {
                properties.load(configFile);

                // === 1. CHARGEMENT MODULE PATIENT (EXISTANT) ===
                String daoClassName = properties.getProperty("patientRepo");
                String servClassName = properties.getProperty("patientService");
                String ctrlClassName = properties.getProperty("patientController");

                Class<?> cRepository = Class.forName(daoClassName);
                PatientRepository repository = (PatientRepository) cRepository.getDeclaredConstructor().newInstance();

                Class<?> cService = Class.forName(servClassName);
                PatientService service = (PatientService) cService.getDeclaredConstructor(PatientRepository.class).newInstance(repository);

                Class<?> cController = Class.forName(ctrlClassName);
                PatientController controller = (PatientController) cController.getDeclaredConstructor(PatientService.class).newInstance(service);

                // Stockage des beans Patient
                context.put(PatientRepository.class, repository);
                context.put(PatientService.class, service);
                context.put(PatientController.class, controller);
                contextByName.put("patientDao", repository);
                contextByName.put("patientService", service);
                contextByName.put("patientController", controller);

                // === 2. CHARGEMENT MODULE CAISSE / FACTURATION (NOUVEAU) ===

                // 2.1 Repositories (Instanciation sans argument)
                String fRepoClassName = properties.getProperty("factureRepo");
                FactureRepository factureRepository = (FactureRepository) Class.forName(fRepoClassName).getDeclaredConstructor().newInstance();

                String cRepoClassName = properties.getProperty("chargesRepo");
                ChargesRepository chargesRepository = (ChargesRepository) Class.forName(cRepoClassName).getDeclaredConstructor().newInstance();

                String rRepoClassName = properties.getProperty("revenusRepo");
                RevenuesRepository revenuesRepository = (RevenuesRepository) Class.forName(rRepoClassName).getDeclaredConstructor().newInstance();

                String sfRepoClassName = properties.getProperty("sitFinRepo");
                SituationFinanciereRepository sitFinRepository = (SituationFinanciereRepository) Class.forName(sfRepoClassName).getDeclaredConstructor().newInstance();

                // 2.2 Services (Injection des Repositories)

                // FactureService dépend de FactureRepository
                String fServClassName = properties.getProperty("factureService");
                Class<?> cfService = Class.forName(fServClassName);
                FactureService factureService = (FactureService)
                        cfService.getDeclaredConstructor(FactureRepository.class).newInstance(factureRepository);

                // FinancialReportingService dépend de FactureRepository, ChargesRepository, RevenuesRepository
                String repServClassName = properties.getProperty("financialService");
                Class<?> crService = Class.forName(repServClassName);
                FinancialReportingService financialReportingService = (FinancialReportingService)
                        crService.getDeclaredConstructor(FactureRepository.class, ChargesRepository.class, RevenuesRepository.class)
                                .newInstance(factureRepository, chargesRepository, revenuesRepository);

                // Stockage des beans Caisse
                context.put(FactureRepository.class, factureRepository);
                context.put(ChargesRepository.class, chargesRepository);
                context.put(RevenuesRepository.class, revenuesRepository);
                context.put(SituationFinanciereRepository.class, sitFinRepository);
                context.put(FactureService.class, factureService);
                context.put(FinancialReportingService.class, financialReportingService);

                contextByName.put("factureRepo", factureRepository);
                contextByName.put("chargesRepo", chargesRepository);
                contextByName.put("revenusRepo", revenuesRepository);
                contextByName.put("sitFinRepo", sitFinRepository);
                contextByName.put("factureService", factureService);
                contextByName.put("financialService", financialReportingService);


            } catch (Exception e) {
                // IMPORTANT: Une exception ici signifie généralement qu'un bean n'a pas été trouvé ou
                // que la signature du constructeur ne correspond pas aux arguments injectés.
                e.printStackTrace();
            }
        } else {
            System.err.println("Erreur : Le fichier beans.properties est introuvable !");
        }
    }

    /**
     * Retourne un composant bean en fonction de son nom (String).
     */
    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    /**
     * Retourne un composant bean en fonction de sa classe.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(context.get(beanClass));
    }


}