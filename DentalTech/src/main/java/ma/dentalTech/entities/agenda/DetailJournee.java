package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;
import ma.dentalTech.entities.enums.StatutJournee;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailJournee {

    private Long id;

    private Long agendaId;               // ✅ schema: detail_journee.agenda_id

    private LocalDate dateJour;          // ✅ schema: date_jour
    private LocalTime heureDebutTravail; // ✅ schema: heure_debut_travail
    private LocalTime heureFinTravail;   // ✅ schema: heure_fin_travail

    // ✅ IMPORTANT: Enum (plus de String)
    private StatutJournee etatJour;      // ✅ schema: etat_jour

    private String commentaire;

    // BaseEntity
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    // Relation optionnelle
    private AgendaMensuel agendaMensuel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailJournee)) return false;
        DetailJournee that = (DetailJournee) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}