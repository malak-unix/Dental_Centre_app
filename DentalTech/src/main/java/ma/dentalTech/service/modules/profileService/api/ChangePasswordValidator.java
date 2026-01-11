package ma.dentalTech.service.modules.profileService.api;


import java.util.Map;
import ma.dentalTech.mvc.dto.auth.ChangePasswordRequest;

public interface ChangePasswordValidator {
    Map<String, String> validate(ChangePasswordRequest req);
}
