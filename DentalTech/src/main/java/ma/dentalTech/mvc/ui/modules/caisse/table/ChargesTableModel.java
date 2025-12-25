package ma.dentalTech.mvc.ui.modules.caisse.table;

import ma.dentalTech.mvc.dto.caisse.ChargeItemDTO;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChargesTableModel extends AbstractTableModel {

    private static final String[] COLS = {"Date", "Libellé", "Montant", "Actions"};

    private final List<ChargeItemDTO> rows = new ArrayList<>();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setRows(List<ChargeItemDTO> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        fireTableDataChanged();
    }

    public ChargeItemDTO getRowAt(int row) {
        if (row < 0 || row >= rows.size()) return null;
        return rows.get(row);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLS.length; }
    @Override public String getColumnName(int column) { return COLS[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ChargeItemDTO c = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> formatDate(c.getDateCharge());
            case 1 -> safe(c.getTitre());
            case 2 -> money(c.getMontant());
            case 3 -> "⋯";
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 3;
    }

    private String safe(Object v) { return v == null ? "" : String.valueOf(v); }

    private String money(BigDecimal v) {
        double x = (v == null) ? 0.0 : v.doubleValue();
        return String.format("%,.2f DH", x);
    }

    private String formatDate(LocalDateTime dt) {
        return dt == null ? "" : dt.format(df);
    }
}
