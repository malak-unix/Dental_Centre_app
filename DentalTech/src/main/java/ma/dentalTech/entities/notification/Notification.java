package ma.dentalTech.entities.notification;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    private String titre;
    private String message;
    private LocalDateTime dateNotification;
    private LocalDateTime dateEnvoi;
    private PrioriteNotification priorite;

    // --- RELATIONS ---

    private Long utilisateurId; // Clé étrangère simple (utile pour JDBC)

    // Relation Many-to-One : Objet parent
    private Utilisateur utilisateur;

    @Override
    public String toString() {
        return """
        Notification {
          id = %d,
          titre = '%s',
          message = '%s',
          priorite = %s,
          utilisateurId = %d
        }
        """.formatted(
                id,
                titre,
                message,
                priorite,
                utilisateur == null ? (utilisateurId == null ? 0 : utilisateurId) : utilisateur.getId()
        );
    }
}