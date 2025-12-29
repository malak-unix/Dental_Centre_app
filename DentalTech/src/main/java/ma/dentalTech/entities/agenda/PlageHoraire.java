package ma.dentalTech.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PlageHoraire extends BaseEntity {

    private Long detailJourneeId;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Boolean disponible;
}
