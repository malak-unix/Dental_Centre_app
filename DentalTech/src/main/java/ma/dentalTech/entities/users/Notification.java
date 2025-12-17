package ma.dentalTech.entities.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

    private Long utilisateurId;
    private LocalDateTime dateNotification;
    private PrioriteNotification priorite;
    private String titre;
    private String message;
    private LocalDateTime dateEnvoi;

    // ==========================
    // Relation (diagramme) : Utilisateur 1 -> * Notification
    // ==========================
    private Utilisateur utilisateur;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Notification {
                id = %s,
                utilisateurId = %s,
                priorite = %s,
                titre = '%s',
                dateNotification = %s,
                dateEnvoi = %s
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(utilisateurId),
                String.valueOf(priorite),
                titre,
                String.valueOf(dateNotification),
                String.valueOf(dateEnvoi)
        );
    }
}
