package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;
import ma.dentalTech.entities.enums.TypeRendezVous;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDV {

    private Long id;

    // FK
    private Long patientId;
    private Long detailJourneeId;   // ✅ existe dans schema: rdv.detail_journee_id
    private Long listeAttenteId;    // ✅ existe dans schema: rdv.liste_attente_id

    private TypeRendezVous typeRdv;

    // champs métier
    private LocalDate dateRdv;      // ✅ schema: date_rdv
    private LocalTime heure;        // ✅ schema: heure
    private String motif;
    private String statut;          // ou enum si vous l'avez
    private String noteMedecin;

    // BaseEntity (si vous les gardez dans l'entity)
    private java.time.LocalDateTime dateCreation;
    private java.time.LocalDateTime dateModification;
    private String creePar;
    private String modifiePar;

    // Relations optionnelles (chargées via JOIN seulement si besoin)
    private DetailJournee detailJournee;
    // private Patient patient;        // si vous avez l'entity Patient
    // private ListeAttente listeAttente; // si vous avez l'entity ListeAttente

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}