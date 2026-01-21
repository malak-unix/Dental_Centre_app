package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;
import ma.dentalTech.mvc.ui.common.UiAssets;
import ma.dentalTech.mvc.ui.common.UiStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginPanel extends JPanel {

    private final JTextField tfLogin = new JTextField();
    private final JPasswordField tfPassword = new JPasswordField();
    private final DentalButton btnLogin = new DentalButton("Connexion");
    private final JButton btnCancel = new DentalButton("Annuler");

    private final JLabel title = new JLabel("Connexion", SwingConstants.CENTER);

    public LoginPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(22);
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(0xF6, 0xF0, 0xE8));
        card.setBorder(new EmptyBorder(16, 22, 18, 22));
        card.setPreferredSize(new Dimension(680, 400));

        // Top: logo + separator
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel logo = new JLabel(UiAssets.loadIconFallback(
                "/assets/logo2.png",
                "src/main/resources/assets/logo2.png",
                360,
                120
        ));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

            logo.setText("DENTAL CENTER ");
            logo.setFont(new Font("Poppins", Font.BOLD, 24));
            logo.setForeground(DentalTheme.TEXT2);




        top.add(logo, BorderLayout.CENTER);
        top.add(new JSeparator(), BorderLayout.SOUTH);

        // Center: title + form
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(18, 10, 10, 10));

        title.setFont(new Font("Poppins", Font.BOLD, 30));
        title.setForeground(new Color(0x2A2A2A));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(title);
        center.add(Box.createVerticalStrut(18));

        center.add(buildRow("Login:", "@", tfLogin));
        center.add(Box.createVerticalStrut(12));
        center.add(buildRow("Mot de passe:", "*", tfPassword));

        // Bottom: logo left + buttons
        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setOpaque(false);
        bottomWrap.setBorder(new EmptyBorder(18, 0, 0, 0));





        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttons.setOpaque(false);

        UiStyles.stylePrimaryButton(btnLogin);
        UiStyles.styleSecondaryButton(btnCancel);
        btnLogin.setFont(DentalTheme.textBold(16));
        btnCancel.setFont(DentalTheme.textBold(16));
        btnLogin.setPreferredSize(new Dimension(240, 44));
        btnCancel.setPreferredSize(new Dimension(240, 44));

        buttons.add(btnLogin);
        buttons.add(btnCancel);


        bottomWrap.add(buttons, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(bottomWrap, BorderLayout.SOUTH);

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

    static class OutlineButton extends JButton {
        private boolean hover = false;

        OutlineButton(String text) {
            super(text);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setForeground(DentalTheme.PRIMARY_DARK);
            setFont(DentalTheme.textBold(16));
            setBorder(new EmptyBorder(10, 16, 10, 16));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = DentalTheme.BTN_RADIUS;
            int w = getWidth();
            int h = getHeight();

            Color bg = hover ? new Color(0xF3, 0xEC, 0xE6) : Color.WHITE;

            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(3, 3, w - 6, h - 6, arc, arc);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 6, h - 6, arc, arc);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(DentalTheme.STROKE);
            g2.drawRoundRect(0, 0, w - 6, h - 6, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
