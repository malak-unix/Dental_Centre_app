package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.DentalButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class LoginPanel extends JPanel {

    private final JTextField tfLogin = new JTextField();
    private final JPasswordField tfPassword = new JPasswordField();
    private final DentalButton btnLogin = new DentalButton("Connexion");
    private final JButton btnCancel = new JButton("Annuler");

    private final JLabel title = new JLabel("Connexion", SwingConstants.CENTER);

    public LoginPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(22);
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(0xF6, 0xF0, 0xE8));
        card.setBorder(new EmptyBorder(16, 22, 18, 22));
        card.setPreferredSize(new Dimension(620, 340));

        // Top: logo + separator
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel logo = new JLabel(loadLogoIcon(260, 70));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        top.add(logo, BorderLayout.CENTER);
        top.add(new JSeparator(), BorderLayout.SOUTH);

        // Center: title + form
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(18, 10, 10, 10));

        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(new Color(0x2A2A2A));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(title);
        center.add(Box.createVerticalStrut(18));

        center.add(buildRow("Login:", "@", tfLogin));
        center.add(Box.createVerticalStrut(12));
        center.add(buildRow("Mot de passe:", "*", tfPassword));

        // Bottom: buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(18, 0, 0, 0));

        stylePrimary(btnLogin);
        styleSecondary(btnCancel);
        btnLogin.setFont(DentalTheme.textBold(16));
        btnLogin.setForeground(Color.WHITE);

        bottom.add(btnLogin);
        bottom.add(btnCancel);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        add(new ShadowWrapper(card));
    }

    private JPanel buildRow(String label, String icon, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(DentalTheme.textFont(16));
        l.setForeground(new Color(0x333333));
        l.setPreferredSize(new Dimension(140, 32));

        JPanel fieldWrap = new RoundedPanel(10);
        fieldWrap.setOpaque(true);
        fieldWrap.setBackground(Color.WHITE);
        fieldWrap.setBorder(new EmptyBorder(6, 10, 6, 10));
        fieldWrap.setLayout(new BorderLayout(10, 0));
        fieldWrap.setPreferredSize(new Dimension(440, 42));

        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        ic.setForeground(new Color(0x6B6B6B));

        if (field instanceof JTextField tf) {
            tf.setBorder(null);
            tf.setFont(DentalTheme.textFont(15));
            tf.setForeground(new Color(0x1F1F1F));
        }
        if (field instanceof JPasswordField pf) {
            pf.setBorder(null);
            pf.setFont(DentalTheme.textFont(15));
            pf.setForeground(new Color(0x1F1F1F));
        }

        fieldWrap.add(ic, BorderLayout.WEST);
        fieldWrap.add(field, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        right.setOpaque(false);
        right.add(fieldWrap);

        row.add(l, BorderLayout.WEST);
        row.add(right, BorderLayout.CENTER);
        return row;
    }

    private void stylePrimary(AbstractButton b) {
        b.setPreferredSize(new Dimension(240, 44));
        b.setFont(DentalTheme.textBold(16));
        b.setFocusPainted(false);
        if (b instanceof DentalButton) return;
        b.setBackground(new Color(0x12, 0x2B, 0x3B));
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x0C, 0x1D, 0x29), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
    }

    private void styleSecondary(AbstractButton b) {
        b.setPreferredSize(new Dimension(240, 44));
        b.setFont(DentalTheme.textBold(15));
        b.setFocusPainted(false);
        b.setBackground(new Color(0xF1, 0xE7, 0xDB));
        b.setForeground(DentalTheme.PRIMARY_DARK);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DentalTheme.STROKE, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
    }

    private Icon loadLogoIcon(int w, int h) {
        // logo: src/main/resources/assets/logo.png
        URL url = getClass().getResource("/assets/logo.png");
        if (url == null) {
            // fallback text
            JLabel fallback = new JLabel("DENTALTech CENTER", SwingConstants.CENTER);
            fallback.setFont(new Font("Serif", Font.BOLD, 24));
            fallback.setForeground(DentalTheme.TEXT);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c1 = new Color(0xF7, 0xF2, 0xEB);
        Color c2 = new Color(0xEC, 0xE1, 0xD1);
        g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
    // Getters
    public JTextField loginField() { return tfLogin; }
    public JPasswordField passwordField() { return tfPassword; }
    public JButton loginButton() { return btnLogin; }
    public JButton cancelButton() { return btnCancel; }

    // ---------- UI helpers ----------
    static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(DentalTheme.STROKE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class ShadowWrapper extends JPanel {
        private final JComponent content;
        ShadowWrapper(JComponent content) {
            this.content = content;
            setOpaque(false);
            setLayout(new GridBagLayout());
            add(content);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (getWidth() - content.getWidth()) / 2;
            int y = (getHeight() - content.getHeight()) / 2;

            g2.setColor(new Color(0, 0, 0, 35));
            Shape s = new RoundRectangle2D.Double(x + 6, y + 8, content.getWidth(), content.getHeight(), 22, 22);
            g2.fill(s);

            g2.dispose();

        }
    }
}


