package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BilanFinancierDTO {
    private LocalDate periodeStart;
    private LocalDate periodeEnd;
    private BigDecimal recettesTotalFacture; // Total payé par les patients
    private BigDecimal recettesAutres;       // Total des Revenues (autres)
    private BigDecimal depensesTotal;        // Total des Charges
    private BigDecimal resultatNet;          // Recettes Totales - Dépenses Totales
    private String statut;                   // POSITIVE, NEGATIVE, EQUILIBRE
}