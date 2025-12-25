package ma.dentalTech.mvc.dto.caisse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseFactureRowDTO {

    // =========================
    // Identité facture
    // =========================
    private Long factureId;
    private String numeroFacture;   // ex: #T00355
    private LocalDate dateFacture;
    private BigDecimal totalFacture;
    private BigDecimal totalPaye;
    private BigDecimal reste;

    // =========================
    // Infos affichage
    // =========================
    private String nom;
    private String prenom;
    private String medecinNom;
    private String patientNom;
    private LocalDate dateEmission;
    private Double montant;
    private String statut; // PAYEE, IMPAYEE, ANNULEE

    // =========================
    // Droits / actions (UI)
    // =========================
    private boolean canView;
    private boolean canPrint;
    private boolean canPay;
    private boolean canCancel;

    private Long consultationId;

}
