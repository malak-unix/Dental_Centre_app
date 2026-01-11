package ma.dentalTech.mvc.controllers.modules.auth.api;

import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;

public interface AuthController {
    AuthResultDTO login(AuthRequestDTO request);
}
