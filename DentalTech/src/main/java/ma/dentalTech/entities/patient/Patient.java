package ma.dentalTech.entities.patient;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Patient {

    private Long id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;                 // DB: enum('H','F')
    private String telephone;
    private String adresse;
    private Assurance assurance;       // DB: enum('CNSS','CNOPS','Mutuelle','Autre','Aucune')

    // FK vers table base_entity (audit)
    private Long baseEntityId;

    // Audit (chargé via JOIN base_entity)
    private LocalDateTime dateCreation;
    private LocalDateTime datedeModification;
    private String creePar;
    private String modifiePar;

    // Relation 1-N (optionnelle, si tu veux la charger plus tard)
    @Builder.Default
    private List<Antecedents> antecedents = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient that = (Patient) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
