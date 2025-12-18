package ma.dentalTech.entities.dossierMedical;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.entities.users.Medecin;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SituationFinanciere extends BaseEntity {

    private Long dossierId;
    private Long medecinId;

    private Double totalDesActes;
    private Double totalPaye;
    private Double credit;

    private StatutSituationFinanciere statut;

    // Relations :
    private DossierMedical dossierMedical;
    private Medecin medecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationFinanciere)) return false;
        SituationFinanciere that = (SituationFinanciere) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            SituationFinanciere {
                id = %s,
                dossierId = %s,
                medecinId = %s,
                totalDesActes = %.2f,
                totalPaye = %.2f,
                credit = %.2f,
                statut = %s
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(dossierId),
                String.valueOf(medecinId),
                totalDesActes != null ? totalDesActes : 0.0,
                totalPaye != null ? totalPaye : 0.0,
                credit != null ? credit : 0.0,
                String.valueOf(statut)
        );
    }
}
