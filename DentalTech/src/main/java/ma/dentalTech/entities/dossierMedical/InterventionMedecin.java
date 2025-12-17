package ma.dentalTech.entities.dossierMedical;

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
    private Long consultationId;
    private Long acteId;
    private Double prixDePatient;
    private Integer numDent;
}
