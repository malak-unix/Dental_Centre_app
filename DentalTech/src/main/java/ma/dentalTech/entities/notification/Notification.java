package ma.dentalTech.entities.notification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.TypeNotification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private String titre;
    private String message;
    private TypeNotification type;   // Enum
    private boolean lu;
    private LocalDateTime dateEnvoi;
}
