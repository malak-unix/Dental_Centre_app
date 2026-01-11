package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthControllerImpl;
import ma.dentalTech.mvc.dto.auth.AuthRequestDTO;
import ma.dentalTech.mvc.dto.auth.AuthResultDTO;
import ma.dentalTech.mvc.ui.MainFrame;
import ma.dentalTech.mvc.ui.common.DentalTheme;
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
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField tfLogin = new JTextField();
    private final JPasswordField tfPassword = new JPasswordField();
    private final JButton btnLogin = new JButton("Se connecter");

    private final AuthController authController;

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

        setContentPane(buildUi());

        btnLogin.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    private JComponent buildUi() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(DentalTheme.BG2);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setBackground(Color.WHITE);

        JLabel title = new JLabel("Connexion");
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        card.add(label("Identifiant"));
        styleField(tfLogin);
        card.add(tfLogin);
        card.add(Box.createVerticalStrut(12));

        card.add(label("Mot de passe"));
        styleField(tfPassword);
        card.add(tfPassword);
        card.add(Box.createVerticalStrut(18));

        stylePrimary(btnLogin);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnLogin);

        root.add(card);
        return root;
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(DentalTheme.textFont(12));
        l.setForeground(DentalTheme.MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleField(JComponent c) {
        c.setMaximumSize(new Dimension(320, 36));
        c.setPreferredSize(new Dimension(320, 36));
        c.setFont(DentalTheme.textFont(13));
    }

    private void stylePrimary(JButton b) {
        b.setFont(DentalTheme.textFont(13));
        b.setFocusPainted(false);
        b.setBackground(DentalTheme.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
    }

    private void doLogin() {
        String login = tfLogin.getText() != null ? tfLogin.getText().trim() : "";
        String password = new String(tfPassword.getPassword());

        AuthResultDTO res = authController.login(new AuthRequestDTO(login, password));

        if (!res.isSuccess()) {
            String msg = res.getMessage();
            if (res.getFieldErrors() != null && !res.getFieldErrors().isEmpty()) {
                msg += "\n" + res.getFieldErrors().toString();
            }
            JOptionPane.showMessageDialog(this, msg, "Connexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // OK -> ouvrir MainFrame(principal)
        MainFrame main = new MainFrame(res.getPrincipal());
        main.setVisible(true);
        dispose();
    }
}
