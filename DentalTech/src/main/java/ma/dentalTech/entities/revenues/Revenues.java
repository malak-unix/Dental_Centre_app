package ma.dentalTech.entities.revenues;

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
public class Revenues extends BaseEntity {
    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;
}
