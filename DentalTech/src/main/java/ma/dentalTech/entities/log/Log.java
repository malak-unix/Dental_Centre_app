package ma.dentalTech.entities.logs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Logs extends BaseEntity {

    private String action;
    private String description;
    private LocalDateTime dateAction;
    private Long utilisateurId;   // ID de l’utilisateur qui a fait l’action
}
