package ma.dentalTech.mvc.ui.modules.caisse.table;

import ma.dentalTech.mvc.dto.caisse.ChargeItemDTO;
import ma.dentalTech.mvc.ui.common.DentalButton;
import ma.dentalTech.mvc.ui.common.DentalTheme;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;
import java.util.function.Consumer;

public final class ChargesActionsColumn {

    private ChargesActionsColumn() {}

    public static void install(JTable table,
                               Consumer<ChargeItemDTO> onEdit,
                               Consumer<ChargeItemDTO> onDelete) {

        int actionCol = findActionsColumnIndex(table);

        table.getColumnModel().getColumn(actionCol).setCellRenderer(new Renderer());
        table.getColumnModel().getColumn(actionCol).setCellEditor(new Editor(table, onEdit, onDelete));
        table.getColumnModel().getColumn(actionCol).setPreferredWidth(170);
    }

    private static int findActionsColumnIndex(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i);
            if (name != null && name.trim().equalsIgnoreCase("Actions")) return i;
        }
        return table.getColumnCount() - 1;
    }

    private static final class Renderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = mini("Modifier");
        private final JButton btnDelete = mini("Supprimer");

        Renderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setBackground(isSelected ? new Color(255, 245, 225) : Color.WHITE);
            return this;
        }
    }

    private static final class Editor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private final JButton btnEdit = mini("Modifier");
        private final JButton btnDelete = mini("Supprimer");

        private ChargeItemDTO current;

        Editor(JTable table,
               Consumer<ChargeItemDTO> onEdit,
               Consumer<ChargeItemDTO> onDelete) {

            panel.setOpaque(true);
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> { stopCellEditing(); if (current != null) onEdit.accept(current); });
            btnDelete.addActionListener(e -> { stopCellEditing(); if (current != null) onDelete.accept(current); });

            table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        }

        @Override public Object getCellEditorValue() { return current; }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            current = (value instanceof ChargeItemDTO) ? (ChargeItemDTO) value : null;
            panel.setBackground(new Color(255, 245, 225));
            return panel;
        }

        @Override public boolean isCellEditable(EventObject e) { return true; }
    }

    private static JButton mini(String text) {
        JButton b = new DentalButton(text);
        b.setFont(DentalTheme.textBold(11));
        b.setPreferredSize(new Dimension(78, 26));
        return b;
    }
}
