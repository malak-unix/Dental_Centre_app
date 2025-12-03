package ma.dentalTech.entities.facture;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Facture extends BaseEntity {
    private Double totaleFacture;
    private Double totalePaye;
    private Double reste;
    private StatutFacture status;
    private LocalDateTime dateFacture;
}
