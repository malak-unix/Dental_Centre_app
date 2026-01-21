package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public final class LogoutDialog extends JDialog {

    private boolean confirmed = false;

    private LogoutDialog(Window owner, String fullName) {
        super(owner, "Déconnexion", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        CardPanel card = new CardPanel("Déconnexion");
        card.setLayout(new BorderLayout(12, 12));
        card.setBackground(DentalTheme.CARD);

        String name = (fullName == null || fullName.isBlank()) ? "Utilisateur" : fullName.trim();
        JLabel msg = new JLabel("Au revoir Mme " + name + " !");
        msg.setFont(DentalTheme.textFont(14));
        msg.setForeground(DentalTheme.TEXT1);
        msg.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(msg, BorderLayout.CENTER);

        JButton btnOk = new DentalButton("Se déconnecter");
        JButton btnCancel = new DentalButton("Annuler");
        UiStyles.stylePrimaryButton(btnOk);
        UiStyles.styleSecondaryButton(btnCancel);

        btnOk.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(btnCancel);
        actions.add(btnOk);

        card.add(center, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
        pack();
        setSize(420, 220);
        setLocationRelativeTo(owner);
    }

    public static boolean confirm(Window owner, String fullName) {
        LogoutDialog dlg = new LogoutDialog(owner, fullName);
        dlg.setVisible(true);
        return dlg.confirmed;
    }
}
