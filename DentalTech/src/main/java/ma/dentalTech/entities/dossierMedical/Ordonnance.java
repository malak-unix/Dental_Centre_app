package ma.dentalTech.entities.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Ordonnance extends BaseEntity {

    private Long dossierId;
    private Long consultationId;
    private LocalDate date;

    // Relations :
    private DossierMedical dossierMedical;
    private Consultation consultation;
    private List<Prescription> prescriptions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ordonnance)) return false;
        Ordonnance that = (Ordonnance) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Ordonnance {
                id = %s,
                dossierId = %s,
                consultationId = %s,
                date = %s,
                prescriptionsCount = %d
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(dossierId),
                String.valueOf(consultationId),
                String.valueOf(date),
                prescriptions == null ? 0 : prescriptions.size()
        );
    }
}
