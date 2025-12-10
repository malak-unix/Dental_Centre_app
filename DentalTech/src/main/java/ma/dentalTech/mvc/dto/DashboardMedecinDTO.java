package ma.dentalTech.mvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DashboardMedecinDTO {

    private LocalDate dateJour;

    // Activité du jour
    private Integer nombreRdvDuJour;
    private Integer nombreConsultationsTerminees;
    private Integer nombreConsultationsEnCours;
    private Integer nombrePatientsEnFileAttente;

    // Actes médicaux
    private Integer nombreActesRealisesDuJour;
    private Double montantTotalActesDuJour;

    // Aspect financier (vue rapide)
    private Double totalFacturesDuJour;
    private Double totalRegleDuJour;
    private Double totalNonRegleDuJour;
}
