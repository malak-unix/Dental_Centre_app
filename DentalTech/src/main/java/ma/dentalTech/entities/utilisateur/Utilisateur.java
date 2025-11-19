package ma.dentalTech.entities.utilisateur;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // ✅ Correction ici : SuperBuilder
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.StatutUtilisateur;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity // ✅ Manquait sur ton image
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED) // ✅ Important pour l'héritage Admin
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    private Sexe sexe;

    private String adresse;
    private String ville;
    private String email;
    private String telephone;
    private String cin;

    @Column(unique = true)
    private String login;
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private StatutUtilisateur statut;

    private LocalDate dateInscription;
    private LocalDateTime derniereConnexion;
}