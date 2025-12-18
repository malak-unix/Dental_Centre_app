package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Facture extends BaseEntity {

    private Long consultationId;
    private LocalDate dateFacture;
    private Double totalFacture;
    private Double totalPaye;
    private Double reste;
    private StatutFacture statut;

    // Relation : Consultation 1 -> 1 Facture
    private Consultation consultation;

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

    @Override
    public String toString() {
        return """
            Facture {
                id = %s,
                consultationId = %s,
                dateFacture = %s,
                totalFacture = %.2f,
                totalPaye = %.2f,
                reste = %.2f,
                statut = %s
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(consultationId),
                String.valueOf(dateFacture),
                totalFacture != null ? totalFacture : 0.0,
                totalPaye != null ? totalPaye : 0.0,
                reste != null ? reste : 0.0,
                String.valueOf(statut)
        );
    }
}
