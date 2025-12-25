package ma.dentalTech.mvc.dto.caisse;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturePrintLineDTO {
    private String designation;  // acte/soin
    private Integer quantite;
    private Double prixUnitaire;
    private Double totalLigne;
}
