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
    private int priorite;

}
