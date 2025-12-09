/**package ma.dentalTech.service.modules.dashboard.baseImplementation;
 BIDMAN YSALIW LBNAT W Y9ADO LES REPO DYALHOM
 7IT HNA I NEED BZF FHAL RDV NOTIFICATION AGENDA ...

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.mvc.dto.DashboardAdminDTO;
import ma.dentalTech.mvc.dto.DashboardMedecinDTO;
import ma.dentalTech.mvc.dto.DashboardSecretaireDTO;
import ma.dentalTech.repository.modules.rdv.api.RdvRepository;
import ma.dentalTech.repository.modules.agenda.api.ListeAttenteRepository;
import ma.dentalTech.repository.modules.notification.api.NotificationRepository;
import ma.dentalTech.repository.modules.consultation.api.ConsultationRepository;
import ma.dentalTech.repository.modules.consultation.api.ActeRepository;
import ma.dentalTech.repository.modules.security.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.dossier.api.DossierMedicalRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;
import ma.dentalTech.service.modules.dashboard.api.DashboardService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    // Services / repositories nécessaires
    private CaisseDashboardService caisseDashboardService;

    private RdvRepository rdvRepository;
    private ListeAttenteRepository listeAttenteRepository;
    private NotificationRepository notificationRepository;

    private ConsultationRepository consultationRepository;
    private ActeRepository acteRepository;

    private UtilisateurRepository utilisateurRepository;
    private PatientRepository patientRepository;
    private DossierMedicalRepository dossierMedicalRepository;

    private FactureRepository factureRepository;
    private ChargesRepository chargesRepository;

    @Override
    public DashboardSecretaireDTO getDashboardSecretaire(Long secretaireId) throws ServiceException {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);

            // Stats caisse du jour
            CaisseDashboardDTO caisseDuJour = caisseDashboardService.getDashboardToday();

            // Rdv / file d'attente
            Integer nbRdvJour = rdvRepository.countByDate(start, end);
            Integer nbEnFileAttente = listeAttenteRepository.countActifs();
            Integer nbRdvEnRetard = rdvRepository.countRdvEnRetard(today);

            // Notifications
            Integer nbNotifNonLues = notificationRepository.countNonLuesPourSecretaire(secretaireId);
            Integer nbAlertesImportantes = notificationRepository.countAlertesImportantesPourSecretaire(secretaireId);

            return DashboardSecretaireDTO.builder()
                    .dateJour(today)
                    .caisseDuJour(caisseDuJour)
                    .nombreRdvDuJour(nbRdvJour)
                    .nombrePatientsEnFileAttente(nbEnFileAttente)
                    .nombreRdvEnRetard(nbRdvEnRetard)
                    .nombreNotificationsNonLues(nbNotifNonLues)
                    .nombreAlertesImportantes(nbAlertesImportantes)
                    .build();

        } catch (DaoException e) {
            throw new ServiceException("Erreur lors du chargement du dashboard Secrétaire.", e);
        }
    }

    @Override
    public DashboardMedecinDTO getDashboardMedecin(Long medecinId) throws ServiceException {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime start = today.atStartOfDay();
            LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);

            Integer nbRdvJour = rdvRepository.countByMedecinAndDate(medecinId, start, end);
            Integer nbConsultTerminees = consultationRepository.countTermineesPourMedecin(medecinId, start, end);
            Integer nbConsultEnCours = consultationRepository.countEnCoursPourMedecin(medecinId, start, end);
            Integer nbEnFileAttente = listeAttenteRepository.countPourMedecin(medecinId);

            Integer nbActesJour = acteRepository.countActesPourMedecinEtDate(medecinId, start, end);
            Double montantActesJour = safeDouble(acteRepository.sumMontantActesPourMedecinEtDate(medecinId, start, end));

            Double totalFacturesJour = safeDouble(factureRepository.calculateTotalFactures(start, end));
            Double totalRegleJour    = safeDouble(factureRepository.calculateTotalRegle(start, end));
            Double totalNonRegleJour = safeDouble(factureRepository.calculateTotalNonRegle(start, end));

            return DashboardMedecinDTO.builder()
                    .dateJour(today)
                    .nombreRdvDuJour(nbRdvJour)
                    .nombreConsultationsTerminees(nbConsultTerminees)
                    .nombreConsultationsEnCours(nbConsultEnCours)
                    .nombrePatientsEnFileAttente(nbEnFileAttente)
                    .nombreActesRealisesDuJour(nbActesJour)
                    .montantTotalActesDuJour(montantActesJour)
                    .totalFacturesDuJour(totalFacturesJour)
                    .totalRegleDuJour(totalRegleJour)
                    .totalNonRegleDuJour(totalNonRegleJour)
                    .build();

        } catch (DaoException e) {
            throw new ServiceException("Erreur lors du chargement du dashboard Médecin.", e);
        }
    }

    @Override
    public DashboardAdminDTO getDashboardAdmin(Long adminId) throws ServiceException {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startJour = today.atStartOfDay();
            LocalDateTime endJour   = today.plusDays(1).atStartOfDay().minusNanos(1);

            // Utilisateurs
            Integer nbUsers    = utilisateurRepository.countAll();
            Integer nbMedecins = utilisateurRepository.countByRole("MEDECIN");
            Integer nbSecs     = utilisateurRepository.countByRole("SECRETAIRE");
            Integer nbAdmins   = utilisateurRepository.countByRole("ADMIN");

            // Patients / dossiers
            Integer nbPatients = patientRepository.countAll();
            Integer nbDossiersActifs = dossierMedicalRepository.countActifs();

            // Financier
            Double caJour  = safeDouble(factureRepository.calculateTotalFactures(startJour, endJour));
            // Ici tu peux adapter pour le mois en cours :
            LocalDate firstDayMonth = today.withDayOfMonth(1);
            LocalDateTime startMois = firstDayMonth.atStartOfDay();
            LocalDateTime endMois   = firstDayMonth.plusMonths(1).atStartOfDay().minusNanos(1);

            Double caMois = safeDouble(factureRepository.calculateTotalFactures(startMois, endMois));
            Double chargesMois = safeDouble(chargesRepository.calculateTotalCharges(startMois, endMois));

            // Sécurité / monitoring (à adapter selon ton modèle)
            Integer nbConnexionsJour = utilisateurRepository.countConnexionsJour(today);
            Integer nbNotifSysteme   = notificationRepository.countNotificationsSystemeNonLues();

            return DashboardAdminDTO.builder()
                    .dateJour(today)
                    .nombreUtilisateursTotal(nbUsers)
                    .nombreMedecins(nbMedecins)
                    .nombreSecretaires(nbSecs)
                    .nombreAdmins(nbAdmins)
                    .nombrePatientsTotal(nbPatients)
                    .nombreDossiersActifs(nbDossiersActifs)
                    .chiffreAffairesJour(caJour)
                    .chiffreAffairesMois(caMois)
                    .totalChargesMois(chargesMois)
                    .nombreConnexionsJour(nbConnexionsJour)
                    .nombreNotificationsSysteme(nbNotifSysteme)
                    .build();

        } catch (DaoException e) {
            throw new ServiceException("Erreur lors du chargement du dashboard Admin.", e);
        }
    }

    private Double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}
**/