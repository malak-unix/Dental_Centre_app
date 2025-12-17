package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Prescription extends BaseEntity {

    private Long ordonnanceId;
    private Long medicamentId;
    private int quantite;
    private String frequence;
    private int dureeEnJours;

    // Relations :
    private Ordonnance ordonnance;   // Ordonnance 1 -> * Prescriptions
    private Medicament medicament;   // Medicament 1 -> * Prescriptions (côté Prescription : many-to-one)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription)) return false;
        Prescription that = (Prescription) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Prescription {
                id = %s,
                ordonnanceId = %s,
                medicamentId = %s,
                quantite = %d,
                frequence = '%s',
                dureeEnJours = %d
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(ordonnanceId),
                String.valueOf(medicamentId),
                quantite,
                frequence,
                dureeEnJours
        );
    }
}
