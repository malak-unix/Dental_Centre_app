package ma.dentalTech.entities.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private TitreNotification titre;       // Enum
    private String message;
    private LocalDate date;
    private LocalTime time;
    private TypeNotification type;         // Enum
    private PrioriteNotification priorite; // Enum
}
