package ma.dentalTech.entities.dossierMedical;

import lombok.experimental.SuperBuilder;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Acte extends BaseEntity {

    private String libelle;
    private String categorie;
    private Double prixBase;
    private String description;

    List<InterventionMedecin> interventionMedecins = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acte)) return false;
        Acte that = (Acte) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Acte {
                id = %s,
                libelle = '%s',
                categorie = '%s',
                prixBase = %.2f,
                interventionsCount = %d
            }
            """.formatted(
                String.valueOf(id),
                libelle,
                categorie,
                prixBase != null ? prixBase : 0.0,
                interventionMedecins == null ? 0 : interventionMedecins.size()
        );
    }
}
