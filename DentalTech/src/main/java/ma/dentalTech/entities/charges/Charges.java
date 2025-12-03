package ma.dentalTech.entities.charges;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Charges extends BaseEntity {

    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;
}
