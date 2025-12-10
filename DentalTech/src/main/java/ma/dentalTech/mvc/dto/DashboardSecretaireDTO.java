package ma.dentalTech.mvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DashboardSecretaireDTO {

    private LocalDate dateJour;

    // Partie caisse (réutilisation de ton DTO déjà existant)
    private CaisseDashboardDTO caisseDuJour;

    // Rendez-vous / file d'attente
    private Integer nombreRdvDuJour;
    private Integer nombrePatientsEnFileAttente;
    private Integer nombreRdvEnRetard;

    // Alertes / notifications
    private Integer nombreNotificationsNonLues;
    private Integer nombreAlertesImportantes;
}
