package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FactureUpdateDTO {
    private List<FactureLineCreateDTO> lignes;
}
