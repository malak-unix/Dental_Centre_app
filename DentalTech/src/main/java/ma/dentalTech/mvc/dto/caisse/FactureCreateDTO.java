package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FactureCreateDTO {
    private Long consultationId;
    private LocalDate dateFacture;
    private BigDecimal totalFacture;
}
