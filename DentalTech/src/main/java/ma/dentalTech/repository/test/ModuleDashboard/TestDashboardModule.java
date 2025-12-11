package ma.dentalTech.repository.test.ModuleDashboard;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.mvc.dto.DashboardAdminDTO;
import ma.dentalTech.mvc.dto.DashboardMedecinDTO;
import ma.dentalTech.mvc.dto.DashboardSecretaireDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.ChargesRepositoryJdbcImpl;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.FactureRepositoryJdbcImpl;
import ma.dentalTech.repository.modules.caisse.jdbc_implementation.RevenuesRepositoryJdbcImpl;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;
import ma.dentalTech.service.modules.caisse.baseImplementation.CaisseDashboardServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDashboardModule {

    public static void main(String[] args) {
        System.out.println("=== TEST MODULE DASHBOARD (avec module CAISSE) - aya ===");


        FactureRepository factureRepo   = new FactureRepositoryJdbcImpl();
        RevenuesRepository revenuesRepo = new RevenuesRepositoryJdbcImpl();
        ChargesRepository chargesRepo   = new ChargesRepositoryJdbcImpl();

        CaisseDashboardService caisseService =
                new CaisseDashboardServiceImpl(factureRepo, revenuesRepo, chargesRepo);

        // On choisit une date de test (par ex. une date de ton seed : 2025-01-15)
        LocalDate dateJour = LocalDate.of(2025, 1, 15);
        LocalDateTime start = dateJour.atStartOfDay();
        LocalDateTime end   = dateJour.plusDays(1).atStartOfDay().minusNanos(1);

        try {
            CaisseDashboardDTO caisseDuJour = caisseService.getDashboardBetween(start, end);

            System.out.println("--- Résumé Caisse du " + dateJour + " ---");
            System.out.println("Total factures : " + safe(caisseDuJour.getTotalFactures()));
            System.out.println("Total réglé    : " + safe(caisseDuJour.getTotalRegle()));
            System.out.println("Total non réglé: " + safe(caisseDuJour.getTotalNonRegle()));
            System.out.println("Autres revenus : " + safe(caisseDuJour.getTotalRevenus()));
            System.out.println("Charges        : " + safe(caisseDuJour.getTotalCharges()));
            System.out.println("Solde net      : " + safe(caisseDuJour.getSoldeNet()));

            DashboardSecretaireDTO dashSec = DashboardSecretaireDTO.builder()
                    .dateJour(dateJour)
                    .caisseDuJour(caisseDuJour)
                    .nombreRdvDuJour(3)                  // valeurs de démo
                    .nombrePatientsEnFileAttente(1)
                    .nombreRdvEnRetard(0)
                    .nombreNotificationsNonLues(2)
                    .nombreAlertesImportantes(1)
                    .build();

            System.out.println("\n=== Dashboard Secrétaire (DEMO) ===");
            System.out.println("Date jour                 : " + dashSec.getDateJour());
            System.out.println("RDV du jour               : " + dashSec.getNombreRdvDuJour());
            System.out.println("Patients en file attente  : " + dashSec.getNombrePatientsEnFileAttente());
            System.out.println("RDV en retard             : " + dashSec.getNombreRdvEnRetard());
            System.out.println("Notif non lues            : " + dashSec.getNombreNotificationsNonLues());
            System.out.println("Alertes importantes       : " + dashSec.getNombreAlertesImportantes());

            // Dashboard Médecin (DEMO)
            DashboardMedecinDTO dashMed = DashboardMedecinDTO.builder()
                    .dateJour(dateJour)
                    .nombreRdvDuJour(3)
                    .nombreConsultationsTerminees(2)
                    .nombreConsultationsEnCours(1)
                    .nombrePatientsEnFileAttente(1)
                    .nombreActesRealisesDuJour(4)
                    .montantTotalActesDuJour(1300.00)
                    .totalFacturesDuJour(caisseDuJour.getTotalFactures())
                    .totalRegleDuJour(caisseDuJour.getTotalRegle())
                    .totalNonRegleDuJour(caisseDuJour.getTotalNonRegle())
                    .build();

            System.out.println("\n=== Dashboard Médecin (DEMO) ===");
            System.out.println("Date jour                 : " + dashMed.getDateJour());
            System.out.println("RDV du jour               : " + dashMed.getNombreRdvDuJour());
            System.out.println("Consultations terminées   : " + dashMed.getNombreConsultationsTerminees());
            System.out.println("Consultations en cours    : " + dashMed.getNombreConsultationsEnCours());
            System.out.println("Patients en file attente  : " + dashMed.getNombrePatientsEnFileAttente());
            System.out.println("Actes réalisés (jour)     : " + dashMed.getNombreActesRealisesDuJour());
            System.out.println("Montant actes (jour)      : " + dashMed.getMontantTotalActesDuJour());
            System.out.println("Total factures (jour)     : " + dashMed.getTotalFacturesDuJour());
            System.out.println("Total réglé (jour)        : " + dashMed.getTotalRegleDuJour());
            System.out.println("Total non réglé (jour)    : " + dashMed.getTotalNonRegleDuJour());

            //  Dashboard Admin (DEMO)
            DashboardAdminDTO dashAdmin = DashboardAdminDTO.builder()
                    .dateJour(dateJour)
                    .nombreUtilisateursTotal(5)
                    .nombreMedecins(1)
                    .nombreSecretaires(3)
                    .nombreAdmins(1)
                    .nombrePatientsTotal(3)
                    .nombreDossiersActifs(3)
                    .chiffreAffairesJour(caisseDuJour.getTotalFactures())
                    .chiffreAffairesMois(caisseDuJour.getTotalFactures())
                    .totalChargesMois(caisseDuJour.getTotalCharges())
                    .nombreConnexionsJour(10)
                    .nombreNotificationsSysteme(2)
                    .build();

            System.out.println("\n=== Dashboard Admin (DEMO) ===");
            System.out.println("Date jour                 : " + dashAdmin.getDateJour());
            System.out.println("Utilisateurs total        : " + dashAdmin.getNombreUtilisateursTotal());
            System.out.println("Médecins                  : " + dashAdmin.getNombreMedecins());
            System.out.println("Secrétaires               : " + dashAdmin.getNombreSecretaires());
            System.out.println("Admins                    : " + dashAdmin.getNombreAdmins());
            System.out.println("Patients total            : " + dashAdmin.getNombrePatientsTotal());
            System.out.println("Dossiers actifs           : " + dashAdmin.getNombreDossiersActifs());
            System.out.println("CA jour                   : " + dashAdmin.getChiffreAffairesJour());
            System.out.println("CA mois                   : " + dashAdmin.getChiffreAffairesMois());
            System.out.println("Charges mois              : " + dashAdmin.getTotalChargesMois());
            System.out.println("Connexions jour           : " + dashAdmin.getNombreConnexionsJour());
            System.out.println("Notifications système     : " + dashAdmin.getNombreNotificationsSysteme());

        } catch (ServiceException e) {
            System.err.println("Erreur pendant le test du module Dashboard : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN TEST MODULE DASHBOARD ===");
    }

    private static double safe(Double d) {
        return d != null ? d : 0.0;
    }
}
