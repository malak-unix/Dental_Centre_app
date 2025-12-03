package ma.dentalTech.entities.antecedents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.NiveauDeRisque;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Antecedents extends BaseEntity {

    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque; // Enum
}
