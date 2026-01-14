package ma.dentalTech.mvc.dto.security;

import java.time.LocalDateTime;

public record BackupDTO(
        String fileName,
        LocalDateTime creationDate,
        long sizeInBytes) {
}
