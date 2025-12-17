package ma.dentalTech.mvc.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO UNIQUE pour tout le monde.
 * Les blocs non autorisés restent null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private LocalDate dateJour;
    private String role; // "ADMIN" | "MEDECIN" | "SECRETAIRE"

    private DashboardFeaturesDTO features;

    // ===== Bloc Secrétaire =====
    private CaisseDashboardDTO caisseDuJour;
    private Integer nombreRdvDuJour;
    private Integer nombrePatientsEnFileAttente;
    private Integer nombreRdvEnRetard;
    private Integer nombreNotificationsNonLues;
    private Integer nombreAlertesImportantes;

    // ===== Bloc Médecin =====
    private Integer nombreConsultationsTerminees;
    private Integer nombreConsultationsEnCours;
    private Integer nombreActesRealisesDuJour;
    private Double montantTotalActesDuJour;

    private Double totalFacturesDuJour;
    private Double totalRegleDuJour;
    private Double totalNonRegleDuJour;

    // ===== Bloc Admin =====
    private Integer nombreUtilisateursTotal;
    private Integer nombreMedecins;
    private Integer nombreSecretaires;
    private Integer nombreAdmins;

    private Integer nombrePatientsTotal;
    private Integer nombreDossiersActifs;

    private Double chiffreAffairesJour;
    private Double chiffreAffairesMois;
    private Double totalChargesMois;

    private Integer nombreConnexionsJour;
    private Integer nombreNotificationsSysteme;
}
