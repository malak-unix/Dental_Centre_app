package ma.dentalTech.entities.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterventionMedecin extends BaseEntity {

    private Long consultationId;
    private Long acteId;
    private Double prixDePatient;
    private Integer numDent;

    // Relations :
    private Consultation consultation; // Consultation 1 -> * Interventions
    private Acte acte;                // Acte 1 -> * Interventions (côté Intervention : many-to-one)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterventionMedecin)) return false;
        InterventionMedecin that = (InterventionMedecin) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            InterventionMedecin {
                id = %s,
                consultationId = %s,
                acteId = %s,
                prixDePatient = %.2f,
                numDent = %s
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(consultationId),
                String.valueOf(acteId),
                prixDePatient != null ? prixDePatient : 0.0,
                String.valueOf(numDent)
        );
    }
}
