package ma.dentalTech.entities.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.FormeMedicament;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Medicament extends BaseEntity {

    private String nom;
    private String laboratoire;
    private String type;
    private FormeMedicament forme;   // Enum
    private boolean remboursable;
    private Double prixUnitaire;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Medicament {
                id = %s,
                nom = '%s',
                laboratoire = '%s',
                forme = %s,
                remboursable = %s,
                prixUnitaire = %.2f
            }
            """.formatted(
                String.valueOf(id),
                nom,
                laboratoire,
                String.valueOf(forme),
                remboursable,
                prixUnitaire != null ? prixUnitaire : 0.0
        );
    }
}
