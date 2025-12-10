package ma.dentalTech.entities.situationFinanciere;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SituationFinanciere extends BaseEntity {

    private Long dossierId;
    private Long medecinId;

    private Double totalDesActes;
    private Double totalPaye;
    private Double credit;

    private StatutSituationFinanciere statut;
}
