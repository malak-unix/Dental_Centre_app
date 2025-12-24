package ma.dentalTech.entities.utilisateur;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.role.Role;
import ma.dentalTech.entities.notification.Notification;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Utilisateur extends BaseEntity {

    private String nom;
    private String prenom;
    private String cin;
    private String adresse;
    private String tel;
    private String email;
    private String login;
    private String motDePass_hash;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private boolean actif;
    private LocalDateTime lastLoginDate;

    // --- RELATIONS ---

    // Relation Many-to-One : Un Utilisateur a 1 Role
    private Role role;

    // Relation One-to-Many : Un Utilisateur a N Notifications
    @Builder.Default // Important pour que le Builder n'écrase pas l'initialisation
    private List<Notification> notifications = new ArrayList<>();

    @Override
    public String toString() {
        return """
        Utilisateur {
          id = %d,
          nom = '%s',
          prenom = '%s',
          login = '%s',
          email = '%s',
          role = %s,
          actif = %b,
          notificationsCount = %d
        }
        """.formatted(
                id,
                nom,
                prenom,
                login,
                email,
                role == null ? "Aucun" : role.getLibelle(), // On affiche juste le libellé pour éviter la boucle
                actif,
                notifications == null ? 0 : notifications.size() // On affiche la taille de la liste
        );
    }
}