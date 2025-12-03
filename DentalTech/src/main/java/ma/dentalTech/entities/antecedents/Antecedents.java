package ma.dentalTech.entities.antecedents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.NiveauDeRisque;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Antecedents extends BaseEntity {

    private Long idAntecedent;
    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque; // Enum
}
