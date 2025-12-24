package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.mvc.dto.auth.*;

public interface AuthService {

    AuthResultDTO authenticate(AuthRequestDTO request);

    UserPrincipalDTO loadUserPrincipalByLogin(String login);

    void changePassword(Long userId, String oldPassword, String newPassword);
}