package ma.dentalTech.mvc.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFeaturesDTO {
    private boolean voirCaisse;
    private boolean voirRdvEtFileAttente;
    private boolean voirNotifications;
    private boolean voirConsultationsEtActes;
    private boolean voirStatsAdmin;
}
