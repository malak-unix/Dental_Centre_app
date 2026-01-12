package ma.dentalTech.mvc.controllers.modules.auth.api;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;

public interface AuthController {

    AuthResultDTO login(AuthRequestDTO request);

    void logout();

    UserPrincipalDTO currentUser();

    boolean isAuthenticated();
}
