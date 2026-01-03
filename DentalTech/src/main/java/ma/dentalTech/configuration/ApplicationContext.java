package ma.dentalTech.configuration;
import ma.dentalTech.repository.common.RowMappers;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;

import ma.dentalTech.repository.modules.agenda.api.*;
import ma.dentalTech.service.modules.agenda.api.*;

import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.impl.*;

import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.io.InputStream;
import java.util.Properties;

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
            put(PatientRepository.class, patientRepo, "patientRepo");

            currentBean = "patientService";
            PatientService patientService = newInstance(props, "patientService", PatientService.class,
                    new Class<?>[]{PatientRepository.class},
                    new Object[]{patientRepo});
            put(PatientService.class, patientService, "patientService");

            // Antecedent (optionnel selon ton projet)
            if (props.getProperty("antecedentRepo") != null && !props.getProperty("antecedentRepo").isBlank()) {
                currentBean = "antecedentRepo";
                ma.dentalTech.repository.modules.patient.api.AntecedentRepository antecedentRepo =
                        newInstance(props, "antecedentRepo", ma.dentalTech.repository.modules.patient.api.AntecedentRepository.class);
                put(ma.dentalTech.repository.modules.patient.api.AntecedentRepository.class, antecedentRepo, "antecedentRepo");

                currentBean = "antecedentService";
                ma.dentalTech.service.modules.patient.api.AntecedentService antecedentService =
                        newInstance(props, "antecedentService", ma.dentalTech.service.modules.patient.api.AntecedentService.class,
                                new Class<?>[]{ma.dentalTech.repository.modules.patient.api.AntecedentRepository.class},
                                new Object[]{antecedentRepo});
                put(ma.dentalTech.service.modules.patient.api.AntecedentService.class, antecedentService, "antecedentService");
            }

            createOptional(props, "patientController", PatientService.class, patientService);
            createOptional(props, "patientControllerSwing", PatientService.class, patientService);

            // ==========================================
            // CAISSE : repos -> services V2 -> (controller optional)
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

            // PDF service
            if (props.getProperty("facturePdfService") != null && !props.getProperty("facturePdfService").isBlank()) {
                currentBean = "facturePdfService";
                FacturePdfService facturePdfService = newInstance(props, "facturePdfService", FacturePdfService.class);
                put(FacturePdfService.class, facturePdfService, "facturePdfService");

                // Facture service V2 (constructeur connu)
                if (props.getProperty("factureServiceV2") != null && !props.getProperty("factureServiceV2").isBlank()) {
                    currentBean = "factureServiceV2";
                    FactureServiceV2 factureServiceV2 = new FactureServiceV2Impl(factureRepo, facturePdfService);
                    put(FactureServiceV2.class, factureServiceV2, "factureServiceV2");
                }
            }

            // Charges / Revenus / SituationFin V2
            if (props.getProperty("chargesServiceV2") != null && !props.getProperty("chargesServiceV2").isBlank()) {
                currentBean = "chargesServiceV2";
                ChargesServiceV2 chargesServiceV2 = newInstance(props, "chargesServiceV2", ChargesServiceV2.class,
                        new Class<?>[]{ChargesRepository.class},
                        new Object[]{chargesRepo});
                put(ChargesServiceV2.class, chargesServiceV2, "chargesServiceV2");
            }

            if (props.getProperty("revenusServiceV2") != null && !props.getProperty("revenusServiceV2").isBlank()) {
                currentBean = "revenusServiceV2";
                RevenusServiceV2 revenusServiceV2 = newInstance(props, "revenusServiceV2", RevenusServiceV2.class,
                        new Class<?>[]{RevenuesRepository.class},
                        new Object[]{revenusRepo});
                put(RevenusServiceV2.class, revenusServiceV2, "revenusServiceV2");
            }

            if (props.getProperty("sitFinServiceV2") != null && !props.getProperty("sitFinServiceV2").isBlank()) {
                currentBean = "sitFinServiceV2";
                SituationFinanciereServiceV2 sitFinServiceV2 = newInstance(props, "sitFinServiceV2", SituationFinanciereServiceV2.class,
                        new Class<?>[]{SituationFinanciereRepository.class},
                        new Object[]{sitFinRepo});
                put(SituationFinanciereServiceV2.class, sitFinServiceV2, "sitFinServiceV2");
            }

            // Dashboard caisse V2 (ctor 3 repos)
            CaisseDashboardServiceV2 caisseDashboardServiceV2 = null;
            if (props.getProperty("caisseDashboardServiceV2") != null && !props.getProperty("caisseDashboardServiceV2").isBlank()) {
                currentBean = "caisseDashboardServiceV2";
                caisseDashboardServiceV2 = newInstance(
                        props,
                        "caisseDashboardServiceV2",
                        CaisseDashboardServiceV2.class,
                        new Class<?>[]{FactureRepository.class, RevenuesRepository.class, ChargesRepository.class},
                        new Object[]{factureRepo, revenusRepo, chargesRepo}
                );
                put(CaisseDashboardServiceV2.class, caisseDashboardServiceV2, "caisseDashboardServiceV2");

                createOptional(props, "caisseDashboardControllerV2", CaisseDashboardServiceV2.class, caisseDashboardServiceV2);
            }

            // ==========================================
            // RDV : repo -> service -> (controller optional)
            // ==========================================
            currentBean = "rdv.repository";
            RdvRepository rdvRepo = newInstance(props, "rdv.repository", RdvRepository.class);
            put(RdvRepository.class, rdvRepo, "rdv.repository");

            if (props.getProperty("rdv.service") != null && !props.getProperty("rdv.service").isBlank()) {
                currentBean = "rdv.service";
                RdvService rdvService = newInstance(props, "rdv.service", RdvService.class,
                        new Class<?>[]{RdvRepository.class},
                        new Object[]{rdvRepo});
                put(RdvService.class, rdvService, "rdv.service");
                createOptional(props, "rdv.controller", RdvService.class, rdvService);
            }

            // ==========================================
            // AGENDA : repos -> service
            // ==========================================
            currentBean = "agendaMensuelRepo";
            AgendaMensuelRepository agendaMensuelRepo = newInstance(props, "agendaMensuelRepo", AgendaMensuelRepository.class);
            put(AgendaMensuelRepository.class, agendaMensuelRepo, "agendaMensuelRepo");

            currentBean = "detailJourneeRepo";
            DetailJourneeRepository detailJourneeRepo = newInstance(props, "detailJourneeRepo", DetailJourneeRepository.class);
            put(DetailJourneeRepository.class, detailJourneeRepo, "detailJourneeRepo");

            if (props.getProperty("agendaService") != null && !props.getProperty("agendaService").isBlank()) {
                currentBean = "agendaService";
                AgendaService agendaService = newInstance(props, "agendaService", AgendaService.class,
                        new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class},
                        new Object[]{agendaMensuelRepo, detailJourneeRepo});
                put(AgendaService.class, agendaService, "agendaService");
            }

            // ==========================================
            // LISTE D'ATTENTE : repo -> service
            // ==========================================
            currentBean = "listeAttente.repository";
            ListeAttenteRepository listeRepo = newInstance(props, "listeAttente.repository", ListeAttenteRepository.class);
            put(ListeAttenteRepository.class, listeRepo, "listeAttente.repository");

            if (props.getProperty("listeAttente.service") != null && !props.getProperty("listeAttente.service").isBlank()) {
                currentBean = "listeAttente.service";
                ListeAttenteService listeService = newInstance(props, "listeAttente.service", ListeAttenteService.class,
                        new Class<?>[]{ListeAttenteRepository.class},
                        new Object[]{listeRepo});
                put(ListeAttenteService.class, listeService, "listeAttente.service");
                createOptional(props, "listeAttente.controller", ListeAttenteService.class, listeService);
            }

            // ==========================================
            // PLAGE HORAIRE : repo -> service
            // ==========================================
            currentBean = "plageHoraire.repository";
            PlageHoraireRepository plageRepo = newInstance(props, "plageHoraire.repository", PlageHoraireRepository.class);
            put(PlageHoraireRepository.class, plageRepo, "plageHoraire.repository");

            if (props.getProperty("plageHoraire.service") != null && !props.getProperty("plageHoraire.service").isBlank()) {
                currentBean = "plageHoraire.service";
                PlageHoraireService plageService = newInstance(props, "plageHoraire.service", PlageHoraireService.class,
                        new Class<?>[]{PlageHoraireRepository.class},
                        new Object[]{plageRepo});
                put(PlageHoraireService.class, plageService, "plageHoraire.service");
            }

            // ==========================================
            // USERS repos
            // ==========================================
            currentBean = "notificationRepo";
            NotificationRepository notificationRepo = newRepoInstance(props, "notificationRepo", NotificationRepository.class);
            put(NotificationRepository.class, notificationRepo, "notificationRepo");

            currentBean = "utilisateurRepo";
            UtilisateurRepository utilisateurRepo = newRepoInstance(props, "utilisateurRepo", UtilisateurRepository.class);
            put(UtilisateurRepository.class, utilisateurRepo, "utilisateurRepo");

            // ==========================================
            // DASHBOARD global (création FLEXIBLE)
            // ==========================================
            if (props.getProperty("dashboardService") != null && !props.getProperty("dashboardService").isBlank()) {
                currentBean = "dashboardService";

                DashboardService dashboardService = createDashboardServiceFlexible(
                        props,
                        notificationRepo,
                        utilisateurRepo,
                        rdvRepo,
                        listeRepo,
                        patientRepo,
                        caisseDashboardServiceV2,
                        factureRepo,
                        chargesRepo
                );

                put(DashboardService.class, dashboardService, "dashboardService");
                createOptional(props, "dashboardController", DashboardService.class, dashboardService);
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

    private static <T> T newRepoInstance(Properties props, String key, Class<T> expectedType) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) {
            throw new IllegalStateException("Bean '" + key + "' introuvable dans beans.properties");
        }

        Class<?> clazz = Class.forName(className);

        // ✅ 1) Essai direct constructeur (Connection) : cas JDBC standard
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(java.sql.Connection.class);
            c.setAccessible(true);
            Object obj = c.newInstance(SessionFactory.getInstance().getConnection());
            return expectedType.cast(obj);
        } catch (NoSuchMethodException ignored) {
            // pas de ctor(Connection) => continue
        }

        // 2) essai constructeur vide
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return expectedType.cast(obj);
        } catch (NoSuchMethodException ignored) {
        }

        // 3) Contexte d'objets connus qu'on sait fournir
        Map<Class<?>, Object> known = new HashMap<>();
        known.put(SessionFactory.class, SessionFactory.getInstance());

        // ✅ rendre Connection disponible aussi pour la résolution flexible
        known.put(java.sql.Connection.class, SessionFactory.getInstance().getConnection());

        // RowMappers : selon ton code, soit statique, soit instanciable
        try {
            known.put(RowMappers.class, RowMappers.class.getDeclaredConstructor().newInstance());
        } catch (Exception ignored) {
        }

        // ajoute aussi tous les beans déjà construits
        known.putAll(context);

        // 4) Essaye tous les constructeurs (flex)
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            boolean ok = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object arg = resolveArg(known, paramTypes[i]);
                if (arg == null) {
                    ok = false;
                    break;
                }
                args[i] = arg;
            }

            if (ok) {
                ctor.setAccessible(true);
                Object obj = ctor.newInstance(args);
                return expectedType.cast(obj);
            }
        }

        throw new IllegalStateException(
                "Aucun constructeur compatible trouvé pour " + className + " (bean=" + key + "). " +
                        "Constructeurs testés mais dépendances non résolues. " +
                        "Solution: ajouter un constructeur vide OU rendre les dépendances disponibles dans ApplicationContext."
        );
    }


    private static Object resolveArg(Map<Class<?>, Object> known, Class<?> paramType) {
        // 1) match direct
        Object direct = known.get(paramType);
        if (direct != null) return direct;

        // 2) match par assignabilité (ex: param est interface, known contient impl)
        for (Map.Entry<Class<?>, Object> e : known.entrySet()) {
            if (paramType.isAssignableFrom(e.getKey())) {
                return e.getValue();
            }
        }

        // 3) si param a un constructeur vide, on peut l'instancier automatiquement
        try {
            return paramType.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Création flexible du DashboardService :
     * - supporte DashboardServiceImpl(NotificationRepository)
     * - et d'autres signatures si tu changes plus tard
     */
    private static DashboardService createDashboardServiceFlexible(
            Properties props,
            NotificationRepository notificationRepo,
            UtilisateurRepository utilisateurRepo,
            RdvRepository rdvRepo,
            ListeAttenteRepository listeRepo,
            PatientRepository patientRepo,
            CaisseDashboardServiceV2 caisseDashboardServiceV2,
            FactureRepository factureRepo,
            ChargesRepository chargesRepo
    ) throws Exception {

        String className = props.getProperty("dashboardService");
        Class<?> clazz = Class.forName(className);

        // 1) ctor(NotificationRepository)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(NotificationRepository.class);
            return (DashboardService) c.newInstance(notificationRepo);
        } catch (NoSuchMethodException ignored) {}

        // 2) ctor(NotificationRepository, UtilisateurRepository)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(NotificationRepository.class, UtilisateurRepository.class);
            return (DashboardService) c.newInstance(notificationRepo, utilisateurRepo);
        } catch (NoSuchMethodException ignored) {}

        // 3) ctor(NotificationRepository, RdvRepository, ListeAttenteRepository)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(NotificationRepository.class, RdvRepository.class, ListeAttenteRepository.class);
            return (DashboardService) c.newInstance(notificationRepo, rdvRepo, listeRepo);
        } catch (NoSuchMethodException ignored) {}

        // 4) ctor(NotificationRepository, UtilisateurRepository, PatientRepository, RdvRepository, ListeAttenteRepository, CaisseDashboardServiceV2)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(
                    NotificationRepository.class,
                    UtilisateurRepository.class,
                    PatientRepository.class,
                    RdvRepository.class,
                    ListeAttenteRepository.class,
                    CaisseDashboardServiceV2.class
            );
            return (DashboardService) c.newInstance(notificationRepo, utilisateurRepo, patientRepo, rdvRepo, listeRepo, caisseDashboardServiceV2);
        } catch (NoSuchMethodException ignored) {}

        // 5) Si rien ne match, erreur claire
        throw new IllegalStateException("Aucun constructeur compatible trouvé pour " + className
                + ". Ajoute un constructeur simple (NotificationRepository) dans DashboardServiceImpl.");
    }
}
