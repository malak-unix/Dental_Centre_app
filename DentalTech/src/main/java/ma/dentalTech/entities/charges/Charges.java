package ma.dentalTech.entities.charges;

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
public class Charges extends BaseEntity {

    private Long cabinetId;

    private String titre;
    private String description;
    private Double montant;

    private LocalDateTime dateCharge;
}
