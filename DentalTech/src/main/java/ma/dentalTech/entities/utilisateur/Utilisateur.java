package ma.dentalTech.entities.utilisateur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.StatutUtilisateur;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String adresse;
    private String ville;
    private String email;
    private String telephone;
    private String cin;
    private String login;
    private String motDePasse;
    private StatutUtilisateur statut;      // enum (ACTIF, INACTIF, BLOQUE…)
    private LocalDate dateInscription;
    private LocalDateTime derniereConnexion;
}
