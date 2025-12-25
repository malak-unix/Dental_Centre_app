package ma.dentalTech.mvc.dto.dashboard.medecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCurrentDTO {

    private Long patientId;
    private String nomComplet;
    private String tel;

    // pour afficher petit statut (optionnel)
    private String statutTraitement; // SAIN / EN_TRAITEMENT / URGENT / TERMINE
}
