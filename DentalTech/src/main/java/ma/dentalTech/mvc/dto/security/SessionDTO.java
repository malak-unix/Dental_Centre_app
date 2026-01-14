package ma.dentalTech.mvc.dto.security;

import java.time.LocalDateTime;

public record SessionDTO(
        Long userId,
        String username,
        String role,
        LocalDateTime loginTime,
        String status) {
}
