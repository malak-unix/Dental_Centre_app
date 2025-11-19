package ma.dentalTech.entities.antecedents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeAntecedent;
import ma.dentalTech.entities.enums.NiveauGravite;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Antecedents extends BaseEntity {

    private TypeAntecedent type;
    private String categorie;
    private NiveauGravite niveauGravite;
}