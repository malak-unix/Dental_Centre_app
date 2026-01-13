package ma.dentalTech.mvc.controllers.modules.auth.common;

import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;

public final class SessionContext {

    private static final SessionContext INSTANCE = new SessionContext();

    private UserPrincipalDTO currentUser;

    private SessionContext() {}

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public UserPrincipalDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserPrincipalDTO currentUser) {
        this.currentUser = currentUser;
    }

    public void clear() {
        this.currentUser = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }
}
