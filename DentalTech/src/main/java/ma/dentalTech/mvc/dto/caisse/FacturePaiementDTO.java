package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FacturePaiementDTO {
    private BigDecimal montant;
}
