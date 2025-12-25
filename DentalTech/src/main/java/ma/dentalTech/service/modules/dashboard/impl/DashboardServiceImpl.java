package ma.dentalTech.service.modules.dashboard.impl;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.ReferentielStatsDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {


    private final Object agendaService;
    private final Object usersService;
    private final Object caisseService;
    private final Object authService;

    public DashboardServiceImpl(Object agendaService, Object usersService, Object caisseService, Object authService) {
        this.agendaService = agendaService;
        this.usersService = usersService;
        this.caisseService = caisseService;
        this.authService = authService;
    }

    @Override
    public DashboardDTO getDashboard(Long utilisateurId) throws ServiceException {
        try {
            // TODO: récupérer rôle réel depuis Auth/User (UserPrincipalDTO)
            // Exemple: String role = authService.getPrincipal(utilisateurId).getRole();
            String role = "SECRETAIRE";

            DashboardFeaturesDTO features = featuresFor(role);

            return switch (role) {
                case "SECRETAIRE" -> DashboardDTO.builder()
                        .role(role)
                        .features(features)
                        .secretaire(buildSecretaireDashboard(utilisateurId))
                        .build();

                case "MEDECIN" -> DashboardDTO.builder()
                        .role(role)
                        .features(features)
                        .medecin(buildMedecinDashboard(utilisateurId))
                        .build();

                case "ADMIN" -> DashboardDTO.builder()
                        .role(role)
                        .features(features)
                        .admin(buildAdminDashboard(utilisateurId))
                        .build();

                default -> DashboardDTO.builder()
                        .role(role)
                        .features(features)
                        .build();
            };

        } catch (Exception e) {
            throw new ServiceException("Erreur DashboardService.getDashboard", e);
        }
    }

    // =========================
    // Builders par rôle
    // =========================

    private SecretaireDashboardResponseDTO buildSecretaireDashboard(Long utilisateurId) {
        // TODO: Remplacer par les vrais appels agenda/caisse
        // - rdvDuJour = agendaService.getRdvDuJour(...)
        // - fileAttente = agendaService.getFileAttente(...)
        // - recetteDuJour = caisseService.getRecetteDuJour(...)
        return SecretaireDashboardResponseDTO.builder()
                .nbPatients(null) // optionnel
                .nbRdvDuJour(12)
                .nbEnAttente(6)
                .recetteDuJour(new BigDecimal("1200"))
                .rdvDuJour(List.of())        // TODO
                .fileAttente(List.of())      // TODO
                .build();
    }

    private MedecinDashboardResponseDTO buildMedecinDashboard(Long utilisateurId) {
        // TODO: rdvDuJour = agendaService.getRdvDuJourMedecin(medecinId, date)
        // TODO: patientEnCours = agendaService.getPatientEnCours(...)
        return MedecinDashboardResponseDTO.builder()
                .nbPatientsDuJour(10)
                .nbRdvDuJour(15)
                .nbActesRealises(7)
                .recetteDuJour(new BigDecimal("1200"))
                .rdvDuJour(List.of()) // TODO
                .patientEnCours(PatientCurrentDTO.builder()
                        .patientId(1L)
                        .nomComplet("Driss Gafar")
                        .tel("06 12 34 56 78")
                        .statutTraitement("EN_TRAITEMENT")
                        .build())
                .build();
    }

    private AdminDashboardResponseDTO buildAdminDashboard(Long utilisateurId) {
        // TODO: utilisateurs = usersService.listUsers(...)
        List<UserSummaryDTO> fakeUsers = List.of(
                UserSummaryDTO.builder()
                        .id(1L).nom("Idrissi").prenom("Adam").login("admin@dental.ma")
                        .role(LibelleRole.valueOf("ADMIN")).statut("ACTIF")
                        .derniereActivite(LocalDateTime.now().minusHours(2))
                        .build()
        );

        return AdminDashboardResponseDTO.builder()
                .nbUtilisateurs(24)
                .nbAdmins(4)
                .nbActesRealises(15)
                .recetteDuJour(new BigDecimal("1200"))
                .utilisateurs(fakeUsers)
                .referentiels(ReferentielStatsDTO.builder()
                        .nbActes(120)
                        .nbMedicaments(80)
                        .nbAntecedents(45)
                        .nbAssurances(12)
                        .build())
                .sauvegarde(null) // optionnel
                .build();
    }

    // =========================
    // Features
    // =========================

    private DashboardFeaturesDTO featuresFor(String role) {
        return switch (role) {
            case "SECRETAIRE" -> DashboardFeaturesDTO.builder()
                    .voirRdvEtFileAttente(true)
                    .voirClientEnCours(false)
                    .voirStatsAdmin(false)
                    .voirCaisse(true)
                    .voirNotifications(true)
                    .build();
            case "MEDECIN" -> DashboardFeaturesDTO.builder()
                    .voirRdvEtFileAttente(true)
                    .voirClientEnCours(true)
                    .voirStatsAdmin(false)
                    .voirCaisse(true)
                    .voirNotifications(true)
                    .build();
            case "ADMIN" -> DashboardFeaturesDTO.builder()
                    .voirRdvEtFileAttente(false)
                    .voirClientEnCours(false)
                    .voirStatsAdmin(true)
                    .voirCaisse(true)
                    .voirNotifications(true)
                    .build();
            default -> DashboardFeaturesDTO.builder().build();
        };
    }
}
