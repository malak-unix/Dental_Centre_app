package ma.dentalTech.entities.interventionMedecin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterventionMedecin extends BaseEntity {

    private Double prixDePatient;
    private Integer numDent;
}
