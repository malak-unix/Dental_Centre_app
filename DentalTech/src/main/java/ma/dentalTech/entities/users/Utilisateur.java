package ma.dentalTech.entities.users;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Utilisateur extends BaseEntity {

    // Passage en protected pour faciliter l'accès aux enfants (Staff, etc.)
    protected String nom;
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

    private Long roleId;

    private List<Notification> notifications = new ArrayList<>();
    public Utilisateur(
            String nom,
            String prenom,
            String email,
            String adresse,
            String cin,
            String tel,
            Sexe sexe,
            String login,
            String motDePasse,
            LocalDate lastLoginDate,
            LocalDate dateNaissance,
            Long roleId
    ) {
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
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Utilisateur {
                id = %s,
                nom = '%s',
                prenom = '%s',
                login = '%s',
                email = '%s',
                actif = %s,
                roleId = %s
            }
            """.formatted(
                String.valueOf(id),
                nom,
                prenom,
                login,
                email,
                actif,
                String.valueOf(roleId)
        );
    }
}
