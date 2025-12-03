package ma.dentalTech.entities.situationFinanciere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.EnPromo;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SituationFinanciere extends BaseEntity {

    private Double totaleDesActes;
    private Double totalPaye;
    private Double credit;
    private StatutSituationFinanciere statut; // Enum
    private EnPromo enPromo;                  // Enum
}
