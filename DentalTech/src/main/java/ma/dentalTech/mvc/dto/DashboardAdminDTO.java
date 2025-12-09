package ma.dentalTech.mvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DashboardAdminDTO {

    private LocalDate dateJour;

    // Utilisateurs
    private Integer nombreUtilisateursTotal;
    private Integer nombreMedecins;
    private Integer nombreSecretaires;
    private Integer nombreAdmins;

    // Patients / dossiers
    private Integer nombrePatientsTotal;
    private Integer nombreDossiersActifs;

    // Financier global (par exemple sur la journée ou le mois en cours)
    private Double chiffreAffairesJour;
    private Double chiffreAffairesMois;
    private Double totalChargesMois;

    // Sécurité / monitoring
    private Integer nombreConnexionsJour;
    private Integer nombreNotificationsSysteme;
}
