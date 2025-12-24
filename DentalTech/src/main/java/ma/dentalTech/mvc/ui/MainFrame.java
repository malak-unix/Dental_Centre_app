package ma.dentalTech.mvc.ui;

import ma.dentalTech.mvc.ui.common.UiTheme;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    public MainFrame() {
        super("DentalTech");
        UiTheme.install();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Left menu
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        menu.setPreferredSize(new Dimension(220, 0));

        JButton btnPatients = UiTheme.primaryButton("Patients");
        JButton btnRdv = UiTheme.primaryButton("RDV");
        JButton btnAgenda = UiTheme.primaryButton("Agenda");

        btnPatients.addActionListener(e -> cards.show(content, "PATIENTS"));
        btnRdv.addActionListener(e -> cards.show(content, "RDV"));
        btnAgenda.addActionListener(e -> cards.show(content, "AGENDA"));

        menu.add(UiTheme.title("Menu"));
        menu.add(Box.createVerticalStrut(12));
        menu.add(btnPatients);
        menu.add(Box.createVerticalStrut(8));
        menu.add(btnRdv);
        menu.add(Box.createVerticalStrut(8));
        menu.add(btnAgenda);
        menu.add(Box.createVerticalGlue());

        // Screens
        content.add(new PatientScreen(), "PATIENTS");
        content.add(new RdvScreen(), "RDV");
        content.add(new AgendaScreen(), "AGENDA");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menu, BorderLayout.WEST);
        getContentPane().add(content, BorderLayout.CENTER);

        cards.show(content, "PATIENTS");
    }
}
