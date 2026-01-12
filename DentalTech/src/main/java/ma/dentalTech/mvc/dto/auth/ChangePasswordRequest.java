package ma.dentalTech.mvc.dto.auth;


import lombok.Builder;

@Builder
public record ChangePasswordRequest(
        Long userId,
        String currentPassword,
        String newPassword,
        String confirmPassword
) {}
