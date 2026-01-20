package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.function.Consumer;

public class AppHeaderPanel extends JPanel {

    private final JLabel logoLabel = new JLabel();

    private final JTextField searchField = new JTextField();
    private Consumer<String> onSearchChanged;
    private Consumer<String> onSearchSubmit;

    private final JLabel userLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();

    private final JButton logout = new JButton("Deconnexion");
    private final JButton searchBtn = new JButton();

    private static final String SEARCH_PLACEHOLDER = "Rechercher patient, RDV, facture...";

    public AppHeaderPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 76));
        setOpaque(true);

        // ✅ même couleur que la sidebar
        setBackground(DentalTheme.BG2);

        // LEFT logo
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 10));
        logoLabel.setIcon(loadLogoKeepRatio("/assets/logo.png", 150)); // ✅ net + ratio
        if (logoLabel.getIcon() == null) {
            logoLabel.setText("DENTAL CENTER");
            logoLabel.setFont(new Font("Serif", Font.BOLD, 20));
            logoLabel.setForeground(DentalTheme.TEXT2);
        }
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
        searchWrap.setPreferredSize(new Dimension(520, 40));

        Icon searchIcon = loadIcon("/assets/icons/search.png", 16, 16);
        if (searchIcon != null) {
            JLabel iconLabel = new JLabel(searchIcon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
            searchWrap.add(iconLabel, BorderLayout.WEST);
        }

        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setFont(DentalTheme.textFont(13));
        searchField.setOpaque(false);
        searchField.setForeground(DentalTheme.TEXT2);
        searchField.setCaretColor(DentalTheme.TEXT2);
        searchField.setToolTipText(SEARCH_PLACEHOLDER);
        installSearchPlaceholder();
        searchWrap.add(searchField, BorderLayout.CENTER);

        styleSearchButton();
        searchWrap.add(searchBtn, BorderLayout.EAST);

        center.add(searchWrap, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // RIGHT user + avatar + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 18));

        avatarLabel.setPreferredSize(new Dimension(38, 38));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(new Color(0xD9, 0xC6, 0xB5));
        avatarLabel.setForeground(DentalTheme.TEXT2);
        avatarLabel.setBorder(BorderFactory.createLineBorder(DentalTheme.STROKE, 2, true));

        userLabel.setFont(DentalTheme.textBold(12));
        userLabel.setForeground(DentalTheme.TEXT2);

        styleLogoutButton();

        right.add(avatarLabel);
        right.add(userLabel);
        right.add(logout);

        add(right, BorderLayout.EAST);

        // Search listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void fire() {
                if (onSearchChanged != null) onSearchChanged.accept(getSearchText());
            }
            @Override public void insertUpdate(DocumentEvent e) { fire(); }
            @Override public void removeUpdate(DocumentEvent e) { fire(); }
            @Override public void changedUpdate(DocumentEvent e) { fire(); }
        });

        searchField.addActionListener(e -> fireSearchSubmit());
        searchBtn.addActionListener(e -> fireSearchSubmit());
    }

    private void styleLogoutButton() {
        logout.setFocusable(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logout.setFont(DentalTheme.textBold(12));
        logout.setForeground(DentalTheme.TEXT2);

        Icon icon = loadIcon("/assets/icons/logout.png", 16, 16);
        if (icon != null) {
            logout.setIcon(icon);
            logout.setIconTextGap(8);
        }

        logout.setOpaque(true);
        logout.setBackground(Color.WHITE);
        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 2),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));

        Color normalBg = Color.WHITE;
        Color hoverBg = new Color(0xF3ECE6);

        logout.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { logout.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e) { logout.setBackground(normalBg); }
        });
    }

    private void styleSearchButton() {
        searchBtn.setFocusable(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setFont(DentalTheme.textBold(12));
        searchBtn.setForeground(DentalTheme.TEXT2);
        searchBtn.setText("GO");

        searchBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        searchBtn.setBackground(new Color(0xF3, 0xEC, 0xE6));
        searchBtn.setOpaque(true);
    }

    private void installSearchPlaceholder() {
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setForeground(DentalTheme.MUTED);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(DentalTheme.TEXT2);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText() == null || searchField.getText().isBlank()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(DentalTheme.MUTED);
                }
            }
        });
    }

    private Icon loadIcon(String path, int w, int h) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image img = scaleImage(icon.getImage(), w, h);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ logo net + ratio conservé
    private ImageIcon loadLogoKeepRatio(String path, int targetW) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            int ow = icon.getIconWidth();
            int oh = icon.getIconHeight();
            if (ow <= 0 || oh <= 0) return null;

            int targetH = Math.max(1, (int) Math.round((double) oh * targetW / ow));
            Image img = scaleImage(icon.getImage(), targetW, targetH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private Image scaleImage(Image src, int w, int h) {
        if (src == null) return null;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return image;
    }

    public void setUser(String fullName, String roleLabel) {
        String n = (fullName == null || fullName.isBlank()) ? "Utilisateur" : fullName.trim();
        String r = (roleLabel == null || roleLabel.isBlank()) ? "" : roleLabel.trim();
        userLabel.setText(r.isEmpty() ? n : (n + " - " + r));
        avatarLabel.setText(initials(n));
    }

    private String initials(String name) {
        if (name == null) return "U";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0) return "U";
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private void fireSearchSubmit() {
        if (onSearchSubmit != null) onSearchSubmit.accept(getSearchText());
    }

    private String getSearchText() {
        String t = searchField.getText();
        if (t == null) return "";
        if (SEARCH_PLACEHOLDER.equals(t)) return "";
        return t.trim();
    }

    public void onSearchChanged(Consumer<String> cb) { this.onSearchChanged = cb; }
    public void onSearchSubmit(Consumer<String> cb) { this.onSearchSubmit = cb; }

    public JButton logoutButton() { return logout; }
}
