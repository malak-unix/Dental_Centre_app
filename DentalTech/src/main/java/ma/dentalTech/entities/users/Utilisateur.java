package ma.dentalTech.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String cin;
    private String tel;
    private Sexe sexe;
    private String login;
    private String motDePass_hash;
    private LocalDate lastLoginDate;
    private LocalDate dateNaissance;
    private boolean actif;

    // ==========================
    // Relations (diagramme)
    // Utilisateur * <-> * Role
    // Utilisateur 1 -> * Notification
    // ==========================

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
                id = %s,
                nom = '%s',
                prenom = '%s',
                email = '%s',
                cin = '%s',
                login = '%s',
                sexe = %s,
                actif = %s,
                rolesCount = %d,
                notificationsCount = %d
            }
            """.formatted(
                String.valueOf(id),
                nom,
                prenom,
                email,
                cin,
                login,
                String.valueOf(sexe),
                actif,
                roles == null ? 0 : roles.size(),
                notifications == null ? 0 : notifications.size()
        );
    }
}
