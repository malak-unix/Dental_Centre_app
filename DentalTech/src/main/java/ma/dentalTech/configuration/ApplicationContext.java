package ma.dentalTech.configuration;

import ma.dentalTech.repository.common.RowMappers;

import ma.dentalTech.repository.modules.patient.api.*;
import ma.dentalTech.service.modules.patient.api.*;

import ma.dentalTech.repository.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.impl.*;

import ma.dentalTech.repository.modules.agenda.api.*;
import ma.dentalTech.service.modules.agenda.api.*;

import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        String currentBean = "aucun";
        try {
            // =========================================================
            // Load beans.properties
            // =========================================================
            Properties props = new Properties();
            try (InputStream in = ApplicationContext.class.getResourceAsStream("/config/beans.properties")) {
                if (in == null) throw new IllegalStateException("Impossible de trouver /config/beans.properties");
                props.load(in);
            }

            // =========================================================
            // Known objects (pour instantiation flexible)
            // IMPORTANT: ne garde pas une Connection ouverte ici.
            // =========================================================
            Map<Class<?>, Object> known = new HashMap<>();
            known.put(SessionFactory.class, SessionFactory.getInstance());

            // RowMappers (si utilisé en ctor quelque part)
            try {
                known.put(RowMappers.class, RowMappers.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}

            // =========================================================
            // PATIENT : repo -> service -> appService -> controller
            // =========================================================
            currentBean = "patientRepo";
            PatientRepository patientRepo = newRepoInstance(props, "patientRepo", PatientRepository.class, known);
            put(PatientRepository.class, patientRepo, "patientRepo");

            currentBean = "patientService";
            PatientService patientService = newServiceInstance(
                    props, "patientService", PatientService.class,
                    new Class<?>[]{PatientRepository.class},
                    new Object[]{patientRepo}
            );
            put(PatientService.class, patientService, "patientService");

            currentBean = "patientAppService";
            PatientAppService patientAppService =
                    new ma.dentalTech.service.modules.patient.impl.PatientAppServiceImpl(patientRepo);
            put(PatientAppService.class, patientAppService, "patientAppService");

            // Antecedent optional
            AntecedentRepository antecedentRepo = null;
            if (hasKey(props, "antecedentRepo")) {
                currentBean = "antecedentRepo";
                antecedentRepo = newRepoInstance(props, "antecedentRepo", AntecedentRepository.class, known);
                put(AntecedentRepository.class, antecedentRepo, "antecedentRepo");

                if (hasKey(props, "antecedentService")) {
                    currentBean = "antecedentService";
                    AntecedentService antecedentService = newServiceInstance(
                            props, "antecedentService", AntecedentService.class,
                            new Class<?>[]{AntecedentRepository.class},
                            new Object[]{antecedentRepo}
                    );
                    put(AntecedentService.class, antecedentService, "antecedentService");
                }
            }

            // patientController optional (1 dep : PatientAppService)
            if (hasKey(props, "patientController")) {
                currentBean = "patientController";
                Object patientController = newFlexibleInstance(
                        props.getProperty("patientController"), known,
                        patientAppService
                );
                contextByName.put("patientController", patientController);
            }

            // =========================================================
            // CAISSE : repos -> services V2 -> controller V2 optional
            // =========================================================
            currentBean = "factureRepo";
            FactureRepository factureRepo = newRepoInstance(props, "factureRepo", FactureRepository.class, known);
            put(FactureRepository.class, factureRepo, "factureRepo");

            currentBean = "chargesRepo";
            ChargesRepository chargesRepo = newRepoInstance(props, "chargesRepo", ChargesRepository.class, known);
            put(ChargesRepository.class, chargesRepo, "chargesRepo");

            currentBean = "revenusRepo";
            RevenuesRepository revenusRepo = newRepoInstance(props, "revenusRepo", RevenuesRepository.class, known);
            put(RevenuesRepository.class, revenusRepo, "revenusRepo");

            currentBean = "sitFinRepo";
            SituationFinanciereRepository sitFinRepo = newRepoInstance(props, "sitFinRepo", SituationFinanciereRepository.class, known);
            put(SituationFinanciereRepository.class, sitFinRepo, "sitFinRepo");

            FacturePdfService facturePdfService = null;
            if (hasKey(props, "facturePdfService")) {
                currentBean = "facturePdfService";
                facturePdfService = newServiceInstance(props, "facturePdfService", FacturePdfService.class);
                put(FacturePdfService.class, facturePdfService, "facturePdfService");
            }

            if (hasKey(props, "factureServiceV2") && facturePdfService != null) {
                currentBean = "factureServiceV2";
                FactureServiceV2 factureServiceV2 = new FactureServiceV2Impl(factureRepo, facturePdfService);
                put(FactureServiceV2.class, factureServiceV2, "factureServiceV2");
            }

            if (hasKey(props, "chargesServiceV2")) {
                currentBean = "chargesServiceV2";
                ChargesServiceV2 chargesServiceV2 = newServiceInstance(
                        props, "chargesServiceV2", ChargesServiceV2.class,
                        new Class<?>[]{ChargesRepository.class},
                        new Object[]{chargesRepo}
                );
                put(ChargesServiceV2.class, chargesServiceV2, "chargesServiceV2");
            }

            if (hasKey(props, "revenusServiceV2")) {
                currentBean = "revenusServiceV2";
                RevenusServiceV2 revenusServiceV2 = newServiceInstance(
                        props, "revenusServiceV2", RevenusServiceV2.class,
                        new Class<?>[]{RevenuesRepository.class},
                        new Object[]{revenusRepo}
                );
                put(RevenusServiceV2.class, revenusServiceV2, "revenusServiceV2");
            }

            if (hasKey(props, "sitFinServiceV2")) {
                currentBean = "sitFinServiceV2";
                SituationFinanciereServiceV2 sitFinServiceV2 = newServiceInstance(
                        props, "sitFinServiceV2", SituationFinanciereServiceV2.class,
                        new Class<?>[]{SituationFinanciereRepository.class},
                        new Object[]{sitFinRepo}
                );
                put(SituationFinanciereServiceV2.class, sitFinServiceV2, "sitFinServiceV2");
            }

            CaisseDashboardServiceV2 caisseDashboardServiceV2 = null;
            if (hasKey(props, "caisseDashboardServiceV2")) {
                currentBean = "caisseDashboardServiceV2";
                caisseDashboardServiceV2 = newServiceInstance(
                        props, "caisseDashboardServiceV2", CaisseDashboardServiceV2.class,
                        new Class<?>[]{FactureRepository.class, RevenuesRepository.class, ChargesRepository.class},
                        new Object[]{factureRepo, revenusRepo, chargesRepo}
                );
                put(CaisseDashboardServiceV2.class, caisseDashboardServiceV2, "caisseDashboardServiceV2");
            }

            if (hasKey(props, "caisseDashboardControllerV2") && caisseDashboardServiceV2 != null) {
                currentBean = "caisseDashboardControllerV2";
                Object ctrl = newFlexibleInstance(
                        props.getProperty("caisseDashboardControllerV2"), known,
                        caisseDashboardServiceV2
                );
                contextByName.put("caisseDashboardControllerV2", ctrl);
            }

            // =========================================================
            // RDV : repo -> service -> controller  ✅ FIX PRINCIPAL
            // =========================================================
            currentBean = "rdv.repository";
            RdvRepository rdvRepo = newRepoInstance(props, "rdv.repository", RdvRepository.class, known);
            put(RdvRepository.class, rdvRepo, "rdv.repository");

            RdvService rdvService = null;
            if (hasKey(props, "rdv.service")) {
                currentBean = "rdv.service";
                rdvService = newServiceInstance(
                        props, "rdv.service", RdvService.class,
                        new Class<?>[]{RdvRepository.class},
                        new Object[]{rdvRepo}
                );
                put(RdvService.class, rdvService, "rdv.service");
            }

            if (hasKey(props, "rdv.controller") && rdvService != null) {
                currentBean = "rdv.controller";
                // ✅ On injecte RdvService -> RdvControllerImpl(RdvService)
                Object rdvCtrl = newFlexibleInstance(
                        props.getProperty("rdv.controller"), known,
                        rdvService
                );
                contextByName.put("rdv.controller", rdvCtrl);
            }

            // =========================================================
            // AGENDA : repos -> service -> controller -> appService
            // =========================================================
            currentBean = "agendaMensuelRepo";
            AgendaMensuelRepository agendaMensuelRepo = newRepoInstance(props, "agendaMensuelRepo", AgendaMensuelRepository.class, known);
            put(AgendaMensuelRepository.class, agendaMensuelRepo, "agendaMensuelRepo");

            currentBean = "detailJourneeRepo";
            DetailJourneeRepository detailJourneeRepo = newRepoInstance(props, "detailJourneeRepo", DetailJourneeRepository.class, known);
            put(DetailJourneeRepository.class, detailJourneeRepo, "detailJourneeRepo");

            if (hasKey(props, "agendaService")) {
                currentBean = "agendaService";
                AgendaService agendaService = newServiceInstance(
                        props, "agendaService", AgendaService.class,
                        new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class},
                        new Object[]{agendaMensuelRepo, detailJourneeRepo}
                );
                put(AgendaService.class, agendaService, "agendaService");
            }

            if (hasKey(props, "agenda.controller")) {
                currentBean = "agenda.controller";
                // ctor(AgendaMensuelRepository, DetailJourneeRepository)
                Object agendaCtrl = newFlexibleInstance(
                        props.getProperty("agenda.controller"), known,
                        agendaMensuelRepo, detailJourneeRepo
                );
                contextByName.put("agenda.controller", agendaCtrl);
            }

            if (hasKey(props, "agendaAppService")) {
                currentBean = "agendaAppService";
                // ctor(AgendaMensuelRepository, DetailJourneeRepository, RdvRepository)
                AgendaAppService agendaAppService = newServiceInstance(
                        props, "agendaAppService", AgendaAppService.class,
                        new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class, RdvRepository.class},
                        new Object[]{agendaMensuelRepo, detailJourneeRepo, rdvRepo}
                );
                put(AgendaAppService.class, agendaAppService, "agendaAppService");
            }

            // =========================================================
            // LISTE D'ATTENTE : repo -> service -> controller
            // =========================================================
            currentBean = "listeAttente.repository";
            ListeAttenteRepository listeRepo = newRepoInstance(props, "listeAttente.repository", ListeAttenteRepository.class, known);
            put(ListeAttenteRepository.class, listeRepo, "listeAttente.repository");

            ListeAttenteService listeService = null;
            if (hasKey(props, "listeAttente.service")) {
                currentBean = "listeAttente.service";
                listeService = newServiceInstance(
                        props, "listeAttente.service", ListeAttenteService.class,
                        new Class<?>[]{ListeAttenteRepository.class},
                        new Object[]{listeRepo}
                );
                put(ListeAttenteService.class, listeService, "listeAttente.service");
            }

            if (hasKey(props, "listeAttente.controller") && listeService != null) {
                currentBean = "listeAttente.controller";
                Object listeCtrl = newFlexibleInstance(
                        props.getProperty("listeAttente.controller"), known,
                        listeService
                );
                contextByName.put("listeAttente.controller", listeCtrl);
            }

            // =========================================================
            // PLAGE HORAIRE : repo -> service
            // =========================================================
            currentBean = "plageHoraire.repository";
            PlageHoraireRepository plageRepo = newRepoInstance(props, "plageHoraire.repository", PlageHoraireRepository.class, known);
            put(PlageHoraireRepository.class, plageRepo, "plageHoraire.repository");

            if (hasKey(props, "plageHoraire.service")) {
                currentBean = "plageHoraire.service";
                PlageHoraireService plageService = newServiceInstance(
                        props, "plageHoraire.service", PlageHoraireService.class,
                        new Class<?>[]{PlageHoraireRepository.class},
                        new Object[]{plageRepo}
                );
                put(PlageHoraireService.class, plageService, "plageHoraire.service");
            }

            // =========================================================
            // USERS : repos
            // =========================================================
            NotificationRepository notificationRepo = null;
            UtilisateurRepository utilisateurRepo = null;

            if (hasKey(props, "notificationRepo")) {
                currentBean = "notificationRepo";
                notificationRepo = newRepoInstance(props, "notificationRepo", NotificationRepository.class, known);
                put(NotificationRepository.class, notificationRepo, "notificationRepo");
            }

            if (hasKey(props, "utilisateurRepo")) {
                currentBean = "utilisateurRepo";
                utilisateurRepo = newRepoInstance(props, "utilisateurRepo", UtilisateurRepository.class, known);
                put(UtilisateurRepository.class, utilisateurRepo, "utilisateurRepo");
            }

            // =========================================================
            // DASHBOARD : service -> controller (optionnel)
            // =========================================================
            if (hasKey(props, "dashboardService") && notificationRepo != null) {
                currentBean = "dashboardService";
                DashboardService dashboardService = createDashboardServiceFlexible(
                        props, notificationRepo, utilisateurRepo, rdvRepo, listeRepo, patientRepo, caisseDashboardServiceV2
                );
                put(DashboardService.class, dashboardService, "dashboardService");

                if (hasKey(props, "dashboardController")) {
                    currentBean = "dashboardController";
                    Object dashCtrl = newFlexibleInstance(
                            props.getProperty("dashboardController"), known,
                            dashboardService
                    );
                    contextByName.put("dashboardController", dashCtrl);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur init ApplicationContext (bean courant = " + currentBean + ")", e);
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
    // Helpers
    // ==========================
    private static boolean hasKey(Properties props, String key) {
        String v = props.getProperty(key);
        return v != null && !v.isBlank();
    }

    private static void put(Class<?> type, Object instance, String name) {
        context.put(type, instance);
        contextByName.put(name, instance);
    }

    private static <T> T newServiceInstance(Properties props, String key, Class<T> expectedType) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) throw new IllegalStateException("Bean '" + key + "' introuvable");
        Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
        return expectedType.cast(obj);
    }

    private static <T> T newServiceInstance(Properties props, String key, Class<T> expectedType,
                                            Class<?>[] ctorTypes, Object[] ctorArgs) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) throw new IllegalStateException("Bean '" + key + "' introuvable");
        Object obj = Class.forName(className).getDeclaredConstructor(ctorTypes).newInstance(ctorArgs);
        return expectedType.cast(obj);
    }

    /**
     * Repo JDBC : essaye ctor(Connection), sinon ctor vide, sinon resolution flexible.
     * NOTE: On fournit une Connection à la demande (pas stockée).
     */
    private static <T> T newRepoInstance(Properties props, String key, Class<T> expectedType, Map<Class<?>, Object> known) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) throw new IllegalStateException("Bean '" + key + "' introuvable");

        Class<?> clazz = Class.forName(className);

        // 1) ctor(Connection)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(Connection.class);
            c.setAccessible(true);
            try (Connection cn = SessionFactory.getInstance().getConnection()) {
                Object obj = c.newInstance(cn);
                return expectedType.cast(obj);
            }
        } catch (NoSuchMethodException ignored) {}

        // 2) ctor vide
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return expectedType.cast(obj);
        } catch (NoSuchMethodException ignored) {}

        // 3) flex (avec known + Connection temporaire si besoin)
        Map<Class<?>, Object> merged = new HashMap<>(known);
        try (Connection cn = SessionFactory.getInstance().getConnection()) {
            merged.put(Connection.class, cn);
            Object obj = instantiateWithKnown(clazz, merged);
            return expectedType.cast(obj);
        }
    }

    /**
     * Controllers/services “optionnels” : on essaye de matcher les args fournis.
     */
    private static Object newFlexibleInstance(String className, Map<Class<?>, Object> known, Object... args) throws Exception {
        Class<?> clazz = Class.forName(className);

        // ajoute args dans known pour aider (résolution par assignabilité)
        Map<Class<?>, Object> merged = new HashMap<>(known);
        for (Object a : args) {
            if (a != null) merged.put(a.getClass(), a);
        }

        // essayer constructeurs avec le bon nombre d'args (assignable)
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            Class<?>[] types = ctor.getParameterTypes();
            if (types.length != args.length) continue;

            boolean ok = true;
            for (int i = 0; i < types.length; i++) {
                if (args[i] == null || !types[i].isAssignableFrom(args[i].getClass())) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            }
        }

        // sinon instanciation flexible via known
        return instantiateWithKnown(clazz, merged);
    }

    private static Object instantiateWithKnown(Class<?> clazz, Map<Class<?>, Object> known) throws Exception {
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Object[] ctorArgs = new Object[paramTypes.length];

            boolean ok = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object arg = resolveArg(known, paramTypes[i]);
                if (arg == null) { ok = false; break; }
                ctorArgs[i] = arg;
            }

            if (ok) {
                ctor.setAccessible(true);
                return ctor.newInstance(ctorArgs);
            }
        }
        throw new IllegalStateException("Aucun constructeur compatible trouvé pour " + clazz.getName());
    }

    private static Object resolveArg(Map<Class<?>, Object> known, Class<?> paramType) {
        Object direct = known.get(paramType);
        if (direct != null) return direct;

        for (Map.Entry<Class<?>, Object> e : known.entrySet()) {
            if (paramType.isAssignableFrom(e.getKey())) return e.getValue();
        }

        try {
            return paramType.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Dashboard flexible
     */
    private static DashboardService createDashboardServiceFlexible(
            Properties props,
            NotificationRepository notificationRepo,
            UtilisateurRepository utilisateurRepo,
            RdvRepository rdvRepo,
            ListeAttenteRepository listeRepo,
            PatientRepository patientRepo,
            CaisseDashboardServiceV2 caisseDashboardServiceV2
    ) throws Exception {

        String className = props.getProperty("dashboardService");
        Class<?> clazz = Class.forName(className);

        // 1) ctor(NotificationRepository)
        try {
            return (DashboardService) clazz.getDeclaredConstructor(NotificationRepository.class)
                    .newInstance(notificationRepo);
        } catch (NoSuchMethodException ignored) {}

        // 2) ctor(NotificationRepository, UtilisateurRepository)
        if (utilisateurRepo != null) {
            try {
                return (DashboardService) clazz.getDeclaredConstructor(NotificationRepository.class, UtilisateurRepository.class)
                        .newInstance(notificationRepo, utilisateurRepo);
            } catch (NoSuchMethodException ignored) {}
        }

        // 3) ctor(NotificationRepository, PatientRepository, RdvRepository, ListeAttenteRepository, CaisseDashboardServiceV2)
        try {
            return (DashboardService) clazz.getDeclaredConstructor(
                    NotificationRepository.class,
                    PatientRepository.class,
                    RdvRepository.class,
                    ListeAttenteRepository.class,
                    CaisseDashboardServiceV2.class
            ).newInstance(notificationRepo, patientRepo, rdvRepo, listeRepo, caisseDashboardServiceV2);
        } catch (NoSuchMethodException ignored) {}

        throw new IllegalStateException("Aucun constructeur compatible pour dashboardService=" + className);
    }
}
