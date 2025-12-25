package ma.dentalTech.mvc.dto.agenda;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeAttenteDto {
    private Long id;
    private String nom;

    //ajouté par aya :
    private Long patientid;
    private String patientNom;
    private String priorite;          // NORMAL / URGENT (ou juste texte)
    private String statut;            // EN_ATTENTE / APPELE / ANNULE ...
    private Boolean arrive;           // true si le patient est arrivé


}
