package ma.dentalTech.entities.log;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Admin;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Log extends BaseEntity {

    private String action;
    private String description;
    private LocalDateTime dateAction;
    private Long utilisateurId;   // ID de l’utilisateur qui a fait l’action

    // ==========================
    // Relation : Log -> Admin
    // (l'admin gère les logs, et un log peut être associé à un admin responsable)
    // ==========================
    private Admin admin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Log)) return false;
        Log that = (Log) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Log {
                id = %s,
                action = '%s',
                description = '%s',
                dateAction = %s,
                utilisateurId = %s,
                adminId = %s
            }
            """.formatted(
                String.valueOf(id),
                action,
                description,
                String.valueOf(dateAction),
                String.valueOf(utilisateurId),
                admin == null ? "null" : String.valueOf(admin.getId())
        );
    }
}
