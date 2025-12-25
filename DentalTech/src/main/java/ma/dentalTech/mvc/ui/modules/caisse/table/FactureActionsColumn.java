package ma.dentalTech.mvc.ui.modules.caisse.table;

import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

public final class FactureActionsColumn {

    private FactureActionsColumn(){}

    public static void install(JTable table,
                               Consumer<CaisseFactureRowDTO> onView,
                               Consumer<CaisseFactureRowDTO> onPdf,
                               Consumer<CaisseFactureRowDTO> onPay,
                               Consumer<CaisseFactureRowDTO> onCancel) {

        int actionCol = findActionsColumnIndex(table);

        table.getColumnModel().getColumn(actionCol).setCellRenderer(new Renderer());
        table.getColumnModel().getColumn(actionCol).setCellEditor(new Editor(table, onView, onPdf, onPay, onCancel));
        table.getColumnModel().getColumn(actionCol).setPreferredWidth(240);
    }

    private static int findActionsColumnIndex(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i);
            if (name != null && name.trim().equalsIgnoreCase("Actions")) return i;
        }
        return table.getColumnCount() - 1; // fallback
    }

    private static final class Renderer extends JPanel implements TableCellRenderer {
        private final JButton btnView = mini("👁");
        private final JButton btnPdf  = mini("PDF");
        private final JButton btnPay  = mini("Payer");
        private final JButton btnCancel = mini("Ann");

        Renderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            add(btnView); add(btnPdf); add(btnPay); add(btnCancel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(255, 245, 225) : Color.WHITE);

            CaisseFactureRowDTO dto = (value instanceof CaisseFactureRowDTO) ? (CaisseFactureRowDTO) value : null;
            apply(dto);

            return this;
        }

        private void apply(CaisseFactureRowDTO dto) {
            btnView.setEnabled(dto != null && dto.isCanView());
            btnPdf.setEnabled(dto != null && dto.isCanPrint());
            btnPay.setEnabled(dto != null && dto.isCanPay());
            btnCancel.setEnabled(dto != null && dto.isCanCancel());
        }
    }

    private static final class Editor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private final JButton btnView = mini("👁");
        private final JButton btnPdf  = mini("PDF");
        private final JButton btnPay  = mini("Payer");
        private final JButton btnCancel = mini("Ann");

        private CaisseFactureRowDTO current;

        Editor(JTable table,
               Consumer<CaisseFactureRowDTO> onView,
               Consumer<CaisseFactureRowDTO> onPdf,
               Consumer<CaisseFactureRowDTO> onPay,
               Consumer<CaisseFactureRowDTO> onCancel) {

            panel.setOpaque(true);
            panel.add(btnView); panel.add(btnPdf); panel.add(btnPay); panel.add(btnCancel);

            btnView.addActionListener(e -> { stopCellEditing(); if (current != null) onView.accept(current); });
            btnPdf.addActionListener(e -> { stopCellEditing(); if (current != null) onPdf.accept(current); });
            btnPay.addActionListener(e -> { stopCellEditing(); if (current != null) onPay.accept(current); });
            btnCancel.addActionListener(e -> { stopCellEditing(); if (current != null) onCancel.accept(current); });

            table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        }

        @Override public Object getCellEditorValue() { return current; }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            current = (value instanceof CaisseFactureRowDTO) ? (CaisseFactureRowDTO) value : null;
            panel.setBackground(new Color(255, 245, 225));
            apply(current);
            return panel;
        }

        private void apply(CaisseFactureRowDTO dto) {
            btnView.setEnabled(dto != null && dto.isCanView());
            btnPdf.setEnabled(dto != null && dto.isCanPrint());
            btnPay.setEnabled(dto != null && dto.isCanPay());
            btnCancel.setEnabled(dto != null && dto.isCanCancel());
        }

        @Override public boolean isCellEditable(EventObject e) { return true; }
    }

    private static JButton mini(String text) {
        JButton b = new DentalButton(text);
        b.setFont(DentalTheme.textBold(11));
        b.setPreferredSize(new Dimension(64, 26));
        return b;
    }
}
