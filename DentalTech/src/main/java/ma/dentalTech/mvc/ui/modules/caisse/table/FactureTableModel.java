package ma.dentalTech.mvc.ui.modules.caisse.table;

import ma.dentalTech.mvc.dto.caisse.CaisseFactureRowDTO;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FactureTableModel extends AbstractTableModel {

    private static final String[] COLS = {
            "N° Facture", "Patient", "Médecin", "Date", "Montant", "Statut", "Actions"
    };

    private final List<CaisseFactureRowDTO> rows = new ArrayList<>();
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setRows(List<CaisseFactureRowDTO> list) {
        rows.clear();
        if (list != null) rows.addAll(list);
        fireTableDataChanged();
    }

    public CaisseFactureRowDTO getRowAt(int row) {
        if (row < 0 || row >= rows.size()) return null;
        return rows.get(row);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLS.length; }
    @Override public String getColumnName(int column) { return COLS[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CaisseFactureRowDTO f = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> safe(f.getNumeroFacture());
            case 1 -> safe(f.getPatientNom());
            case 2 -> safe(f.getMedecinNom());
            case 3 -> formatDate(f.getDateEmission());
            case 4 -> money(f.getMontant());
            case 5 -> safe(f.getStatut());
            case 6 -> "⋯";
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 6;
    }

    private String safe(Object v) { return v == null ? "" : String.valueOf(v); }

    private String money(Double v) {
        double x = v == null ? 0.0 : v;
        return String.format("%,.2f DH", x);
    }

    private String formatDate(LocalDate d) {
        return d == null ? "" : d.format(df);
    }
}
