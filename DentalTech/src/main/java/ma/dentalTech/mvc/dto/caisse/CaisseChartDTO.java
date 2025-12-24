package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseChartDTO {
    private String title;            // "Revenus vs Charges"
    private List<String> labels;     // ["Jan", "Feb", ...]
    private List<Double> revenus;    // série revenus
    private List<Double> charges;    // série charges
}
