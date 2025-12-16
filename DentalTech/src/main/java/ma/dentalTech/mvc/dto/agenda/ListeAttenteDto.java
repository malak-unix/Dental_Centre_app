package ma.dentalTech.mvc.dto.agenda;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeAttenteDto {
    private Long id;
    private String nom;
}
