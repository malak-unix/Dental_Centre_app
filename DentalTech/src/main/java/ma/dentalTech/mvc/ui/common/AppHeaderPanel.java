package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Consumer;

public class AppHeaderPanel extends JPanel {

    private final JLabel logoLabel = new JLabel();

    private final JTextField searchField = new JTextField();
    private Consumer<String> onSearchChanged;

    private final JLabel userLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();
    private final JButton logout = new JButton("⎋");

    public AppHeaderPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 76));
        setOpaque(true);
        setBackground(DentalTheme.BG_HEADER);

        // LEFT logo
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 10));
        logoLabel.setIcon(loadLogoIcon());
        add(logoLabel, BorderLayout.WEST);

        // CENTER search
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));

        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setOpaque(true);
        searchWrap.setBackground(Color.WHITE);
        searchWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel searchIcon = new JLabel("🔎");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchWrap.add(searchIcon, BorderLayout.WEST);

        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setFont(DentalTheme.textFont(13));
        searchField.setOpaque(false);
        searchField.setForeground(DentalTheme.TEXT2);
        searchField.setCaretColor(DentalTheme.TEXT2);
        searchField.setToolTipText("Rechercher...");
        searchWrap.add(searchField, BorderLayout.CENTER);

        center.add(searchWrap, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // RIGHT user + avatar + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 18));

        avatarLabel.setPreferredSize(new Dimension(38, 38));
        avatarLabel.setIcon(loadAvatarFallback(38, 38));

        userLabel.setFont(DentalTheme.textBold(12));
        userLabel.setForeground(DentalTheme.TEXT2);

        logout.setFocusable(false);
        logout.setBorderPainted(false);
        logout.setContentAreaFilled(false);
        logout.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        logout.setForeground(DentalTheme.TEXT2);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        right.add(avatarLabel);
        right.add(userLabel);
        right.add(logout);

        add(right, BorderLayout.EAST);

        // Search listener (non bloquant)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void fire() {
                if (onSearchChanged != null) onSearchChanged.accept(searchField.getText());
            }
            @Override public void insertUpdate(DocumentEvent e) { fire(); }
            @Override public void removeUpdate(DocumentEvent e) { fire(); }
            @Override public void changedUpdate(DocumentEvent e) { fire(); }
        });
    }

    private Icon loadLogoIcon() {
        java.net.URL url = getClass().getResource("/assets/logo.png");
        if (url == null) {
            logoLabel.setText("DENTAL CENTER");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 20));
            logoLabel.setForeground(DentalTheme.TEXT2);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(140, 46, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Icon loadAvatarFallback(int w, int h) {
        return new Icon() {
            @Override public int getIconWidth() { return w; }
            @Override public int getIconHeight() { return h; }
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xD9C6B5));
                g2.fillOval(x, y, w, h);
                g2.setColor(DentalTheme.STROKE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, w, h);
                g2.dispose();
            }
        };
    }

    public void setUser(String fullName, String roleLabel) {
        String n = (fullName == null || fullName.isBlank()) ? "Utilisateur" : fullName.trim();
        String r = (roleLabel == null || roleLabel.isBlank()) ? "" : roleLabel.trim();
        userLabel.setText(r.isEmpty() ? n : (n + " • " + r));
    }

    public JButton logoutButton() {
        return logout;
    }

    /** Permet de brancher la recherche globale (optionnel) */
    public void onSearchChanged(Consumer<String> listener) {
        this.onSearchChanged = listener;
    }

    /** Si une page veut vider la recherche */
    public void clearSearch() {
        searchField.setText("");
    }

    public String getSearchText() {
        return searchField.getText();
    }
}
