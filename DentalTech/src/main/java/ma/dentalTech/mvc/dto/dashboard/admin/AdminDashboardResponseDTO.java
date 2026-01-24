package ma.dentalTech.mvc.dto.dashboard.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.mvc.dto.dashboard.common.ActivityDTO;
import ma.dentalTech.mvc.dto.users.UserSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDTO {

    // KPIs globaux
    private Integer nbUtilisateurs;
    private Integer nbAdmins;
    private Integer nbActesRealises;
    private BigDecimal recetteDuJour;

    // table users
    private List<UserSummaryDTO> utilisateurs;

    // activites recentes
    private List<ActivityDTO> activities;

    // referentiels + securite
    private ReferentielStatsDTO referentiels;
    private BackupStatusDTO sauvegarde; // optionnel si non utilisé

    public BigDecimal getRecetteJour() {
        return recetteDuJour;
    }

    public Integer getNbActes() {
        return nbActesRealises;
    }
}
