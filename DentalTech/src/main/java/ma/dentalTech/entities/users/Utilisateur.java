package ma.dentalTech.entities.users;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Utilisateur extends BaseEntity {

    protected String nom; // passage en protected pour faciliter l'accès aux enfants
    protected String prenom;
    protected String email;
    protected String adresse;
    protected String cin;
    protected String tel;
    protected Sexe sexe;
    protected String login;
    protected String motDePasse;
    protected LocalDate lastLoginDate;
    protected LocalDate dateNaissance;
    protected boolean actif;

    private List<Role> roles = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

    // Constructeur manuel pour l'héritage
    public Utilisateur(String nom, String prenom, String email, String adresse, String cin, String tel,
                       Sexe sexe, String login, String motDePasse, LocalDate lastLoginDate,
                       LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.cin = cin;
        this.tel = tel;
        this.sexe = sexe;
        this.login = login;
        this.motDePasse = motDePasse;
        this.lastLoginDate = lastLoginDate;
        this.dateNaissance = dateNaissance;
        this.actif = true;
    }

    // ... toString et hashCode inchangés
}