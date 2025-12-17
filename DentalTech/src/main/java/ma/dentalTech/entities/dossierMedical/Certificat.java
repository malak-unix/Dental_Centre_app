package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Medecin;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Certificat extends BaseEntity {

    private Long dossierId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int duree;
    private String noteMedecin;

    // Relations (diagramme : Certificat lié au Dossier + Médecin)
    private DossierMedical dossierMedical;
    private Medecin medecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificat)) return false;
        Certificat that = (Certificat) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Certificat {
                id = %s,
                dossierId = %s,
                dateDebut = %s,
                dateFin = %s,
                duree = %d
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(dossierId),
                String.valueOf(dateDebut),
                String.valueOf(dateFin),
                duree
        );
    }
}
