package ma.dentalTech.entities.medicament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.FormeMedicament;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medicament extends BaseEntity {

    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;   // Enum
    private boolean remboursable;
    private Double prixUnitaire;
    private String description;
}
