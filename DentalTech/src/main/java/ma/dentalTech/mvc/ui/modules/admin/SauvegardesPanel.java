package ma.dentalTech.mvc.ui.modules.admin;

import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import java.awt.*;

public class SauvegardesPanel extends JPanel {

    public SauvegardesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel title = new JLabel("Sauvegardes Système", SwingConstants.CENTER);
        title.setFont(DentalTheme.titleFont(22));
        title.setForeground(DentalTheme.TEXT);

        JButton btn = new DentalButton("Lancer une sauvegarde manuelle");
        btn.setFont(DentalTheme.textFont(14));
        btn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sauvegarde simulée effectuée avec succès !"));

        JPanel box = new JPanel(new FlowLayout(FlowLayout.CENTER));
        box.setOpaque(false);
        box.add(btn);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.add(title, BorderLayout.NORTH);
        card.add(box, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }
}
