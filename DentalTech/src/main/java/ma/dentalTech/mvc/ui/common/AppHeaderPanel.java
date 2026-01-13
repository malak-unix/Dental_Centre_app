package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Header commun (logo + recherche + utilisateur).
 *
 * Le but est d'avoir le même rendu sur tous les écrans (maquettes).
 */
public class AppHeaderPanel extends JPanel {

    private final JLabel logoLabel;
    private final JTextField searchField;
    private final JLabel roleLabel;
    private final JLabel userLabel;
    private final JButton logoutButton;

    public AppHeaderPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(12, 0));

        // LEFT (logo)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        logoLabel = new JLabel("DENTAL CENTER");
        logoLabel.setFont(DentalTheme.H2);
        logoLabel.setForeground(DentalTheme.TEXT2);
        left.add(logoLabel);

        add(left, BorderLayout.WEST);

        // CENTER (search)
        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        center.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(420, 36));
        searchField.setFont(DentalTheme.textFont(13));
        searchField.setText("Rechercher ...");
        center.add(searchField);

        add(center, BorderLayout.CENTER);

        // RIGHT (user)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        roleLabel = new JLabel("—");
        roleLabel.setFont(DentalTheme.textBold(12));
        roleLabel.setForeground(DentalTheme.TEXT2);

        userLabel = new JLabel("Utilisateur");
        userLabel.setFont(DentalTheme.BASE_BOLD);
        userLabel.setForeground(DentalTheme.TEXT2);

        logoutButton = new JButton("⎋");
        logoutButton.setToolTipText("Déconnexion");
        logoutButton.setFocusPainted(false);

        right.add(roleLabel);
        right.add(userLabel);
        right.add(logoutButton);

        add(right, BorderLayout.EAST);
    }

    public void setUser(String displayName, String role) {
        userLabel.setText(displayName == null || displayName.isBlank() ? "Utilisateur" : displayName);
        roleLabel.setText(role == null || role.isBlank() ? "" : role);
    }

    public JLabel logoLabel() {
        return logoLabel;
    }

    public JTextField searchField() {
        return searchField;
    }

    public JButton logoutButton() {
        return logoutButton;
    }
}
