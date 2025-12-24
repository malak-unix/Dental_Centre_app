package ma.dentalTech.mvc.dto.users;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSecretaireRequestDTO {
    private String nom;
    private String prenom;
    private String login;
    private String password;
    private String numCNSS;
}