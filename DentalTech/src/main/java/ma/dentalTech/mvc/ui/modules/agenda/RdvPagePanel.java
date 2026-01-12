package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.mvc.ui.common.CardPanel;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class RdvPagePanel extends JPanel {

    private final RdvController controller;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Patient", "Date", "Heure", "Motif", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);

    // filtres style maquette
    private final PillButton bAll = new PillButton("Tous");
    private final PillButton bToday = new PillButton("Aujourd'hui");
    private final PillButton bUpcoming = new PillButton("À venir");

    // actions
    private final DentalButton btnAdd = new DentalButton("Ajouter");
    private final DentalButton btnEdit = new DentalButton("Modifier");
    private final DentalButton btnDelete = new DentalButton("Supprimer");

    public RdvPagePanel() {
        setLayout(new BorderLayout(12, 12));
        setOpaque(false);

        controller = (RdvController) ApplicationContext.getBean("rdv.controller");

        CardPanel card = new CardPanel((String) null);
        card.setLayout(new BorderLayout(14, 14));
        add(card, BorderLayout.CENTER);

        card.add(buildTop(), BorderLayout.NORTH);
        card.add(buildCenter(), BorderLayout.CENTER);
        card.add(buildBottom(), BorderLayout.SOUTH);

        wireActions();

        // initial
        setFilterSelected(bAll);
        refresh(safe(() -> controller.getAll()));
    }

    private JComponent buildTop() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Rendez-vous");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(DentalTheme.TEXT2);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);
        filters.setAlignmentX(Component.LEFT_ALIGNMENT);

        filters.add(bAll);
        filters.add(bToday);
        filters.add(bUpcoming);

        top.add(title);
        top.add(Box.createVerticalStrut(8));
        top.add(filters);

        return top;
    }

    private JComponent buildCenter() {
        CardPanel results = new CardPanel((String) null);
        results.setLayout(new BorderLayout());

        table.setRowHeight(28);
        table.setFont(DentalTheme.textFont(12));
        table.getTableHeader().setFont(DentalTheme.textBold(12));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        results.add(sp, BorderLayout.CENTER);

        return results;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        return bottom;
    }

    private void wireActions() {
        bAll.addActionListener(e -> {
            setFilterSelected(bAll);
            refresh(safe(() -> controller.getAll()));
        });

        bToday.addActionListener(e -> {
            setFilterSelected(bToday);
            refresh(safe(() -> controller.getByDate(LocalDate.now())));
        });

        bUpcoming.addActionListener(e -> {
            setFilterSelected(bUpcoming);
            refresh(safe(() -> controller.getUpcomingFromToday()));
        });

        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "À implémenter : creer(...) dans RdvController + Service.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnEdit.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "À implémenter : modifier(...) dans RdvController + Service.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnDelete.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "Supprimer le RDV #" + id + " ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (ok != JOptionPane.YES_OPTION) return;

            if (!invokeIfExists(controller, new String[]{"supprimer", "delete", "remove"},
                    new Class[]{Long.class}, new Object[]{id})) {

                JOptionPane.showMessageDialog(
                        this,
                        "Action non disponible.\nAjoute une méthode supprimer(Long id) dans RdvController + Service.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            // refresh selon filtre actif
            if (bToday.isPillSelected()) refresh(safe(() -> controller.getByDate(LocalDate.now())));
            else if (bUpcoming.isPillSelected()) refresh(safe(() -> controller.getUpcomingFromToday()));
            else refresh(safe(() -> controller.getAll()));
        });
    }

    private void refresh(List<RdvDto> list) {
        model.setRowCount(0);
        if (list == null) return;

        for (RdvDto r : list) {
            String patientAff = (r.getPatientNom() != null && !r.getPatientNom().isBlank())
                    ? r.getPatientNom()
                    : ("#" + r.getPatientId());

            model.addRow(new Object[]{
                    r.getId(),
                    patientAff,
                    r.getDateRdv(),
                    r.getHeure(),
                    r.getMotif(),
                    r.getStatut()
            });
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne d’abord.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object v = model.getValueAt(row, 0);
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    private interface SupplierX<T> { T get() throws Exception; }

    private <T> T safe(SupplierX<T> s) {
        try {
            if (controller == null) throw new IllegalStateException("Bean rdv.controller introuvable (beans.properties)");
            return s.get();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur RDV", JOptionPane.ERROR_MESSAGE);
            return (T) Collections.emptyList();
        }
    }

    private static boolean invokeIfExists(Object target, String[] names, Class<?>[] types, Object[] args) {
        if (target == null) return false;
        for (String n : names) {
            try {
                Method m = target.getClass().getMethod(n, types);
                m.invoke(target, args);
                return true;
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return false;
    }

    // ===== PillButton (sans override setSelected/isSelected)
    private static class PillButton extends JButton {
        private boolean pillSelected = false;

        PillButton(String text) {
            super(text);
            setFocusPainted(false);
            setFont(DentalTheme.textBold(12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DentalTheme.BORDER, 2, true),
                    BorderFactory.createEmptyBorder(7, 16, 7, 16)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPillSelected(false);
        }

        void setPillSelected(boolean v) {
            pillSelected = v;

            // Style
            if (pillSelected) {
                setBackground(DentalTheme.PRIMARY_DARK);
                setForeground(Color.WHITE);
            } else {
                setBackground(DentalTheme.BG);
                setForeground(DentalTheme.TEXT2);
            }
            setOpaque(true);
            repaint();
        }

        boolean isPillSelected() {
            return pillSelected;
        }
    }

    private void setFilterSelected(PillButton selected) {
        bAll.setPillSelected(selected == bAll);
        bToday.setPillSelected(selected == bToday);
        bUpcoming.setPillSelected(selected == bUpcoming);
    }
}
