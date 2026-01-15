package ma.dentalTech.mvc.ui.common.components;

import ma.dentalTech.mvc.ui.common.UiTokens;

import javax.swing.*;
import java.awt.*;

public class KpiCardPanel extends JPanel {

    private final JLabel valueLabel = new JLabel("-");
    private final JLabel titleLabel;

    public KpiCardPanel(String title) {
        setLayout(new BorderLayout(0, 8));
        setBackground(UiTokens.BG_CARD);
        setBorder(UiTokens.cardBorder());

        titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 20f));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value == null ? "-" : value);
    }
}
