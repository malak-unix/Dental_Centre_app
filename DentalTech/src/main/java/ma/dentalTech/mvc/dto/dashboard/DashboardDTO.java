package ma.dentalTech.mvc.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    /**
     * ADMIN / MEDECIN / SECRETAIRE
     */
    private String role;

    /**
     * Pour que l’UI sache quels widgets afficher.
     */
    private DashboardFeaturesDTO features;

    /**
     * Une seule des 3 parties sera généralement remplie,
     * selon le rôle connecté.
     */
    private SecretaireDashboardResponseDTO secretaire;
    private MedecinDashboardResponseDTO medecin;
    private AdminDashboardResponseDTO admin;
}
