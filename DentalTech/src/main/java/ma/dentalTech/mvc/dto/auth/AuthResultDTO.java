package ma.dentalTech.mvc.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResultDTO {
    private boolean success;
    private String message;
    private UserPrincipalDTO principal;
}