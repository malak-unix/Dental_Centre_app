package ma.dentalTech.configuration;

import ma.dentalTech.mvc.controllers.modules.caisse.api.ChargesControllerV2;
import ma.dentalTech.mvc.controllers.modules.caisse.api.FactureControllerV2;
import ma.dentalTech.repository.common.RowMappers;

import ma.dentalTech.repository.modules.patient.api.*;
import ma.dentalTech.repository.modules.users.api.*;
import ma.dentalTech.service.modules.patient.api.*;

import ma.dentalTech.repository.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.api.*;
import ma.dentalTech.service.modules.caisse.impl.*;

import ma.dentalTech.repository.modules.agenda.api.*;
import ma.dentalTech.service.modules.agenda.api.*;

import ma.dentalTech.service.modules.dashboard.api.DashboardService;
//ajouté par jihane
import ma.dentalTech.common.utilitaire.RepoFactory;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
//
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import ma.dentalTech.service.modules.users.api.NotificationService;
import ma.dentalTech.mvc.controllers.modules.users.api.NotificationController;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        String currentBean = "aucun";

        // ✅ Known = “mini container” utilisé par instantiateWithKnown()
        Map<Class<?>, Object> known = new HashMap<>();

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
            // Known base objects
            // =========================================================
            SessionFactory sf = SessionFactory.getInstance();
            known.put(SessionFactory.class, sf);

            // ✅ IMPORTANT : garder une connexion partagée pour les repos qui ont ctor(Connection)
            Connection sharedCn = sf.getConnection();
            known.put(Connection.class, sharedCn);

            // RowMappers (si utilisé)
            try {
                known.put(RowMappers.class, RowMappers.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}

            // =========================================================
            // PATIENT : repo -> service -> appService -> controller
            // =========================================================
            currentBean = "patientRepo";
            PatientRepository patientRepo = newRepoInstance(props, "patientRepo", PatientRepository.class, known);
            put(PatientRepository.class, patientRepo, "patientRepo");
            registerKnown(known, patientRepo);

            currentBean = "patientService";
            PatientService patientService = newServiceInstance(
                    props, "patientService", PatientService.class,
                    new Class<?>[]{PatientRepository.class},
                    new Object[]{patientRepo}
            );
            put(PatientService.class, patientService, "patientService");
            registerKnown(known, patientService);

            currentBean = "patientAppService";
            PatientAppService patientAppService =
                    new ma.dentalTech.service.modules.patient.impl.PatientAppServiceImpl(patientRepo);
            put(PatientAppService.class, patientAppService, "patientAppService");
            registerKnown(known, patientAppService);

            // Antecedent optional
            if (hasKey(props, "antecedentRepo")) {
                currentBean = "antecedentRepo";
                AntecedentRepository antecedentRepo = newRepoInstance(props, "antecedentRepo", AntecedentRepository.class, known);
                put(AntecedentRepository.class, antecedentRepo, "antecedentRepo");
                registerKnown(known, antecedentRepo);

                if (hasKey(props, "antecedentService")) {
                    currentBean = "antecedentService";
                    AntecedentService antecedentService = newServiceInstance(
                            props, "antecedentService", AntecedentService.class,
                            new Class<?>[]{AntecedentRepository.class},
                            new Object[]{antecedentRepo}
                    );
                    put(AntecedentService.class, antecedentService, "antecedentService");
                    registerKnown(known, antecedentService);
                }

                // ✅ AJOUT IMPORTANT : antecedentController (bean name EXACT)
                if (hasKey(props, "antecedentController") && hasBean("antecedentService")) {
                    currentBean = "antecedentController";

                    AntecedentService antecedentServiceBean = getBean(AntecedentService.class);

                    Object antecedentController = newFlexibleInstance(
                            props.getProperty("antecedentController"),
                            known,
                            antecedentServiceBean
                    );

                    contextByName.put("antecedentController", antecedentController);
                    registerKnown(known, antecedentController);
                }
            }



            if (hasKey(props, "patientController")) {
                currentBean = "patientController";
                Object patientController = newFlexibleInstance(
                        props.getProperty("patientController"),
                        known,
                        patientAppService
                );
                contextByName.put("patientController", patientController);
                registerKnown(known, patientController);
            }

            // =========================================================
            // CAISSE V2
            // =========================================================

            currentBean = "factureRepo";
            FactureRepository factureRepo = newRepoInstance(props, "factureRepo", FactureRepository.class, known);
            put(FactureRepository.class, factureRepo, "factureRepo");
            registerKnown(known, factureRepo);

            currentBean = "chargesRepo";
            ChargesRepository chargesRepo = newRepoInstance(props, "chargesRepo", ChargesRepository.class, known);
            put(ChargesRepository.class, chargesRepo, "chargesRepo");
            registerKnown(known, chargesRepo);

            currentBean = "revenusRepo";
            RevenuesRepository revenusRepo = newRepoInstance(props, "revenusRepo", RevenuesRepository.class, known);
            put(RevenuesRepository.class, revenusRepo, "revenusRepo");
            registerKnown(known, revenusRepo);

            currentBean = "sitFinRepo";
            SituationFinanciereRepository sitFinRepo = newRepoInstance(props, "sitFinRepo", SituationFinanciereRepository.class, known);
            put(SituationFinanciereRepository.class, sitFinRepo, "sitFinRepo");
            registerKnown(known, sitFinRepo);

            // CaisseValidationService (indispensable)
            CaisseValidationService validationSvc = new CaisseValidationServiceImpl();
            put(CaisseValidationService.class, validationSvc, "caisseValidationService");
            registerKnown(known, validationSvc);


            FacturePdfService facturePdfService = null;
            if (hasKey(props, "facturePdfService")) {
                currentBean = "facturePdfService";
                facturePdfService = newServiceInstance(props, "facturePdfService", FacturePdfService.class);
                put(FacturePdfService.class, facturePdfService, "facturePdfService");
                registerKnown(known, facturePdfService);
            }

            if (hasKey(props, "factureServiceV2") && facturePdfService != null) {
                currentBean = "factureServiceV2";
                FactureServiceV2 factureServiceV2 = new FactureServiceV2Impl(factureRepo, facturePdfService);
                put(FactureServiceV2.class, factureServiceV2, "factureServiceV2");
                registerKnown(known, factureServiceV2);
            }

            if (hasKey(props, "chargesServiceV2")) {
                currentBean = "chargesServiceV2";
                ChargesServiceV2 chargesServiceV2 = newServiceInstance(
                        props, "chargesServiceV2", ChargesServiceV2.class,
                        new Class<?>[]{ChargesRepository.class},
                        new Object[]{chargesRepo}
                );
                put(ChargesServiceV2.class, chargesServiceV2, "chargesServiceV2");
                registerKnown(known, chargesServiceV2);
            }

            if (hasKey(props, "revenusServiceV2")) {
                currentBean = "revenusServiceV2";
                RevenusServiceV2 revenusServiceV2 = newServiceInstance(
                        props, "revenusServiceV2", RevenusServiceV2.class,
                        new Class<?>[]{RevenuesRepository.class},
                        new Object[]{revenusRepo}
                );
                put(RevenusServiceV2.class, revenusServiceV2, "revenusServiceV2");
                registerKnown(known, revenusServiceV2);
            }

            if (hasKey(props, "sitFinServiceV2")) {
                currentBean = "sitFinServiceV2";
                SituationFinanciereServiceV2 sitFinServiceV2 = newServiceInstance(
                        props, "sitFinServiceV2", SituationFinanciereServiceV2.class,
                        new Class<?>[]{SituationFinanciereRepository.class},
                        new Object[]{sitFinRepo}
                );
                put(SituationFinanciereServiceV2.class, sitFinServiceV2, "sitFinServiceV2");
                registerKnown(known, sitFinServiceV2);
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
                registerKnown(known, caisseDashboardServiceV2);
            }

            if (hasKey(props, "caisseDashboardControllerV2") && caisseDashboardServiceV2 != null) {
                currentBean = "caisseDashboardControllerV2";
                Object ctrl = newFlexibleInstance(
                        props.getProperty("caisseDashboardControllerV2"),
                        known,
                        caisseDashboardServiceV2
                );
                contextByName.put("caisseDashboardControllerV2", ctrl);
                registerKnown(known, ctrl);
            }

            // ✅ FactureControllerV2
            if (hasKey(props, "factureControllerV2") && hasBean("factureServiceV2")) {
                FactureServiceV2 fs = getBean(FactureServiceV2.class);
                Object fc = newFlexibleInstance(props.getProperty("factureControllerV2"), known, fs);
                put(FactureControllerV2.class, fc, "factureControllerV2");
                registerKnown(known, fc);
            }

            // ChargesControllerV2
            if (hasKey(props, "chargesControllerV2") && hasBean("chargesServiceV2")) {
                ChargesServiceV2 cs = getBean(ChargesServiceV2.class);
                Object cc = newFlexibleInstance(props.getProperty("chargesControllerV2"), known, cs);
                put(ChargesControllerV2.class, cc, "chargesControllerV2");
                registerKnown(known, cc);
            }




            // =========================================================
            // RDV : repo -> service -> controller
            // =========================================================
            currentBean = "rdv.repository";
            RdvRepository rdvRepo = newRepoInstance(props, "rdv.repository", RdvRepository.class, known);
            put(RdvRepository.class, rdvRepo, "rdv.repository");
            registerKnown(known, rdvRepo);

            RdvService rdvService = null;
            if (hasKey(props, "rdv.service")) {
                currentBean = "rdv.service";
                rdvService = newServiceInstance(
                        props, "rdv.service", RdvService.class,
                        new Class<?>[]{RdvRepository.class},
                        new Object[]{rdvRepo}
                );
                put(RdvService.class, rdvService, "rdv.service");
                registerKnown(known, rdvService);
            }

            if (hasKey(props, "rdv.controller") && rdvService != null) {
                currentBean = "rdv.controller";
                Object rdvCtrl = newFlexibleInstance(
                        props.getProperty("rdv.controller"),
                        known,
                        rdvService, patientRepo
                );
                contextByName.put("rdv.controller", rdvCtrl);
                registerKnown(known, rdvCtrl);
            }

            // =========================================================
            // AGENDA
            // =========================================================
            currentBean = "agendaMensuelRepo";
            AgendaMensuelRepository agendaMensuelRepo = newRepoInstance(props, "agendaMensuelRepo", AgendaMensuelRepository.class, known);
            put(AgendaMensuelRepository.class, agendaMensuelRepo, "agendaMensuelRepo");
            registerKnown(known, agendaMensuelRepo);

            currentBean = "detailJourneeRepo";
            DetailJourneeRepository detailJourneeRepo = newRepoInstance(props, "detailJourneeRepo", DetailJourneeRepository.class, known);
            put(DetailJourneeRepository.class, detailJourneeRepo, "detailJourneeRepo");
            registerKnown(known, detailJourneeRepo);

            if (hasKey(props, "agendaService")) {
                currentBean = "agendaService";
                AgendaService agendaService = newServiceInstance(
                        props, "agendaService", AgendaService.class,
                        new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class},
                        new Object[]{agendaMensuelRepo, detailJourneeRepo}
                );
                put(AgendaService.class, agendaService, "agendaService");
                registerKnown(known, agendaService);
            }

            if (hasKey(props, "agenda.controller")) {
                currentBean = "agenda.controller";
                Object agendaCtrl = newFlexibleInstance(
                        props.getProperty("agenda.controller"),
                        known,
                        agendaMensuelRepo, detailJourneeRepo
                );
                contextByName.put("agenda.controller", agendaCtrl);
                registerKnown(known, agendaCtrl);
            }

            if (hasKey(props, "agendaAppService")) {
                currentBean = "agendaAppService";
                AgendaAppService agendaAppService = newServiceInstance(
                        props, "agendaAppService", AgendaAppService.class,
                        new Class<?>[]{AgendaMensuelRepository.class, DetailJourneeRepository.class, RdvRepository.class},
                        new Object[]{agendaMensuelRepo, detailJourneeRepo, rdvRepo}
                );
                put(AgendaAppService.class, agendaAppService, "agendaAppService");
                registerKnown(known, agendaAppService);
            }

            // =========================================================
            // LISTE D'ATTENTE
            // =========================================================
            currentBean = "listeAttente.repository";
            ListeAttenteRepository listeRepo = newRepoInstance(props, "listeAttente.repository", ListeAttenteRepository.class, known);
            put(ListeAttenteRepository.class, listeRepo, "listeAttente.repository");
            registerKnown(known, listeRepo);

            ListeAttenteService listeService = null;
            if (hasKey(props, "listeAttente.service")) {
                currentBean = "listeAttente.service";

                // ✅ utilise instantiateWithKnown => il trouvera (ListeAttenteRepository, RdvService)
                Object svc = newFlexibleInstance(
                        props.getProperty("listeAttente.service"),
                        known,
                        listeRepo,
                        getBean(RdvService.class) // rdvService déjà créé plus haut
                );

                listeService = (ListeAttenteService) svc;

                put(ListeAttenteService.class, listeService, "listeAttente.service");
                registerKnown(known, listeService);
            }


            if (hasKey(props, "listeAttente.controller") && listeService != null) {
                currentBean = "listeAttente.controller";
                Object listeCtrl = newFlexibleInstance(
                        props.getProperty("listeAttente.controller"),
                        known,
                        listeService
                );
                contextByName.put("listeAttente.controller", listeCtrl);
                registerKnown(known, listeCtrl);
            }

            // =========================================================
            // PLAGE HORAIRE
            // =========================================================
            currentBean = "plageHoraire.repository";
            PlageHoraireRepository plageRepo = newRepoInstance(props, "plageHoraire.repository", PlageHoraireRepository.class, known);
            put(PlageHoraireRepository.class, plageRepo, "plageHoraire.repository");
            registerKnown(known, plageRepo);

            if (hasKey(props, "plageHoraire.service")) {
                currentBean = "plageHoraire.service";
                PlageHoraireService plageService = newServiceInstance(
                        props, "plageHoraire.service", PlageHoraireService.class,
                        new Class<?>[]{PlageHoraireRepository.class},
                        new Object[]{plageRepo}
                );
                put(PlageHoraireService.class, plageService, "plageHoraire.service");
                registerKnown(known, plageService);
            }

            // =========================================================
            // USERS : repos
            // =========================================================
            NotificationRepository notificationRepo = null;
            UtilisateurRepository utilisateurRepo = null;

            // ajouté par jihane (3lignes)
            RoleRepository roleRepo = null;
            MedecinRepository medecinRepo = null;
            SecretaireRepository secretaireRepo = null;
            // ajouté par jihane (safe)
            if (hasKey(props, "roleRepo")) {
                currentBean = "roleRepo";
                roleRepo = newRepoInstance(props, "roleRepo", RoleRepository.class, known);
                put(RoleRepository.class, roleRepo, "roleRepo");
                registerKnown(known, roleRepo);
            }

            if (hasKey(props, "medecinRepo")) {
                currentBean = "medecinRepo";
                medecinRepo = newRepoInstance(props, "medecinRepo", MedecinRepository.class, known);
                put(MedecinRepository.class, medecinRepo, "medecinRepo");
                registerKnown(known, medecinRepo);
            }

            if (hasKey(props, "secretaireRepo")) {
                currentBean = "secretaireRepo";
                secretaireRepo = newRepoInstance(props, "secretaireRepo", SecretaireRepository.class, known);
                put(SecretaireRepository.class, secretaireRepo, "secretaireRepo");
                registerKnown(known, secretaireRepo);
            }
            //

            if (hasKey(props, "notificationRepo")) {
                currentBean = "notificationRepo";
                notificationRepo = newRepoInstance(props, "notificationRepo", NotificationRepository.class, known);
                put(NotificationRepository.class, notificationRepo, "notificationRepo");
                registerKnown(known, notificationRepo);
            }

            if (hasKey(props, "utilisateurRepo")) {
                currentBean = "utilisateurRepo";
                utilisateurRepo = newRepoInstance(props, "utilisateurRepo", UtilisateurRepository.class, known);
                put(UtilisateurRepository.class, utilisateurRepo, "utilisateurRepo");
                registerKnown(known, utilisateurRepo);
            }
            //jihane
            // =========================================================
            // USERS : service (Management)
            // =========================================================
            if (hasKey(props, "userManagementService")) {
                currentBean = "userManagementService";

                // Factories (Connection -> RepoImpl)
                RepoFactory<UtilisateurRepository> userFactory =
                        ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl::new;

                RepoFactory<MedecinRepository> medecinFactory =
                        ma.dentalTech.repository.modules.users.impl.MedecinRepositoryImpl::new;

                RepoFactory<SecretaireRepository> secretaireFactory =
                        ma.dentalTech.repository.modules.users.impl.SecretaireRepositoryImpl::new;

                RepoFactory<RoleRepository> roleFactory =
                        ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl::new;

                PasswordEncoder encoder;
                Object encObj = contextByName.get("authEncoder");
                if (encObj instanceof PasswordEncoder) {
                    encoder = (PasswordEncoder) encObj;
                } else {
                    encoder = new ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl();
                }

                ma.dentalTech.service.modules.users.api.UserManagementService userManagementService =
                        new ma.dentalTech.service.modules.users.impl.UserManagementServiceImpl(
                                userFactory, medecinFactory, secretaireFactory, roleFactory, encoder
                        );

                contextByName.put("userManagementService", userManagementService);
                registerKnown(known, userManagementService);
            }
            if (hasKey(props, "userManagementController")
                    && contextByName.containsKey("userManagementService")) {

                currentBean = "userManagementController";

                Object svc = contextByName.get("userManagementService");
                if (!(svc instanceof ma.dentalTech.service.modules.users.api.UserManagementService ums)) {
                    throw new IllegalStateException("userManagementService introuvable pour créer userManagementController");
                }

                Object ctrl = newFlexibleInstance(
                        props.getProperty("userManagementController"),
                        known,
                        ums
                );

                contextByName.put("userManagementController", ctrl);
                registerKnown(known, ctrl);
            }

            //ajouté par aya
            // =========================================================
            // NOTIFICATION : service -> controller
            // =========================================================
            NotificationService notificationService = null;

            if (hasKey(props, "notificationService") && notificationRepo != null) {
                currentBean = "notificationService";
                notificationService = newServiceInstance(
                        props, "notificationService", NotificationService.class,
                        new Class<?>[]{NotificationRepository.class},
                        new Object[]{notificationRepo}
                );
                put(NotificationService.class, notificationService, "notificationService");
                registerKnown(known, notificationService);
            }

            if (hasKey(props, "notificationController") && notificationService != null) {
                currentBean = "notificationController";
                NotificationController notificationController = newServiceInstance(
                        props, "notificationController", NotificationController.class,
                        new Class<?>[]{NotificationService.class},
                        new Object[]{notificationService}
                );
                put(NotificationController.class, notificationController, "notificationController");
                registerKnown(known, notificationController);
            }



            //ajouté par jihane
            // =========================================================
            // AUTH : validator -> encoder -> service -> controller
            // =========================================================
            if (hasKey(props, "authValidator") && hasKey(props, "authEncoder")
                    && hasKey(props, "authService") && hasKey(props, "authController")) {

                currentBean = "authValidator";
                LoginFormValidator authValidator = newServiceInstance(props, "authValidator", LoginFormValidator.class);
                put(LoginFormValidator.class, authValidator, "authValidator");
                registerKnown(known, authValidator);

                currentBean = "authEncoder";
                PasswordEncoder authEncoder = newServiceInstance(props, "authEncoder", PasswordEncoder.class);
                put(PasswordEncoder.class, authEncoder, "authEncoder");
                registerKnown(known, authEncoder);

                // Factories repos (Connection -> RepoImpl)
                RepoFactory<UtilisateurRepository> userFactory =
                        ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl::new;

                RepoFactory<RoleRepository> roleFactory =
                        ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl::new;

                // AuthService (constructeur compat, donc LoginFrame reste intact)
                currentBean = "authService";
                AuthService authService = new ma.dentalTech.service.modules.auth.impl.AuthServiceImpl(
                        userFactory, roleFactory, authValidator, authEncoder
                );
                put(AuthService.class, authService, "authService");
                registerKnown(known, authService);

                // Controller (interface AuthController ne contient que login())
                currentBean = "authController";
                Object authController = newFlexibleInstance(
                        props.getProperty("authController"),
                        known,
                        authService
                );
                contextByName.put("authController", authController);
                registerKnown(known, authController);
            }
            // =========================================================
// DOSSIER MEDICAL : services + controllers (pour UI MainFrame)
// =========================================================
            try {

                // --- Services (constructeurs no-arg existent)
                var acteService =
                        new ma.dentalTech.service.modules.dossierMedical.impl.ActeServiceImpl();

                var consultationService =
                        new ma.dentalTech.service.modules.dossierMedical.impl.ConsultationServiceImpl();

                // --- Controllers
                var dossierCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.DossierMedicalControllerImpl();
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.DossierMedicalController.class,
                        dossierCtrl, "dossierMedicalController");
                registerKnown(known, dossierCtrl);

                var acteCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.ActeControllerImpl(acteService);
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController.class,
                        acteCtrl, "acteController");
                registerKnown(known, acteCtrl);

                var consultationCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.ConsultationControllerImpl(consultationService);
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController.class,
                        consultationCtrl, "consultationController");
                registerKnown(known, consultationCtrl);

                var ordonnanceCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.OrdonnanceControllerImpl();
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.OrdonnanceController.class,
                        ordonnanceCtrl, "ordonnanceController");
                registerKnown(known, ordonnanceCtrl);

                var certificatCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.CertificatControllerImpl();
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.CertificatController.class,
                        certificatCtrl, "certificatController");
                registerKnown(known, certificatCtrl);

                var situationCtrl =
                        new ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl.SituationFinanciereControllerImpl();
                put(ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.SituationFinanciereController.class,
                        situationCtrl, "situationFinanciereController");
                registerKnown(known, situationCtrl);

            } catch (Exception e) {
                // Option: log si tu veux
                System.err.println("⚠️ Erreur init DOSSIER MEDICAL: " + e.getMessage());
            }


            // =========================================================
            // DASHBOARD : service -> controller
            // =========================================================
            if (hasKey(props, "dashboardService") && notificationRepo != null) {
                currentBean = "dashboardService";
                DashboardService dashboardService = createDashboardServiceFlexible(
                        props, notificationRepo, utilisateurRepo, rdvRepo, listeRepo, patientRepo, caisseDashboardServiceV2
                );
                put(DashboardService.class, dashboardService, "dashboardService");
                registerKnown(known, dashboardService);

                if (hasKey(props, "dashboardController")) {
                    currentBean = "dashboardController";
                    Object dashCtrl = newFlexibleInstance(
                            props.getProperty("dashboardController"),
                            known,
                            dashboardService
                    );
                    contextByName.put("dashboardController", dashCtrl);
                    registerKnown(known, dashCtrl);
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

    /** ✅ Enregistre instance dans known avec classe + interfaces + superclasses */
    private static void registerKnown(Map<Class<?>, Object> known, Object instance) {
        if (instance == null) return;

        Class<?> c = instance.getClass();
        known.putIfAbsent(c, instance);

        for (Class<?> itf : c.getInterfaces()) {
            known.putIfAbsent(itf, instance);
        }

        Class<?> sup = c.getSuperclass();
        while (sup != null && sup != Object.class) {
            known.putIfAbsent(sup, instance);
            sup = sup.getSuperclass();
        }
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

    private static <T> T newRepoInstance(Properties props, String key, Class<T> expectedType, Map<Class<?>, Object> known) throws Exception {
        String className = props.getProperty(key);
        if (className == null || className.isBlank()) throw new IllegalStateException("Bean '" + key + "' introuvable");

        Class<?> clazz = Class.forName(className);

        // 1) ctor(Connection)
        try {
            Constructor<?> c = clazz.getDeclaredConstructor(Connection.class);
            c.setAccessible(true);
            Object obj = c.newInstance(known.get(Connection.class));
            return expectedType.cast(obj);
        } catch (NoSuchMethodException ignored) {}

        // 2) ctor vide
        try {
            Object obj = clazz.getDeclaredConstructor().newInstance();
            return expectedType.cast(obj);
        } catch (NoSuchMethodException ignored) {}

        // 3) flex
        Object obj = instantiateWithKnown(clazz, known);
        return expectedType.cast(obj);
    }

    private static Object newFlexibleInstance(String className, Map<Class<?>, Object> known, Object... args) throws Exception {
        Class<?> clazz = Class.forName(className);

        Map<Class<?>, Object> merged = new HashMap<>(known);
        for (Object a : args) registerKnown(merged, a);

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

        //  On privilégie les constructeurs qui permettent de détecter le rôle
        try {
            return (DashboardService) clazz.getDeclaredConstructor(
                    NotificationRepository.class,
                    UtilisateurRepository.class
            ).newInstance(notificationRepo, utilisateurRepo);
        } catch (NoSuchMethodException ignored) {}

        // Constructeur complet si tu veux dashboard riche (si présent)
        try {
            return (DashboardService) clazz.getDeclaredConstructor(
                    NotificationRepository.class,
                    UtilisateurRepository.class,
                    PatientRepository.class,
                    RdvRepository.class,
                    ListeAttenteRepository.class,
                    CaisseDashboardServiceV2.class
            ).newInstance(notificationRepo, utilisateurRepo, patientRepo, rdvRepo, listeRepo, caisseDashboardServiceV2);
        } catch (NoSuchMethodException ignored) {}

        //  Ancien constructeur fallback (sans rôle)
        try {
            return (DashboardService) clazz.getDeclaredConstructor(
                    NotificationRepository.class
            ).newInstance(notificationRepo);
        } catch (NoSuchMethodException ignored) {}

        // Constructeur utilisé auparavant dans ton code (si présent)
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
