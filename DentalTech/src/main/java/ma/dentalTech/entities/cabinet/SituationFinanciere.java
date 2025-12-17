package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituationFinanciere {

    private Long id;

    private Long dossierId;                      // FK: dossier_medical.id (UNIQUE)
    private Long medecinId;                      // FK: medecin.id (nullable)

    private BigDecimal totalDesActes;
    private BigDecimal totalPaye;
    private BigDecimal credit;

    private StatutSituationFinanciere statut;    // ENUM('NORMAL','EN_CREANCE','EN_PROMO')

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationFinanciere)) return false;
        SituationFinanciere that = (SituationFinanciere) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
