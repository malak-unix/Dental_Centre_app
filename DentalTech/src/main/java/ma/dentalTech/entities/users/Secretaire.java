package ma.dentalTech.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Secretaire extends Staff {

    private String numCNSS;
    private Double commission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Secretaire)) return false;
        Secretaire that = (Secretaire) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Secretaire {
                id = %s,
                nom = '%s',
                prenom = '%s',
                login = '%s',
                numCNSS = '%s',
                commission = %.2f
            }
            """.formatted(
                String.valueOf(id),
                getNom(),
                getPrenom(),
                getLogin(),
                numCNSS,
                commission != null ? commission : 0.0
        );
    }
}
