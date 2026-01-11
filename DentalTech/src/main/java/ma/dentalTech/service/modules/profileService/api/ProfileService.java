package ma.dentalTech.service.modules.profileService.api;

// On importe tes DTOs depuis le dossier 'auth' comme sur ton arborescence
import ma.dentalTech.mvc.dto.auth.ProfileData;
import ma.dentalTech.mvc.dto.auth.ProfileUpdateRequest;
import ma.dentalTech.mvc.dto.auth.ProfileUpdateResult;
import ma.dentalTech.mvc.dto.auth.ChangePasswordRequest;
import ma.dentalTech.mvc.dto.auth.ChangePasswordResult;

public interface ProfileService {
    ProfileData loadByUserId(Long userId);
    ProfileUpdateResult update(ProfileUpdateRequest req);
    ChangePasswordResult changePassword(ChangePasswordRequest req);
}