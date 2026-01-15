package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class AppHeaderPanel extends JPanel {

    private final JLabel logoLabel = new JLabel();
    private final JLabel userLabel = new JLabel();
    private final JButton logout = new JButton("⎋");

    public AppHeaderPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 70));
        setOpaque(true);
        setBackground(DentalTheme.BG_HEADER);

        // LEFT logo
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        logoLabel.setIcon(loadLogoIcon());
        add(logoLabel, BorderLayout.WEST);

        // RIGHT user + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        right.setOpaque(false);

        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(DentalTheme.TEXT);

        logout.setFocusable(false);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logout.setForeground(DentalTheme.TEXT);

        right.add(userLabel);
        right.add(logout);

        add(right, BorderLayout.EAST);
    }

    private Icon loadLogoIcon() {
        // ✅ Le logo doit être dans src/main/resources/assets/logo.png
        java.net.URL url = getClass().getResource("/assets/logo.png");
        if (url == null) {
            // fallback: texte si le logo n'existe pas
            logoLabel.setText("DENTAL CENTER");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 22));
            logoLabel.setForeground(DentalTheme.TEXT);
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(140, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public void setUser(String fullName, String roleLabel) {
        String n = (fullName == null || fullName.isBlank()) ? "Utilisateur" : fullName.trim();
        String r = (roleLabel == null || roleLabel.isBlank()) ? "" : roleLabel.trim();
        userLabel.setText(r.isEmpty() ? n : (n + " • " + r));
    }

    public JButton logoutButton() {
        return logout;
    }
}
