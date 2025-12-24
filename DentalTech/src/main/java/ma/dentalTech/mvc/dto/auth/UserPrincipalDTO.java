package ma.dentalTech.mvc.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.enums.LibelleRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipalDTO {
    private Long id;
    private String login;
    private String nom;
    private String prenom;
    private LibelleRole role; // Ton Enum
}