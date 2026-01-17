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
    private Long patientId;
    private String patientNom;
    private String motif;
    private LocalDateTime dateAjout;
    private String priorite;
}
