package ma.dentalTech.entities.facture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facture extends BaseEntity {

    private Long idFacture;
    private Double totaleFacture;
    private Double totalePaye;
    private Double reste;
    private StatutFacture status;       // Enum
    private LocalDateTime dateFacture;
}
