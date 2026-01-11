package ma.dentalTech.mvc.dto.dashboard.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferentielStatsDTO {

    private Integer nbActes;
    private Integer nbMedicaments;
    private Integer nbAntecedents;
    private Integer nbAssurances;
}
