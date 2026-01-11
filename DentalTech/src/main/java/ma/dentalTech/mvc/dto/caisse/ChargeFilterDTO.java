package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChargeFilterDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
