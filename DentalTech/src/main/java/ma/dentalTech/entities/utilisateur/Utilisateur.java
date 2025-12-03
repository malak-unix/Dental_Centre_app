package ma.dentalTech.entities.utilisateur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String cin;
    private String tel;
    private Sexe sexe;                 // Enum
    private String login;
    private String motDePass_hash;
    private LocalDate lastLoginDate;
    private LocalDate dateNaissance;
    private boolean actif;
}

