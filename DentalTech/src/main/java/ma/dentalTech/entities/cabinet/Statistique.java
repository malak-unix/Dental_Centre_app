package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.enums.CategorieStatistique;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistique {

    private Long id;

    private Long cabinetId;                 // FK: cabinet_medical.id
    private String nom;
    private CategorieStatistique categorie; // ENUM('FINANCIER','ACTIVITE','AUTRE')
    private BigDecimal chiffre;
    private LocalDate dateCloture;

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistique)) return false;
        Statistique that = (Statistique) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
