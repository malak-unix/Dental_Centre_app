package ma.dentalTech.service.modules.dashboard.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.mvc.dto.DashboardFeaturesDTO;

import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;

import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepository;

import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.agenda.api.RdvRepository;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private CaisseDashboardService caisseDashboardService;

    private RdvRepository rdvRepository;
    private ListeAttenteRepository listeAttenteRepository;
    private NotificationRepository notificationRepository;

    // ✅ CORRIGÉ : dossierMedical.api
    private ConsultationRepository consultationRepository;
    private ActeRepository acteRepository;
    private DossierMedicalRepository dossierMedicalRepository;

    private UtilisateurRepository utilisateurRepository;
    private PatientRepository patientRepository;

    private FactureRepository factureRepository;
    private ChargesRepository chargesRepository;

    @Override
    public DashboardDTO getDashboard(Long utilisateurId) throws ServiceException {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

            String role = utilisateurRepository.findRoleByUtilisateurId(utilisateurId);
            if (role == null) throw new ServiceException("Role introuvable pour utilisateurId=" + utilisateurId);

            DashboardFeaturesDTO features = featuresForRole(role);

            DashboardDTO.DashboardDTOBuilder out = DashboardDTO.builder()
                    .dateJour(today)
                    .role(role)
                    .features(features);

            // Secrétaire
            if (features.isVoirCaisse()) {
                CaisseDashboardDTO caisse = caisseDashboardService.getDashboardToday();
                out.caisseDuJour(caisse);
            }
            if (features.isVoirRdvEtFileAttente()) {
                out.nombreRdvDuJour(rdvRepository.countByDate(start, end));
                out.nombrePatientsEnFileAttente(listeAttenteRepository.countActifs());
                out.nombreRdvEnRetard(rdvRepository.countRdvEnRetard(today));
            }
            if (features.isVoirNotifications()) {
                out.nombreNotificationsNonLues(notificationRepository.countNonLuesPourSecretaire(utilisateurId));
                out.nombreAlertesImportantes(notificationRepository.countAlertesImportantesPourSecretaire(utilisateurId));
            }

            // Médecin
            if (features.isVoirConsultationsEtActes()) {
                Long medecinId = utilisateurId;

                out.nombreConsultationsTerminees(consultationRepository.countTermineesPourMedecin(medecinId, start, end));
                out.nombreConsultationsEnCours(consultationRepository.countEnCoursPourMedecin(medecinId, start, end));

                out.nombreActesRealisesDuJour(acteRepository.countActesPourMedecinEtDate(medecinId, start, end));
                out.montantTotalActesDuJour(safeDouble(acteRepository.sumMontantActesPourMedecinEtDate(medecinId, start, end)));

                out.totalFacturesDuJour(safeDouble(factureRepository.calculateTotalFactures(start, end)));
                out.totalRegleDuJour(safeDouble(factureRepository.calculateTotalRegle(start, end)));
                out.totalNonRegleDuJour(safeDouble(factureRepository.calculateTotalNonRegle(start, end)));
            }

            // Admin
            if (features.isVoirStatsAdmin()) {
                out.nombreUtilisateursTotal(utilisateurRepository.countAll());
                out.nombreMedecins(utilisateurRepository.countByRole("MEDECIN"));
                out.nombreSecretaires(utilisateurRepository.countByRole("SECRETAIRE"));
                out.nombreAdmins(utilisateurRepository.countByRole("ADMIN"));

                out.nombrePatientsTotal(patientRepository.countAll());
                out.nombreDossiersActifs(dossierMedicalRepository.countActifs());

                out.chiffreAffairesJour(safeDouble(factureRepository.calculateTotalFactures(start, end)));

                LocalDate firstDay = today.withDayOfMonth(1);
                LocalDateTime startMonth = firstDay.atStartOfDay();
                LocalDateTime endMonth = firstDay.plusMonths(1).atStartOfDay().minusNanos(1);

                out.chiffreAffairesMois(safeDouble(factureRepository.calculateTotalFactures(startMonth, endMonth)));
                out.totalChargesMois(safeDouble(chargesRepository.calculateTotalCharges(startMonth, endMonth)));

                out.nombreConnexionsJour(utilisateurRepository.countConnexionsJour(today));
                out.nombreNotificationsSysteme(notificationRepository.countNotificationsSystemeNonLues());
            }

            return out.build();

        } catch (Exception e) {
            throw new ServiceException("Erreur getDashboard (DTO unique).", e);
        }
    }

    private DashboardFeaturesDTO featuresForRole(String role) {
        role = role == null ? "" : role.trim().toUpperCase();
        return switch (role) {
            case "SECRETAIRE" -> DashboardFeaturesDTO.builder()
                    .voirCaisse(true).voirRdvEtFileAttente(true).voirNotifications(true)
                    .voirConsultationsEtActes(false).voirStatsAdmin(false)
                    .build();
            case "MEDECIN" -> DashboardFeaturesDTO.builder()
                    .voirCaisse(false).voirRdvEtFileAttente(true).voirNotifications(false)
                    .voirConsultationsEtActes(true).voirStatsAdmin(false)
                    .build();
            case "ADMIN" -> DashboardFeaturesDTO.builder()
                    .voirCaisse(true).voirRdvEtFileAttente(false).voirNotifications(true)
                    .voirConsultationsEtActes(false).voirStatsAdmin(true)
                    .build();
            default -> DashboardFeaturesDTO.builder().build();
        };
    }

    private Double safeDouble(Double v) {
        return v != null ? v : 0.0;
    }
}
