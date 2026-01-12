package ma.dentalTech.mvc.controllers.modules.auth.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.common.SessionContext;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.service.modules.auth.api.AuthService;

@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final SessionContext session = SessionContext.getInstance();

    @Override
    public AuthResultDTO login(AuthRequestDTO request) {
        AuthResultDTO result = authService.authenticate(request);

        if (result != null && result.isSuccess()) {
            session.setCurrentUser(result.getPrincipal());
        }
        return result;
    }
}
