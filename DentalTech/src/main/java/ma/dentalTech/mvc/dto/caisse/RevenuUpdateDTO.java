package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RevenuUpdateDTO {
    private String titre;
    private String description;
    private BigDecimal montant;
    private LocalDateTime dateRevenu;
}
