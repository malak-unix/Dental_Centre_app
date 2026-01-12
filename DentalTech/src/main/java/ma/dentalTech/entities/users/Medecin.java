package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Sexe;

/**
 * Entité représentant un médecin dentiste.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;
    private AgendaMensuel agendaMensuel;

    @Override
    public String toString() {
        return """
            Medecin {
                id = %d,
                nom = '%s',
                specialite = '%s'
            }
            """.formatted(getId(), getNom(), specialite);
    }
}
