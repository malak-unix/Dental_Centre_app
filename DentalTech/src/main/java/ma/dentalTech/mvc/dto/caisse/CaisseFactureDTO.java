package ma.dentalTech.mvc.dto.caisse;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseFactureDTO {

    private Long factureId;          // pour action details/print/paiement

    private String nom;              // patient nom
    private String prenom;           // patient prenom

    private Double montant;          // total TTC
    private LocalDate dateEmission;  // date émission

    private String statut;           // "PAYEE" | "IMPAYEE" | "ANNULEE"
    private Double reste;            // restant à payer

    // actions visibles (selon rôle + statut)
    private boolean canView;
    private boolean canPrint;
    private boolean canPay;
    private boolean canCancel;
}
