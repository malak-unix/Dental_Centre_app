package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaisseDashboardDTO {

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;


    private Double totalFactures;
    private Double totalRegle;
    private Double totalNonRegle;

    private Double totalRevenus;

    private Double totalCharges;

    private Double soldeNet;
}
