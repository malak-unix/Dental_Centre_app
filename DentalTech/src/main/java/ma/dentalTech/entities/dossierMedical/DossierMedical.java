package ma.dentalTech.entities.dossierMedical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import ma.dentalTech.entities.base.BaseEntity;
// ⚠️ Ajuste cet import selon ton projet
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
public class DossierMedical extends BaseEntity {

    private Long patientId;
    private Long medecinId;
    private String notes;


    // Relation : DossierMedical (1) <-> (1) Patient (d'après diagramme)
    private Patient patient;

    // Relation : DossierMedical (1) -> (*) Consultations
    private List<Consultation> consultations = new ArrayList<>();

    // Relation : DossierMedical (1) -> (*) Ordonnances
    private List<Ordonnance> ordonnances = new ArrayList<>();

    // Relation : DossierMedical (1) -> (*) Certificats
    private List<Certificat> certificats = new ArrayList<>();

    // Relation : DossierMedical (1) -> (1) SituationFinanciere (selon ton diagramme)
    private SituationFinanciere situationFinanciere;

    private Medecin medecin;
    public DossierMedical() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierMedical)) return false;
        DossierMedical that = (DossierMedical) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            DossierMedical {
                id = %s,
                patient = %s,
                consultationsCount = %d,
                ordonnancesCount = %d,
                certificatsCount = %d
            }
            """.formatted(
                String.valueOf(id),
                patient == null ? "null" : String.valueOf(patient),
                consultations == null ? 0 : consultations.size(),
                ordonnances == null ? 0 : ordonnances.size(),
                certificats == null ? 0 : certificats.size()
        );
    }
}
