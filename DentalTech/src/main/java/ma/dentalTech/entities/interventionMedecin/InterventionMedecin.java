package ma.dentalTech.entities.interventionMedecin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterventionMedecin extends BaseEntity {
    private Double prixDePatient;
    private Integer numDent;
}
