package ma.dentalTech.mvc.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFeaturesDTO {

    private boolean voirRdvEtFileAttente;
    private boolean voirClientEnCours;
    private boolean voirStatsAdmin;
    private boolean voirCaisse;
    private boolean voirNotifications;
}
