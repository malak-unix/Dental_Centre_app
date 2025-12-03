package ma.dentalTech.entities.agendaMensuel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.Mois;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuperBuilder
public class AgendaMensuel extends BaseEntity {

    private Mois mois;                        // Enum
    private int annee;
    private List<DetailJournee> listeJours;   // Liste<DetailJournee>
}
