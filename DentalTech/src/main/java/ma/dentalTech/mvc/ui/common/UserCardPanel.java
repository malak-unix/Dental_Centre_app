package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UserCardPanel extends CardPanel {

    private final JLabel roleLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel avatarLabel = new JLabel();

    public UserCardPanel(String roleText, String fullName) {
        super((String) null);
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);

        avatarLabel.setPreferredSize(new Dimension(54, 54));
        avatarLabel.setIcon(loadAvatarOrFallback(54, 54));
        left.add(avatarLabel, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        roleLabel.setFont(DentalTheme.textBold(12));
        roleLabel.setForeground(DentalTheme.TEXT2);

        nameLabel.setFont(DentalTheme.textFont(12));
        nameLabel.setForeground(DentalTheme.MUTED);

        info.add(roleLabel);
        info.add(Box.createVerticalStrut(2));
        info.add(nameLabel);

        add(left, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);

        setUser(roleText, fullName);
    }

    public void setUser(String roleText, String fullName) {
        roleLabel.setText(roleText != null ? roleText : "");
        nameLabel.setText(fullName != null ? fullName : "");
    }

    private Icon loadAvatarOrFallback(int w, int h) {
        try {
            URL url = getClass().getResource("/assets/avatar.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}

        // fallback (cercle)
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
}
