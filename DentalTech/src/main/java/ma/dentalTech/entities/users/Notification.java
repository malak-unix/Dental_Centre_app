package ma.dentalTech.entities.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private Long utilisateurId;
    private LocalDateTime dateNotification;
    private PrioriteNotification priorite;
    private String titre;
    private String message;
    //private TypeNotification type;   // Enum
    //private boolean lu;
    private LocalDateTime dateEnvoi;
}
