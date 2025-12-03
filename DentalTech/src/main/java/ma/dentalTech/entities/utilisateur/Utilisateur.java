package ma.dentalTech.entities.utilisateur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Utilisateur extends BaseEntity {

    private Long idUser;
    private String nom;
    private String email;
    // sur le diagramme : adresse : Adresse -> on garde String
    private String adresse;
    private String cin;
    private String tel;
    private Sexe sexe;                 // Enum
    private String login;
    private String motDePass;
    private LocalDate lastLoginDate;
    private LocalDate dateNaissance;
}
