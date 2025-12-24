package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CaisseDashboardResponseDTO {

    private CaisseDashboardRequestDTO filters;

    // Cards
    private Double totalFactures;
    private Double totalRegle;
    private Double totalNonRegle;
    private Double totalRevenus;
    private Double totalCharges;
    private Double soldeNet;

    // Graph
    private CaisseChartDTO chart;

    // Table
    private List<CaisseFactureRowDTO> factures;
}
