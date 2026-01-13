package ma.dentalTech.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entité représentant un administrateur du système.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Admin extends Staff {

    @Override
    public String toString() {
        return """
            Admin {
                id = %d,
                nom = '%s',
                email = '%s'
            }
            """.formatted(getId(), getNom(), getEmail());
    }
}
