package ma.dentalTech.mvc.controllers.modules.auth.impl;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.service.modules.auth.api.AuthService;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public AuthResultDTO login(AuthRequestDTO request) {
        if (request == null) {
            return AuthResultDTO.failure("Requête invalide");
        }
        return authService.authenticate(request);
    }
}
