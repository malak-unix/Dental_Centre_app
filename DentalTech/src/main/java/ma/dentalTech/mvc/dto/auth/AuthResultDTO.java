package ma.dentalTech.mvc.dto.auth;

import java.util.Map;

public class AuthResultDTO {
    private final boolean success;
    private final String message;
    private final UserPrincipalDTO principal;
    private final Map<String, String> fieldErrors;

    private AuthResultDTO(boolean success, String message, UserPrincipalDTO principal, Map<String, String> fieldErrors) {
        this.success = success;
        this.message = message;
        this.principal = principal;
        this.fieldErrors = fieldErrors;
    }

    public static AuthResultDTO success(UserPrincipalDTO principal) {
        return new AuthResultDTO(true, "OK", principal, Map.of());
    }

    public static AuthResultDTO failure(String message) {
        return new AuthResultDTO(false, message, null, Map.of());
    }

    public static AuthResultDTO failure(String message, Map<String, String> fieldErrors) {
        return new AuthResultDTO(false, message, null, fieldErrors);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserPrincipalDTO getPrincipal() { return principal; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}