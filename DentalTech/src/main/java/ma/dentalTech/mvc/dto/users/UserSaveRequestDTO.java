package ma.dentalTech.mvc.dto.users;

import lombok.*;
import ma.dentalTech.entities.enums.LibelleRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveRequestDTO {
    private String nom;
    private String prenom;
    private String login;
    private String password; // Sera haché par ton PasswordEncoder avant l'enregistrement
    private LibelleRole role;
}