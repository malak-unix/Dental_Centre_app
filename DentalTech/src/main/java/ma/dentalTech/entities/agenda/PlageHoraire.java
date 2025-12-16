package ma.dentalTech.entities.agenda;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlageHoraire {

    private Long id;

    private Long detailJourneeId;   // ✅ schema: plage_horaire.detail_journee_id
    private LocalTime heureDebut;   // ✅ schema: heure_debut
    private LocalTime heureFin;     // ✅ schema: heure_fin
    private Boolean disponible;     // ✅ schema: disponible

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    // Relation optionnelle
    private DetailJournee detailJournee;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlageHoraire)) return false;
        PlageHoraire that = (PlageHoraire) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
