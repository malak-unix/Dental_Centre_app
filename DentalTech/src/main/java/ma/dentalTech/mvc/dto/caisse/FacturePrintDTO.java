package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FacturePrintDTO {
    private String numeroFacture;
    private LocalDate dateFacture;

    private Long consultationId;

    private BigDecimal totalFacture;
    private BigDecimal totalPaye;
    private BigDecimal reste;

    private String statut;
}
