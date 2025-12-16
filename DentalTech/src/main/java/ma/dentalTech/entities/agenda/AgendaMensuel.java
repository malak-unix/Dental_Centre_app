package ma.dentalTech.entities.agenda;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.enums.Mois;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuel {

    private Long id;

    private Long medecinId;     // ✅ schema: medecin_id
    private Mois mois;          // ✅ enum SQL
    private Integer annee;

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    // Relations optionnelles (chargées via JOIN)
    @Builder.Default
    private List<DetailJournee> jours = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaMensuel)) return false;
        AgendaMensuel that = (AgendaMensuel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
