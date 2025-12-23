package ma.dentalTech.entities.cabinet;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Charges {

    private Long id;

    private Long cabinetId;          // FK: cabinet_medical.id
    private String titre;
    private String description;
    private BigDecimal montant;
    private LocalDateTime dateCharge;

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Charges)) return false;
        Charges that = (Charges) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
