package ma.dentalTech.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Mois;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AgendaMensuel extends BaseEntity {

    private Long medecinId;
    private Mois mois;
    private Integer annee;
}
