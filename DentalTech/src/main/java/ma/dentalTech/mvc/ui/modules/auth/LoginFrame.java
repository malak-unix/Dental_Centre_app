package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthControllerImpl;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.mvc.dto.auth.UserPrincipalDTO;
import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.common.UiTheme;

import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.impl.RoleRepositoryImpl;
import ma.dentalTech.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.LoginFormValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.impl.AuthServiceImpl;
import ma.dentalTech.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.dentalTech.service.modules.auth.impl.PasswordEncoderImpl;

import javax.swing.*;

public class LoginFrame extends JFrame {

    private final AuthController authController;
    private final LoginPanel panel = new LoginPanel();

    public LoginFrame() {
        super("DentalTech - Connexion");

        UiTheme.install();

        // Wiring simple (sans toucher ApplicationContext)
        RepoFactory<UtilisateurRepository> userFactory = UtilisateurRepositoryImpl::new;
        RepoFactory<RoleRepository> roleFactory = RoleRepositoryImpl::new;
        LoginFormValidator validator = new CredentialsValidatorImpl();
        PasswordEncoder encoder = new PasswordEncoderImpl();
        AuthService authService = new AuthServiceImpl(userFactory, roleFactory, validator, encoder);
        this.authController = new AuthControllerImpl(authService);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        setContentPane(panel);

        panel.loginButton().addActionListener(e -> doLogin());
        panel.cancelButton().addActionListener(e -> dispose());
        getRootPane().setDefaultButton(panel.loginButton());
    }

    private void doLogin() {
        String login = panel.loginField().getText() != null ? panel.loginField().getText().trim() : "";
        String password = new String(panel.passwordField().getPassword());

        AuthResultDTO res = authController.login(new AuthRequestDTO(login, password));

        if (res == null || !res.isSuccess()) {
            String msg = (res != null && res.getMessage() != null) ? res.getMessage() : "Échec de connexion";
            if (res != null && res.getFieldErrors() != null && !res.getFieldErrors().isEmpty()) {
                msg += "\n" + res.getFieldErrors();
            }
            JOptionPane.showMessageDialog(this, msg, "Connexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserPrincipalDTO p = res.getPrincipal();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Connexion OK mais principal null", "Connexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long userId = (p.id() != null) ? p.id() : 1L;
        String fullName = (p.nom() != null && !p.nom().isBlank()) ? p.nom() : p.login();
        if (fullName == null || fullName.isBlank()) fullName = "Utilisateur";

        MainFrame main = new MainFrame(p.rolePrincipal(), userId, fullName);
        main.setVisible(true);
        dispose();
    }
}
