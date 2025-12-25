package ma.dentalTech.configuration;

import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.DetailJourneeRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaService;

import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;

import ma.dentalTech.repository.modules.agenda.api.PlageHoraireRepository;
import ma.dentalTech.service.modules.agenda.api.PlageHoraireService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.dashboard.api.DashboardService;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        String currentBean = "aucun";
        try {
            Properties props = new Properties();
            try (InputStream in = ApplicationContext.class.getResourceAsStream("/config/beans.properties")) {
                if (in == null) {
                    throw new IllegalStateException("Impossible de trouver /config/beans.properties dans le classpath");
                }
                props.load(in);
            }

            // ==========================================
            // PATIENT : repo -> service -> (controller optional)
            // ==========================================
            currentBean = "patientRepo";
            PatientRepository patientRepo = newInstance(props, "patientRepo", PatientRepository.class);

            currentBean = "patientService";
            PatientService patientService = newInstance(props, "patientService", PatientService.class,
                    new Class<?>[]{PatientRepository.class},
                    new Object[]{patientRepo});

            put(PatientRepository.class, patientRepo, "patientRepo");
            put(PatientService.class, patientService, "patientService");

            createOptional(props, "patientController", PatientService.class, patientService);
            createOptional(props, "patientControllerSwing", PatientService.class, patientService);

            // ==========================================
            // CAISSE : repos -> service -> (controller optional)
            // ==========================================
            currentBean = "factureRepo";
            FactureRepository factureRepo = newInstance(props, "factureRepo", FactureRepository.class);
            put(FactureRepository.class, factureRepo, "factureRepo");

            currentBean = "chargesRepo";
            ChargesRepository chargesRepo = newInstance(props, "chargesRepo", ChargesRepository.class);
            put(ChargesRepository.class, chargesRepo, "chargesRepo");

            currentBean = "revenusRepo";
            RevenuesRepository revenusRepo = newInstance(props, "revenusRepo", RevenuesRepository.class);
            put(RevenuesRepository.class, revenusRepo, "revenusRepo");

            currentBean = "sitFinRepo";
            SituationFinanciereRepository sitFinRepo = newInstance(props, "sitFinRepo", SituationFinanciereRepository.class);
            put(SituationFinanciereRepository.class, sitFinRepo, "sitFinRepo");

            currentBean = "caisseDashboardService";
            CaisseDashboardService caisseService = newInstance(props, "caisseDashboardService", CaisseDashboardService.class,
                    new Class<?>[]{FactureRepository.class, RevenuesRepository.class, ChargesRepository.class},
                    new Object[]{factureRepo, revenusRepo, chargesRepo});
            put(CaisseDashboardService.class, caisseService, "caisseDashboardService");
            createOptional(props, "caisseDashboardController", CaisseDashboardService.class, caisseService);

            // ==========================================
            // RDV : repo -> service -> (controller optional)
            // ==========================================
            currentBean = "rdv.repository";
            RdvRepository rdvRepo = newInstance(props, "rdv.repository", RdvRepository.class);
            put(RdvRepository.class, rdvRepo, "rdv.repository");

            currentBean = "rdv.service";
            RdvService rdvService = newInstance(props, "rdv.service", RdvService.class,
                    new Class<?>[]{RdvRepository.class},
                    new Object[]{rdvRepo});
            put(RdvService.class, rdvService, "rdv.service");

            createOptional(props, "rdv.controller", RdvService.class, rdvService);

            // ==========================================
            // AGENDA : repos -> service
            // ==========================================
            currentBean = "agendaMensuelRepo";
            AgendaMensuelRepository agendaMensuelRepo = newInstance(props, "agendaMensuelRepo", AgendaMensuelRepository.class);
            put(AgendaMensuelRepository.class, agendaMensuelRepo, "agendaMensuelRepo");

            currentBean = "detailJourneeRepo";
            DetailJourneeRepository detailJourneeRepo = newInstance(props, "detailJourneeRepo", DetailJourneeRepository.class);
            put(DetailJourneeRepository.class, detailJourneeRepo, "detailJourneeRepo");

            currentBean = "agendaService";
            AgendaService agendaService = newInstance(props, "agendaService", AgendaService.class,
                    new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class},
                    new Object[]{agendaMensuelRepo, detailJourneeRepo});
            put(AgendaService.class, agendaService, "agendaService");

            // ==========================================
            // AGENDA CONTROLLER : repos -> controller
            // ==========================================
            if (props.getProperty("agenda.controller") != null && !props.getProperty("agenda.controller").isBlank()) {
                Object obj = Class.forName(props.getProperty("agenda.controller"))
                        .getDeclaredConstructor(AgendaMensuelRepository.class, DetailJourneeRepository.class)
                        .newInstance(agendaMensuelRepo, detailJourneeRepo);
                contextByName.put("agenda.controller", obj);
            }

            // ==========================================
            // LISTE D'ATTENTE : repo -> service
            // ==========================================
            currentBean = "listeAttente.repository";
            ListeAttenteRepository listeRepo = newInstance(props, "listeAttente.repository", ListeAttenteRepository.class);
            put(ListeAttenteRepository.class, listeRepo, "listeAttente.repository");

            currentBean = "listeAttente.service";
            ListeAttenteService listeService = newInstance(props, "listeAttente.service", ListeAttenteService.class,
                    new Class<?>[]{ListeAttenteRepository.class},
                    new Object[]{listeRepo});
            put(ListeAttenteService.class, listeService, "listeAttente.service");

            createOptional(props, "listeAttente.controller", ListeAttenteService.class, listeService);

            // ==========================================
            // PLAGE HORAIRE : repo -> service
            // ==========================================
            currentBean = "plageHoraire.repository";
            PlageHoraireRepository plageRepo = newInstance(props, "plageHoraire.repository", PlageHoraireRepository.class);
            put(PlageHoraireRepository.class, plageRepo, "plageHoraire.repository");

            currentBean = "plageHoraire.service";
            PlageHoraireService plageService = newInstance(props, "plageHoraire.service", PlageHoraireService.class,
                    new Class<?>[]{PlageHoraireRepository.class},
                    new Object[]{plageRepo});
            put(PlageHoraireService.class, plageService, "plageHoraire.service");

            // ==========================================
            // DASHBOARD : repos (optionnels si pas encore alignés)
            // ==========================================
            currentBean = "notificationRepo";
            NotificationRepository notificationRepo =
                    newInstance(props, "notificationRepo", NotificationRepository.class);
            put(NotificationRepository.class, notificationRepo, "notificationRepo");

            currentBean = "utilisateurRepo";
            UtilisateurRepository utilisateurRepo =
                    newInstance(props, "utilisateurRepo", UtilisateurRepository.class);
            put(UtilisateurRepository.class, utilisateurRepo, "utilisateurRepo");

            // ---- Repos dossierMedical : OPTIONNELS (pour éviter ClassCastException)
            Object consultationRepoObj = createOptionalRepo(props, "consultationRepo",
                    ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository.class,
                    "consultationRepo");

            Object acteRepoObj = createOptionalRepo(props, "acteRepo",
                    ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class,
                    "acteRepo");

            Object dossierMedicalRepoObj = createOptionalRepo(props, "dossierMedicalRepo",
                    ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository.class,
                    "dossierMedicalRepo");

            // ---- DashboardService : seulement si tout est présent
            boolean canCreateDashboard =
                    consultationRepoObj != null &&
                            acteRepoObj != null &&
                            dossierMedicalRepoObj != null &&
                            caisseService != null &&
                            rdvRepo != null &&
                            listeRepo != null &&
                            notificationRepo != null &&
                            utilisateurRepo != null &&
                            patientRepo != null &&
                            factureRepo != null &&
                            chargesRepo != null;

            if (canCreateDashboard) {
                currentBean = "dashboardService";
                DashboardService dashboardService =
                        newInstance(props, "dashboardService", DashboardService.class,
                                new Class<?>[]{
                                        CaisseDashboardService.class,
                                        RdvRepository.class,
                                        ListeAttenteRepository.class,
                                        NotificationRepository.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class,
                                        UtilisateurRepository.class,
                                        PatientRepository.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository.class,
                                        FactureRepository.class,
                                        ChargesRepository.class
                                },
                                new Object[]{
                                        caisseService,
                                        rdvRepo,
                                        listeRepo,
                                        notificationRepo,
                                        (ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository) consultationRepoObj,
                                        (ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository) acteRepoObj,
                                        utilisateurRepo,
                                        patientRepo,
                                        (ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository) dossierMedicalRepoObj,
                                        factureRepo,
                                        chargesRepo
                                });

                put(DashboardService.class, dashboardService, "dashboardService");
                createOptional(props, "dashboardController", DashboardService.class, dashboardService);
            } else {
                // pas bloquant : dashboard sera activé quand les repos seront alignés
                // System.out.println("INFO: DashboardService non créé (dépendances manquantes ou non alignées).");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation ApplicationContext (bean courant = " + currentBean + ")", e);
        }
    }

    private ApplicationContext() {}

    // ==========================
    // Public API
    // ==========================
    public static Object getBean(String name) {
        return contextByName.get(name);
    }

    public static boolean hasBean(String name) {
        return contextByName.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) context.get(clazz);
    }

    // ==========================
    // Internal helpers
    // ==========================
    private static void put(Class<?> type, Object instance, String name) {
        context.put(type, instance);
        contextByName.put(name, instance);
    }

    private static <T> T newInstance(Properties props, String key, Class<T> expectedType) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) {
            throw new IllegalStateException("Bean '" + key + "' introuvable dans beans.properties");
        }
        Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
        return expectedType.cast(obj);
    }

    private static <T> T newInstance(Properties props, String key, Class<T> expectedType,
                                     Class<?>[] ctorTypes, Object[] ctorArgs) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) {
            throw new IllegalStateException("Bean '" + key + "' introuvable dans beans.properties");
        }
        Object obj = Class.forName(className).getDeclaredConstructor(ctorTypes).newInstance(ctorArgs);
        return expectedType.cast(obj);
    }

    private static void createOptional(Properties props, String key, Class<?> depType, Object depInstance) {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) return;

        try {
            Object obj = Class.forName(className).getDeclaredConstructor(depType).newInstance(depInstance);
            contextByName.put(key, obj);
        } catch (Exception e) {
            throw new RuntimeException("Erreur création bean optionnel '" + key + "'", e);
        }
    }

    /**
     * Repo/service optionnel : si la classe est absente OU ne cast pas => on ignore (pas bloquant).
     */
    private static <T> Object createOptionalRepo(Properties props, String key, Class<T> expectedType, String name) {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) return null;

        try {
            Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
            T casted = expectedType.cast(obj); // peut lancer ClassCastException
            put(expectedType, casted, name);
            return casted;
        } catch (Exception e) {
            // on ignore pour ne pas bloquer l'app (utile pendant l'intégration)
            return null;
        }
    }
}
