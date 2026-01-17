package ma.dentalTech.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ListeAttente extends BaseEntity {

    private Long patientId;
    private String nom;
    private String motif;
    private java.time.LocalDateTime dateAjout;
    private String priorite;
}
