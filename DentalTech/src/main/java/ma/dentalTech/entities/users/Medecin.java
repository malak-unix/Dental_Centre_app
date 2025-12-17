package ma.dentalTech.entities.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Medecin extends Staff {

    private String specialite;
    private Double pourcentage;  // % de commission par acte

    // ==========================
    // Relation (diagramme) : Médecin 1 -> 1 AgendaMensuel
    // (type Object tant que AgendaMensuel n'existe pas)
    // ==========================
    private Object agendaMensuel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medecin)) return false;
        Medecin that = (Medecin) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Medecin {
                id = %s,
                nom = '%s',
                prenom = '%s',
                login = '%s',
                specialite = '%s',
                pourcentage = %.2f
            }
            """.formatted(
                String.valueOf(id),
                getNom(),
                getPrenom(),
                getLogin(),
                specialite,
                pourcentage != null ? pourcentage : 0.0
        );
    }
}
