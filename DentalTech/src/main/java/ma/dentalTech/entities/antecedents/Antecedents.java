package ma.dentalTech.entities.antecedents;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.NiveauDeRisque;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Antecedents extends BaseEntity {
    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque;
    private String description;
}
