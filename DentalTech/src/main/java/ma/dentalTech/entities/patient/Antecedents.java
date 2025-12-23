package ma.dentalTech.entities.patient;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.NiveauDeRisque;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Antecedents extends BaseEntity {
    private String nom;
    private String categorie;
    private NiveauDeRisque niveauDeRisque;
    private String description;

    private List<Patient> patients = new ArrayList<>();

    @Override
    public String toString() {
        return """
            Antecedents {
                id = %s,
                nom = '%s',
                categorie = '%s',
                niveauDeRisque = %s,
                description = '%s',
                patientsCount = %d
            }
            """.formatted(
                String.valueOf(id),
                nom,
                categorie,
                String.valueOf(niveauDeRisque),
                description,
                patients == null ? 0 : patients.size()
        );
    }


}
