package ma.dentalTech.entities.patient;

import lombok.*;
import ma.dentalTech.entities.enums.NiveauDeRisque;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Antecedents {

    private Long id;

    // FK
    private Long patientId;

    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque; // DB: enum('FAIBLE','MOYEN','ELEVE')
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Antecedents)) return false;
        Antecedents that = (Antecedents) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
