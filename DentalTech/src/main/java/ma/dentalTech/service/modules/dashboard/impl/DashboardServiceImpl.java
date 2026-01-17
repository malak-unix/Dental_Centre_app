package ma.dentalTech.service.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.common.AlerteDTO;
import ma.dentalTech.mvc.dto.dashboard.common.NotificationDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardServiceImpl implements DashboardService {

    private final RdvRepository rdvRepo;
    private final ListeAttenteRepository listeAttenteRepo;
    private final PatientRepository patientRepo;
    private final FactureRepository factureRepo;
    private final ActeRepository acteRepo; // optionnel (recette du jour par médecin)
    private final ConsultationRepository consultationRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final NotificationRepository notificationRepo;

    /**
     * ✅ Constructeur SANS ARGUMENTS
     * Important : ApplicationContext (createDashboardServiceFlexible) aime ce
     * constructeur.
     * On récupère les beans via réflexion => pas de dépendance compile sur une
     * signature exacte.
     */
    public DashboardServiceImpl() {
        this(
                bean(RdvRepository.class),
                bean(ListeAttenteRepository.class),
                bean(PatientRepository.class),
                bean(FactureRepository.class),
                beanOrNull(ActeRepository.class),
                bean(ConsultationRepository.class),
                bean(UtilisateurRepository.class),
                bean(NotificationRepository.class));
    }

    /**
     * ✅ Constructeur "standard" complet.
     */
    public DashboardServiceImpl(RdvRepository rdvRepo,
            ListeAttenteRepository listeAttenteRepo,
            PatientRepository patientRepo,
            FactureRepository factureRepo,
            ActeRepository acteRepo,
            ConsultationRepository consultationRepo,
            UtilisateurRepository utilisateurRepo,
            NotificationRepository notificationRepo) {
        this.rdvRepo = rdvRepo;
        this.listeAttenteRepo = listeAttenteRepo;
        this.patientRepo = patientRepo;
        this.factureRepo = factureRepo;
        this.acteRepo = acteRepo;
        this.consultationRepo = consultationRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.notificationRepo = notificationRepo;
    }

    /**
     * ✅ Constructeur fallback au cas où ApplicationContext n’a pas encore
     * ActeRepository.
     */
    public DashboardServiceImpl(RdvRepository rdvRepo,
            ListeAttenteRepository listeAttenteRepo,
            PatientRepository patientRepo,
            FactureRepository factureRepo,
            ConsultationRepository consultationRepo,
            UtilisateurRepository utilisateurRepo,
            NotificationRepository notificationRepo) {
        this(rdvRepo, listeAttenteRepo, patientRepo, factureRepo, null, consultationRepo, utilisateurRepo,
                notificationRepo);
    }

    @Override
    public DashboardDTO getDashboard(Long utilisateurId) throws ServiceException {
        if (utilisateurId == null)
            throw new ServiceException("utilisateurId est null");

        LibelleRole role = detectRole(utilisateurId);
        DashboardFeaturesDTO features = buildFeatures(role);

        DashboardDTO.DashboardDTOBuilder b = DashboardDTO.builder()
                .role(role.name())
                .features(features);

        switch (role) {
            case ADMIN -> b.admin(buildAdmin());
            case MEDECIN -> b.medecin(buildMedecin(utilisateurId));
            default -> b.secretaire(buildSecretaire(utilisateurId));
        }

        return b.build();
    }

    // =========================================================
    // ROLE + FEATURES
    // =========================================================

    private LibelleRole detectRole(Long utilisateurId) {
        try {
            List<String> roles = utilisateurRepo.getRoleLibellesOfUser(utilisateurId);
            if (roles == null || roles.isEmpty()) return LibelleRole.SECRETAIRE;

            LibelleRole role = parseRole(roles.get(0));

            // debug (garde-le 1 minute puis supprime)
            System.out.println("roles BD=" + roles + " -> role detecte=" + role);

            return role;
        } catch (Exception e) {
            return LibelleRole.SECRETAIRE;
        }
    }



    private DashboardFeaturesDTO buildFeatures(LibelleRole role) {
        if (role == LibelleRole.ADMIN) {
            return DashboardFeaturesDTO.builder()
                    .voirStatsAdmin(true)
                    .voirNotifications(true)
                    .voirAlertes(true)
                    .voirCaisse(false)
                    .voirClientEnCours(false)
                    .voirRdvEtFileAttente(false)
                    .build();
        }
        if (role == LibelleRole.MEDECIN) {
            return DashboardFeaturesDTO.builder()
                    .voirRdvEtFileAttente(true)
                    .voirClientEnCours(true)
                    .voirCaisse(false)
                    .voirStatsAdmin(false)
                    .voirNotifications(true)
                    .voirAlertes(true)
                    .build();
        }
        // SECRETAIRE
        return DashboardFeaturesDTO.builder()
                .voirRdvEtFileAttente(true)
                .voirClientEnCours(false)
                .voirCaisse(true)
                .voirStatsAdmin(false)
                .voirNotifications(true)
                .voirAlertes(true)
                .build();
    }

    private LibelleRole parseRole(String raw) {
        if (raw == null) return LibelleRole.SECRETAIRE;

        String key = raw.trim().toUpperCase(java.util.Locale.ROOT)
                .replace("É", "E").replace("È", "E").replace("Ê", "E")
                .replace("À", "A")
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "");

        // Exemples BD possibles
        if (key.startsWith("ADMIN")) return LibelleRole.ADMIN;          // ADMIN / ADMINISTRATEUR / ADMINISTRATION / Admin
        if (key.startsWith("MEDEC")) return LibelleRole.MEDECIN;        // MEDECIN / MÉDECIN
        if (key.startsWith("SECRE")) return LibelleRole.SECRETAIRE;     // SECRETAIRE / SECRÉTAIRE

        // Si jamais la BD contient déjà exactement ADMIN/MEDECIN/SECRETAIRE
        try { return LibelleRole.valueOf(key); }
        catch (Exception e) { return LibelleRole.SECRETAIRE; }
    }


    // =========================================================
    // SECRETAIRE (maquette "Revue du jour")
    // =========================================================

    // =========================================================
    // SECRETAIRE (maquette "Revue du jour")
    // =========================================================

    @Override
    public SecretaireDashboardResponseDTO buildSecretaire(Long utilisateurId) {
        LocalDate today = LocalDate.now();

        Integer nbPatients = safeInt((int) patientRepo.countAll());
        Integer nbRdvDuJour = safeInt(rdvRepo.countRdvDuJour());
        Integer nbEnAttente = safeInt(listeAttenteRepo.countActifs());

        BigDecimal recetteDuJour = safeBig(factureRepo.totalRecetteDuJour());

        List<RDV> rdvEntities = safeList(rdvRepo.findByDate(today));
        List<RdvDto> rdvDuJour = rdvEntities.stream()
                .sorted(Comparator.comparing(RDV::getHeure, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toRdvDtoWithPatientNom)
                .toList();

        List<ListeAttente> listeAttenteEntities = safeList(listeAttenteRepo.findAll());
        List<ListeAttenteDto> fileAttente = listeAttenteEntities.stream()
                .filter(l -> isSameDay(l.getDateAjout(), today))
                .sorted(Comparator.comparing(ListeAttente::getDateAjout,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toListeAttenteDto)
                .toList();

        List<AlerteDTO> alertes = buildAlertesRetardFromRdv(rdvEntities);
        int nbAlertesNonLues = (int) alertes.stream().filter(a -> !a.isLue()).count();

        List<Notification> notifs = safeList(notificationRepo.findByUtilisateur(utilisateurId));
        notifs.sort(Comparator.comparing(this::safeNotifDate).reversed());
        List<NotificationDTO> notifDTOs = notifs.stream().map(this::toNotificationDTO).toList();

        int nbNotifNonLues;
        try {
            nbNotifNonLues = safeList(notificationRepo.findUnreadByUtilisateur(utilisateurId)).size();
        } catch (Exception e) {
            nbNotifNonLues = (int) notifs.stream().filter(n -> !safeIsLue(n)).count();
        }

        return SecretaireDashboardResponseDTO.builder()
                .nbPatients(nbPatients)
                .nbRdvDuJour(nbRdvDuJour)
                .nbEnAttente(nbEnAttente)
                .recetteDuJour(recetteDuJour)
                .rdvDuJour(rdvDuJour)
                .fileAttente(fileAttente)
                .nbAlertesNonLues(nbAlertesNonLues)
                .nbNotificationsNonLues(nbNotifNonLues)
                .alertes(alertes)
                .notifications(notifDTOs)
                .build();
    }

    private List<AlerteDTO> buildAlertesRetardFromRdv(List<RDV> rdvEntities) {
        if (rdvEntities == null || rdvEntities.isEmpty())
            return List.of();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<AlerteDTO> res = new ArrayList<>();
        for (RDV r : rdvEntities) {
            if (r == null)
                continue;
            if (r.getDateRdv() == null || !r.getDateRdv().equals(today))
                continue;
            if (r.getStatut() != EtatRendezVous.PLANIFIE)
                continue;
            if (r.getHeure() == null)
                continue;

            if (r.getHeure().isBefore(now.minusMinutes(5))) {
                String patientNom = resolvePatientNom(r.getPatientId());
                res.add(AlerteDTO.builder()
                        .id(r.getId())
                        .type("RETARD_RDV")
                        .titre("RDV en retard")
                        .message("Patient: " + patientNom + " (" + r.getHeure() + ")")
                        .priorite("NORMAL")
                        .lue(false)
                        .date(LocalDateTime.now())
                        .action("OPEN_RDV")
                        .referenceId(r.getId())
                        .build());
            }
        }
        return res;
    }

    // =========================================================
    // MEDECIN (maquette "Dashboard médecin")
    // =========================================================

    // =========================================================
    // MEDECIN (maquette "Dashboard médecin")
    // =========================================================

    @Override
    public MedecinDashboardResponseDTO buildMedecin(Long medecinId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Integer nbRdvDuJour = safeInt(rdvRepo.countByMedecinAndDate(medecinId, start, end));
        Integer nbActesRealises = safeInt(consultationRepo.countTermineesPourMedecin(medecinId, start, end));

        // ✅ si acteRepo existe -> recette DU MEDECIN, sinon recette globale
        BigDecimal recetteDuJour = BigDecimal.ZERO;
        try {
            if (acteRepo != null)
                recetteDuJour = safeBig(acteRepo.totalRecetteDuJourPourMedecin(medecinId));
            else
                recetteDuJour = safeBig(factureRepo.totalRecetteDuJour());
        } catch (Exception e) {
            recetteDuJour = safeBig(factureRepo.totalRecetteDuJour());
        }

        // ✅ liste RDV du médecin du jour
        List<RDV> rdvEntities = safeList(rdvRepo.findByMedecinAndDate(medecinId, today));

        List<RdvDto> rdvDuJour = rdvEntities.stream()
                .sorted(Comparator.comparing(RDV::getHeure, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toRdvDtoWithPatientNom)
                .toList();

        Integer nbPatientsDuJour = (int) rdvEntities.stream()
                .map(RDV::getPatientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .size();

        PatientCurrentDTO current = pickPatientEnCours(rdvEntities);

        return MedecinDashboardResponseDTO.builder()
                .nbPatientsDuJour(nbPatientsDuJour)
                .nbRdvDuJour(nbRdvDuJour)
                .nbActesRealises(nbActesRealises)
                .recetteDuJour(recetteDuJour)
                .rdvDuJour(rdvDuJour)
                .patientEnCours(current)
                .build();
    }

    private PatientCurrentDTO pickPatientEnCours(List<RDV> rdvEntities) {
        if (rdvEntities == null || rdvEntities.isEmpty())
            return null;

        LocalTime now = LocalTime.now();

        RDV best = rdvEntities.stream()
                .filter(Objects::nonNull)
                .filter(r -> r.getStatut() == EtatRendezVous.PLANIFIE)
                .filter(r -> r.getHeure() != null)
                .min(Comparator.comparing(r -> Math.abs(java.time.Duration.between(now, r.getHeure()).toMinutes())))
                .orElse(rdvEntities.get(0));

        if (best == null)
            return null;

        Patient p = safeFindPatient(best.getPatientId());

        return PatientCurrentDTO.builder()
                .patientId(best.getPatientId())
                .nomComplet(buildPatientNom(p))
                .tel(p != null ? p.getTelephone() : null)
                .statutTraitement("EN_TRAITEMENT")
                .build();
    }

    // =========================================================
    // ADMIN (maquette "Statistiques globales")
    // =========================================================

    // =========================================================
    // ADMIN (maquette "Statistiques globales")
    // =========================================================

    @Override
    public AdminDashboardResponseDTO buildAdmin() {
        LocalDate today = LocalDate.now();

        Integer nbUtilisateurs = safeInt((int) utilisateurRepo.countAll());
        Integer nbAdmins = safeInt((int) utilisateurRepo.countByRole(LibelleRole.ADMIN));
        BigDecimal recetteDuJour = safeBig(factureRepo.totalRecetteDuJour());

        List<Consultation> consToday = safeList(consultationRepo.findByDate(today));
        Integer nbActesRealises = (int) consToday.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getStatut() == StatutConsultation.TERMINE)
                .count();

        List<Utilisateur> users = safeList(utilisateurRepo.findPage(50, 0));
        List<UserSummaryDTO> userDTOs = users.stream().map(this::toUserSummaryDTO).toList();

        System.out.println("countAll users=" + utilisateurRepo.countAll());
        System.out.println("count admins=" + utilisateurRepo.countByRole(LibelleRole.ADMIN));
        System.out.println("recetteJour=" + factureRepo.totalRecetteDuJour());
        System.out.println("consToday=" + consultationRepo.findByDate(LocalDate.now()).size());

        return AdminDashboardResponseDTO.builder()
                .nbUtilisateurs(nbUtilisateurs)
                .nbAdmins(nbAdmins)
                .nbActesRealises(nbActesRealises)
                .recetteDuJour(recetteDuJour)
                .utilisateurs(userDTOs)
                .referentiels(null)
                .sauvegarde(null)
                .build();

    }

    // =========================================================
    // MAPPING HELPERS
    // =========================================================

    private RdvDto toRdvDtoWithPatientNom(RDV r) {
        if (r == null)
            return null;

        String patientNom = resolvePatientNom(r.getPatientId());

        return RdvDto.builder()
                .id(r.getId())
                .patientId(r.getPatientId())
                .detailJourneeId(r.getDetailJourneeId())
                .listeAttenteId(r.getListeAttenteId())
                .typeRdv(null)
                .dateRdv(r.getDateRdv())
                .heure(r.getHeure())
                .motif(r.getMotif())
                .statut(r.getStatut())
                .noteMedecin(r.getNoteMedecin())
                .patientNom(patientNom)
                .build();
    }

    private ListeAttenteDto toListeAttenteDto(ListeAttente l) {
        if (l == null) return null;
        String patientNom = resolvePatientNom(l.getPatientId());

        return ListeAttenteDto.builder()
                .id(l.getId())
                .nom(l.getNom())
                .patientId(l.getPatientId())
                .patientNom(patientNom)
                .motif(l.getMotif())
                .dateAjout(l.getDateAjout())
                .priorite(l.getPriorite())
                .build();
    }

    private UserSummaryDTO toUserSummaryDTO(Utilisateur u) {
        if (u == null)
            return null;

        LibelleRole role = LibelleRole.SECRETAIRE;
        try {
            List<String> roles = utilisateurRepo.getRoleLibellesOfUser(u.getId());
            if (roles != null && !roles.isEmpty()) {
                role = parseRole(roles.get(0));
            }
        } catch (Exception ignore) {}


        boolean actif = u.isActif();

        return UserSummaryDTO.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .login(u.getLogin())
                .role(role)
                .actif(actif)
                .statut(actif ? "ACTIF" : "DESACTIVE")
                .derniereActivite(null)
                .build();
    }

    private NotificationDTO toNotificationDTO(Notification n) {
        if (n == null)
            return null;

        String source = "SYSTEM";
        try {
            if (n.getPriorite() != null && n.getPriorite() == PrioriteNotification.HAUTE)
                source = "URGENT";
        } catch (Exception ignore) {
        }

        return NotificationDTO.builder()
                .id(n.getId())
                .source(source)
                .titre(nullSafe(n.getTitre()))
                .message(nullSafe(n.getMessage()))
                .lue(safeIsLue(n))
                .date(safeNotifDate(n))
                .action(null)
                .referenceId(null)
                .build();
    }

    // =========================================================
    // SAFE HELPERS
    // =========================================================

    private String resolvePatientNom(Long patientId) {
        Patient p = safeFindPatient(patientId);
        return buildPatientNom(p);
    }

    private Patient safeFindPatient(Long patientId) {
        try {
            if (patientId == null)
                return null;
            return patientRepo.findById(patientId);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildPatientNom(Patient p) {
        if (p == null)
            return "";
        String nom = p.getNom() == null ? "" : p.getNom();
        String prenom = p.getPrenom() == null ? "" : p.getPrenom();
        return (nom + " " + prenom).trim();
    }

    private boolean safeIsLue(Notification n) {
        try {
            return n.isLue();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameDay(LocalDateTime dt, LocalDate day) {
        if (day == null) return true;
        if (dt == null) return true;
        return day.equals(dt.toLocalDate());
    }

    private LocalDateTime safeNotifDate(Notification n) {
        try {
            if (n.getDateCreation() != null)
                return n.getDateCreation();
        } catch (Exception ignore) {
        }
        return LocalDateTime.now();
    }

    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private BigDecimal safeBig(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String nullSafe(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private <T> List<T> safeList(List<T> v) {
        return v == null ? List.of() : v;
    }

    // =========================================================
    // ApplicationContext bean access (reflection-safe)
    // =========================================================

    private static <T> T bean(Class<T> type) {
        T v = beanOrNull(type);
        if (v == null)
            throw new IllegalStateException("Bean introuvable: " + type.getName());
        return v;
    }

    @SuppressWarnings("unchecked")
    private static <T> T beanOrNull(Class<T> type) {
        try {
            Class<?> ctx = Class.forName("ma.dentalTech.configuration.ApplicationContext");
            // on essaye getBean(Class)
            try {
                Object obj = ctx.getMethod("getBean", Class.class).invoke(null, type);
                return (T) obj;
            } catch (NoSuchMethodException ignore) {
                // on essaye getBean(String) avec le nom simple
                String key = Character.toLowerCase(type.getSimpleName().charAt(0)) + type.getSimpleName().substring(1);
                Object obj = ctx.getMethod("getBean", String.class).invoke(null, key);
                return (T) obj;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
