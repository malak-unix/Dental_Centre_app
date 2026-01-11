package ma.dentalTech.mvc.dto.auth;


import java.util.Map;

public record ProfileUpdateResult(
        boolean ok,
        String message,
        ProfileData data,                 // data rechargée après update
        Map<String, String> fieldErrors   // erreurs par champ
) {
    public static ProfileUpdateResult success(String msg, ProfileData data) {
        return new ProfileUpdateResult(true, msg, data, Map.of());
    }
    public static ProfileUpdateResult failure(String msg, Map<String, String> errors) {
        return new ProfileUpdateResult(false, msg, null, errors);
    }
}
