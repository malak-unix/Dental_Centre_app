package ma.dentalTech.mvc.dto.users;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdminRequestDTO {
    private String nom;
    private String prenom;
    private String login;
    private String password;
}
