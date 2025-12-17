package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    private Long id;

    private Long consultationId;      // FK: consultation.id (nullable)
    private LocalDate dateFacture;

    private BigDecimal totalFacture;
    private BigDecimal totalPaye;
    private BigDecimal reste;         // colonne calculée en DB
    private StatutFacture statut;     // ENUM('NON_PAYEE','PARTIEL','PAYEE')

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture)) return false;
        Facture that = (Facture) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
