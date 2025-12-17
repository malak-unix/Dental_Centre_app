package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Consultation extends BaseEntity {

    private Long dossierId;               // FK vers DossierMedical
    private LocalDate date;
    private StatutConsultation status;
    private String observationMedecin;

    // Relations (diagramme : DossierMedical 1 -> * Consultations)
    private DossierMedical dossierMedical;

    // Diagramme : Consultation 1 -> 1 Facture
    private Facture facture;

    // Diagramme : Consultation 1 -> * InterventionMedecin
    private List<InterventionMedecin> interventionMedecins = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consultation)) return false;
        Consultation that = (Consultation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Consultation {
                id = %s,
                dossierId = %s,
                date = %s,
                status = %s,
                interventionsCount = %d,
                hasFacture = %s
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(dossierId),
                String.valueOf(date),
                String.valueOf(status),
                interventionMedecins == null ? 0 : interventionMedecins.size(),
                facture != null
        );
    }
}
