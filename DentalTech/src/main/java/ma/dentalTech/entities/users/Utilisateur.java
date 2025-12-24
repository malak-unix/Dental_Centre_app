package ma.dentalTech.entities.users;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

/**
 * Entité représentant un utilisateur du système.
 * Classe parent pour Admin, Médecin et Secrétaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Builder
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String cin;
    private String tel;
    private Sexe sexe;
    private String login;
    private String motDePasse;
    private LocalDate lastLoginDate;
    private LocalDate dateNaissance;
    private boolean actif;

    private List<Role> roles = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();

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
                id = %d,
                nom = '%s',
                email = '%s',
                login = '%s',
                rolesCount = %d
            }
            """.formatted(id, nom, email, login, roles == null ? 0 : roles.size());
    }
}
