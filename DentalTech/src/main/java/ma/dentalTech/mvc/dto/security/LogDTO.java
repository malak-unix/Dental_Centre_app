package ma.dentalTech.mvc.dto.security;

import java.time.LocalDateTime;

public record LogDTO(
        Long id,
        String utilisateur,
        String action,
        String description,
        LocalDateTime dateAction) {
}
