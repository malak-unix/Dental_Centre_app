package ma.dentalTech.mvc.dto.users;

import lombok.*;
import ma.dentalTech.entities.enums.LibelleRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String login;
    private LibelleRole role; // Utilise ton Enum existante
    private boolean actif;    // Pour savoir si le compte est activé ou non
}