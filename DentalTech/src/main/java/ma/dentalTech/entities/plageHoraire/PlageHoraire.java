package ma.dentalTech.entities.plageHoraire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PlageHoraire extends BaseEntity {

    // Lien vers la journée de travail (DetailJournee)
    private Long detailJourneeId;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    // true = disponible, false = occupée
    private Boolean disponible;
}
