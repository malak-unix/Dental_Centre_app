package ma.dentalTech.service.modules.profileService.api;

import java.util.Map;
import ma.dentalTech.mvc.dto.auth.ProfileUpdateRequest;

public interface ProfileValidator {
    Map<String, String> validate(ProfileUpdateRequest req);
}