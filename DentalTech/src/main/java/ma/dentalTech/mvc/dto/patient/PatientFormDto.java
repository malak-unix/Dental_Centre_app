package ma.dentalTech.mvc.dto.patient;

import lombok.*;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientFormDto {
    private Long id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;

    private String telephone;
    private String adresse;
    private Assurance assurance;
}
